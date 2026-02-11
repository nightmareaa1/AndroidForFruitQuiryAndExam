package com.example.userauth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.dto.EntryDto
import com.example.userauth.data.local.PreferencesManager
import com.example.userauth.data.repository.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryEditUiState(
    val entries: List<EntryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: Long? = null,
    val isAdmin: Boolean = false,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

@HiltViewModel
class EntryEditViewModel @Inject constructor(
    private val competitionRepository: CompetitionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryEditUiState())
    val uiState: StateFlow<EntryEditUiState> = _uiState.asStateFlow()

    private var currentCompetitionId: Long = 0

    init {
        loadCurrentUserInfo()
    }

    private fun loadCurrentUserInfo() {
        val userId = preferencesManager.getUserId()
        val isAdmin = preferencesManager.isAdmin()
        _uiState.value = _uiState.value.copy(
            currentUserId = userId,
            isAdmin = isAdmin
        )
    }

    fun loadEntries(competitionId: Long, showOnlyMyEntries: Boolean = false) {
        currentCompetitionId = competitionId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            competitionRepository.getCompetitionEntries(competitionId)
                .onSuccess { entries ->
                    val filteredEntries = if (showOnlyMyEntries) {
                        entries.filter { it.contestantId == _uiState.value.currentUserId }
                    } else {
                        entries
                    }
                    _uiState.value = _uiState.value.copy(
                        entries = filteredEntries,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "加载作品列表失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun canEditEntry(entry: EntryDto): Boolean {
        val state = _uiState.value
        return state.isAdmin || (state.currentUserId != null && state.currentUserId == entry.contestantId)
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            competitionRepository.deleteEntry(currentCompetitionId, entryId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        entries = _uiState.value.entries.filter { it.id != entryId },
                        deleteSuccess = true,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "删除作品失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun updateEntry(
        entryId: Long,
        entryName: String,
        description: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            competitionRepository.updateEntry(
                currentCompetitionId,
                entryId,
                entryName,
                description,
                imageUri
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        updateSuccess = true,
                        isLoading = false
                    )
                    loadEntries(currentCompetitionId)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "更新作品失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false, deleteSuccess = false)
    }
}
