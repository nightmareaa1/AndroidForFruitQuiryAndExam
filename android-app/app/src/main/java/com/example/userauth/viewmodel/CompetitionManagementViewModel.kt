package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.model.Competition
import com.example.userauth.data.repository.CompetitionRepository
import com.example.userauth.data.repository.UserRepository
import com.example.userauth.data.api.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class CompetitionManagementViewModel @Inject constructor(
    private val repository: CompetitionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _competitions = MutableStateFlow<List<Competition>>(emptyList())
    val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()

    private val _selectedCompetition = MutableStateFlow<Competition?>(null)
    val selectedCompetition: StateFlow<Competition?> = _selectedCompetition.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users.asStateFlow()

    private val _existingJudgeIds = MutableStateFlow<List<Long>>(emptyList())
    val existingJudgeIds: StateFlow<List<Long>> = _existingJudgeIds.asStateFlow()

    private val _selectedJudgeIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedJudgeIds: StateFlow<List<Long>> = _selectedJudgeIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCompetitions()
    }

    fun loadCompetitions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getAllCompetitions()
                .onSuccess { competitions ->
                    _competitions.value = competitions
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }

    fun loadCompetitionDetail(competitionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // 确保用户数据先加载
            if (_users.value.isEmpty()) {
                loadUsers()
                // 等待用户数据加载完成
                delay(500) // 简单等待一下
            }

            // 检查用户是否已登录
            println("[DEBUG] 检查用户登录状态")
            // TODO: 这里可以添加登录状态检查

            // 添加调试日志
            println("[DEBUG] 正在调用repository.getCompetitionById(competitionId=$competitionId)")
            repository.getCompetitionById(competitionId)
                .onSuccess { competitionDto ->
                    val comp = Competition(
                        id = competitionDto.id,
                        name = competitionDto.name,
                        modelId = competitionDto.modelId ?: 0L,
                        creatorId = competitionDto.creatorId ?: 0L,
                        deadline = competitionDto.deadline?.substring(0, 10) ?: "",
                        status = competitionDto.status ?: "ACTIVE",
                        description = competitionDto.description ?: "",
                        judges = competitionDto.judges?.map { it.userId } ?: emptyList()
                    )
                    _selectedCompetition.value = comp
                    _existingJudgeIds.value = comp.judges.toMutableList()
                    _selectedJudgeIds.value = emptyList()
                }
                .onFailure { e ->
                    // 添加详细的错误日志
                    println("[ERROR] getCompetitionById调用失败: ${e.message}")
                    e.printStackTrace()
                    _error.value = "加载赛事详情失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.getAllUsers()
                .onSuccess { users ->
                    _users.value = users
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val filteredUsers: StateFlow<List<UserDto>> = combine(users, searchQuery) { users, query ->
        if (query.isBlank()) {
            users
        } else {
            users.filter { user ->
                user.username?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectCompetition(competition: Competition?) {
        _selectedCompetition.value = competition
    }

    fun setSelectedJudgeIds(judgeIds: List<Long>) {
        _selectedJudgeIds.value = judgeIds
    }

    fun toggleJudgeSelection(userId: Long) {
        val current = _selectedJudgeIds.value.toMutableList()
        if (current.contains(userId)) {
            current.remove(userId)
        } else {
            current.add(userId)
        }
        _selectedJudgeIds.value = current
    }

    fun removeExistingJudge(competitionId: Long, judgeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.removeJudge(competitionId, judgeId)
                .onSuccess {
                    val current = _existingJudgeIds.value.toMutableList()
                    current.remove(judgeId)
                    _existingJudgeIds.value = current
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }

    fun addNewJudges(competitionId: Long) {
        val judgeIdsToAdd = _selectedJudgeIds.value
        if (judgeIdsToAdd.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.addJudges(competitionId, judgeIdsToAdd)
                .onSuccess {
                    val current = _existingJudgeIds.value.toMutableList()
                    current.addAll(judgeIdsToAdd)
                    _existingJudgeIds.value = current
                    _selectedJudgeIds.value = emptyList()
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }
    
    fun addNewJudges(competitionId: Long, judgeIds: List<Long>) {
        if (judgeIds.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.addJudges(competitionId, judgeIds)
                .onSuccess {
                    val current = _existingJudgeIds.value.toMutableList()
                    current.addAll(judgeIds)
                    _existingJudgeIds.value = current
                    _selectedJudgeIds.value = emptyList()
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }

    fun clearSelectedJudges() {
        _selectedJudgeIds.value = emptyList()
        _existingJudgeIds.value = emptyList()
        _searchQuery.value = ""
    }

    fun addJudgeForCreation(judgeId: Long) {
        val current = _existingJudgeIds.value.toMutableList()
        if (!current.contains(judgeId)) {
            current.add(judgeId)
            _existingJudgeIds.value = current
        }
    }

    fun removeJudgeForCreation(judgeId: Long) {
        val current = _existingJudgeIds.value.toMutableList()
        current.remove(judgeId)
        _existingJudgeIds.value = current
    }

    fun addCompetition(
        name: String,
        deadline: String,
        modelId: Long,
        description: String = "",
        judgeIds: List<Long>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.createCompetition(name, deadline, modelId, description, judgeIds)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun updateCompetition(updatedCompetition: Competition, judgeIds: List<Long>? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.updateCompetition(
                id = updatedCompetition.id,
                name = updatedCompetition.name,
                description = updatedCompetition.description,
                deadline = updatedCompetition.deadline,
                modelId = updatedCompetition.modelId,
                judgeIds = judgeIds
            )
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun deleteCompetition(id: Long) {
        viewModelScope.launch {
            repository.deleteCompetition(id)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun getCompetitionById(id: Long): Competition? {
        return _competitions.value.find { it.id == id }
    }

    fun clearError() {
        _error.value = null
    }
}
