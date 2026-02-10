package com.example.userauth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.repository.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryAddUiState(
    val competitionId: Long = 0,
    val entryName: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class EntryAddViewModel @Inject constructor(
    private val competitionRepository: CompetitionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EntryAddUiState())
    val uiState: StateFlow<EntryAddUiState> = _uiState.asStateFlow()
    
    fun setCompetitionId(competitionId: Long) {
        _uiState.value = _uiState.value.copy(competitionId = competitionId)
    }
    
    fun updateEntryName(name: String) {
        _uiState.value = _uiState.value.copy(entryName = name, error = null)
    }
    
    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }
    
    fun updateImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }
    
    fun submitEntry() {
        val state = _uiState.value
        
        // Validate
        if (state.entryName.isBlank()) {
            _uiState.value = state.copy(error = "参赛作品名称不能为空")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            
            val result = competitionRepository.submitEntry(
                competitionId = state.competitionId,
                entryName = state.entryName.trim(),
                description = state.description.trim().ifBlank { null },
                imageUri = state.imageUri
            )
            
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissionSuccess = true
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "提交失败"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun reset() {
        _uiState.value = EntryAddUiState(competitionId = _uiState.value.competitionId)
    }
}
