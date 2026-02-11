package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.dto.EntryDto
import com.example.userauth.data.repository.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryReviewViewModel @Inject constructor(
    private val competitionRepository: CompetitionRepository
) : ViewModel() {

    private val _entries = MutableStateFlow<List<EntryDto>>(emptyList())
    val entries: StateFlow<List<EntryDto>> = _entries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun loadEntries(competitionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            competitionRepository.getCompetitionEntries(competitionId)
                .onSuccess { entries ->
                    _entries.value = entries
                    _isLoading.value = false
                }
                .onFailure { e ->
                    _error.value = "加载作品列表失败: ${e.message}"
                    _isLoading.value = false
                }
        }
    }

    fun updateEntryStatus(competitionId: Long, entryId: Long, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            competitionRepository.updateEntryStatus(competitionId, entryId, status)
                .onSuccess {
                    _updateSuccess.value = true
                    if (status == "APPROVED" || status == "REJECTED") {
                        _entries.value = _entries.value.filter { it.id != entryId }
                    } else {
                        loadEntries(competitionId)
                    }
                    _isLoading.value = false
                }
                .onFailure { e ->
                    _error.value = "更新状态失败: ${e.message}"
                    _isLoading.value = false
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }
}