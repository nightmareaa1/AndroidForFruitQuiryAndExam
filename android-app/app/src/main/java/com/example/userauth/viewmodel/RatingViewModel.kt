package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.dto.*
import com.example.userauth.data.repository.EvaluationModelRepository
import com.example.userauth.data.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RatingUiState(
    val competitionId: Long = 0,
    val entryId: Long = 0,
    val entryName: String = "",
    val evaluationModel: EvaluationModelDto? = null,
    val currentScores: Map<Long, Double> = emptyMap(),
    val note: String = "",
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val modelRepository: EvaluationModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

    fun loadRatingData(competitionId: Long, entryId: Long, entryName: String, modelId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Load evaluation model directly
                modelRepository.getModelById(modelId)
                    .onSuccess { model ->
                        _uiState.value = _uiState.value.copy(
                            competitionId = competitionId,
                            entryId = entryId,
                            entryName = entryName,
                            evaluationModel = model
                        )

                        // Load existing rating for this entry
                        loadExistingRating(competitionId, entryId)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            error = "加载评价模型失败: ${e.message}",
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载失败: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun loadExistingRating(competitionId: Long, entryId: Long) {
        viewModelScope.launch {
            ratingRepository.getMyRatings(competitionId)
                .onSuccess { ratings ->
                    val existingRating = ratings.find { it.entryId == entryId }
                    if (existingRating != null) {
                        _uiState.value = _uiState.value.copy(
                            currentScores = existingRating.scores?.associate {
                                it.parameterId to it.score
                            } ?: emptyMap(),
                            note = existingRating.note ?: "",
                            isSubmitted = true
                        )
                    }
                }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateScore(parameterId: Long, score: Double) {
        val currentScores = _uiState.value.currentScores.toMutableMap()
        currentScores[parameterId] = score
        _uiState.value = _uiState.value.copy(
            currentScores = currentScores,
            isSubmitted = false
        )
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(
            note = note,
            isSubmitted = false
        )
    }

    fun submitRating() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val state = _uiState.value
            val evaluationModel = state.evaluationModel

            if (evaluationModel == null) {
                _uiState.value = _uiState.value.copy(
                    error = "评价模型未加载",
                    isLoading = false
                )
                return@launch
            }

            // Validate all parameters have scores
            val missingParameters = evaluationModel.parameters.filter { param ->
                !state.currentScores.containsKey(param.id)
            }

            if (missingParameters.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    error = "请为所有评价参数打分",
                    isLoading = false
                )
                return@launch
            }

            // Prepare request
            val scoreRequests = state.currentScores.map { (parameterId, score) ->
                ScoreRequestDto(parameterId = parameterId, score = score)
            }

            val request = RatingRequestDto(
                competitionId = state.competitionId,
                entryId = state.entryId,
                scores = scoreRequests,
                note = state.note.ifBlank { null }
            )

            ratingRepository.submitRating(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSubmitted = true,
                        submissionSuccess = true
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "提交失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSubmissionSuccess() {
        _uiState.value = _uiState.value.copy(submissionSuccess = false)
    }
}