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
import kotlinx.coroutines.launch
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

    private val _selectedJudgeIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedJudgeIds: StateFlow<List<Long>> = _selectedJudgeIds.asStateFlow()

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

    fun clearSelectedJudges() {
        _selectedJudgeIds.value = emptyList()
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

    fun addJudgesToCompetition(competitionId: Long, judgeIds: List<Long>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.addJudges(competitionId, judgeIds)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }

    fun removeJudgeFromCompetition(competitionId: Long, judgeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.removeJudge(competitionId, judgeId)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
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
