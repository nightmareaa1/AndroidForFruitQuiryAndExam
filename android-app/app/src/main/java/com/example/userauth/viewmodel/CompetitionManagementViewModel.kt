package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.userauth.data.model.Competition
import com.example.userauth.data.repository.CompetitionRepository
import javax.inject.Inject

@HiltViewModel
class CompetitionManagementViewModel @Inject constructor(
    private val repository: CompetitionRepository
) : ViewModel() {

    private val _competitions = MutableStateFlow<List<Competition>>(emptyList())
    val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()

    private val _selectedCompetition = MutableStateFlow<Competition?>(null)
    val selectedCompetition: StateFlow<Competition?> = _selectedCompetition.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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

    fun selectCompetition(competition: Competition?) {
        _selectedCompetition.value = competition
    }

    fun addCompetition(name: String, deadline: String, modelId: Long, description: String = "") {
        viewModelScope.launch {
            _isLoading.value = true

            repository.createCompetition(name, deadline, modelId, description)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun updateCompetition(updatedCompetition: Competition) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.updateCompetition(
                id = updatedCompetition.id,
                name = updatedCompetition.name,
                deadline = updatedCompetition.deadline,
                modelId = updatedCompetition.modelId,
                description = ""
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
}
