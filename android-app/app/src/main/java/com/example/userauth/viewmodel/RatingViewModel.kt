package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.dto.*
import com.example.userauth.data.model.EvaluationParameter
import com.example.userauth.data.repository.CompetitionRepository
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
    val competitionName: String = "",
    val entries: List<CompetitionEntryDto> = emptyList(),
    val evaluationModel: EvaluationModelDto? = null,
    val currentEntryIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val submissionSuccess: Boolean = false
)

data class EntryRatingState(
    val entryId: Long,
    val entryName: String,
    val scores: Map<Long, Double>,
    val note: String,
    val isSubmitted: Boolean = false
)

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val competitionRepository: CompetitionRepository,
    private val modelRepository: EvaluationModelRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()
    
    private val _entryRatings = MutableStateFlow<Map<Long, EntryRatingState>>(emptyMap())
    val entryRatings: StateFlow<Map<Long, EntryRatingState>> = _entryRatings.asStateFlow()
    
    fun loadCompetitionForRating(competitionId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load competition details with entries
                competitionRepository.getCompetitionById(competitionId)
                    .onSuccess { competition ->
                        _uiState.value = _uiState.value.copy(
                            competitionId = competitionId,
                            competitionName = competition.name
                        )
                        
                        // Load evaluation model
                        competition.modelId?.let { modelId ->
                            modelRepository.getModelById(modelId)
                                .onSuccess { model ->
                                    _uiState.value = _uiState.value.copy(
                                        evaluationModel = model
                                    )
                                }
                        }
                        
                        // Load existing ratings for this judge
                        loadExistingRatings(competitionId)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            error = "加载赛事数据失败: ${e.message}",
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
    
    private fun loadExistingRatings(competitionId: Long) {
        viewModelScope.launch {
            ratingRepository.getMyRatings(competitionId)
                .onSuccess { ratings ->
                    // Convert existing ratings to EntryRatingState
                    val existingRatings = ratings.associate { rating ->
                        rating.entryId to EntryRatingState(
                            entryId = rating.entryId,
                            entryName = rating.entryName ?: "",
                            scores = rating.scores?.associate { 
                                it.parameterId to it.score 
                            } ?: emptyMap(),
                            note = rating.note ?: "",
                            isSubmitted = true
                        )
                    }
                    _entryRatings.value = existingRatings
                }
            
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun updateScore(entryId: Long, parameterId: Long, score: Double) {
        val currentRatings = _entryRatings.value.toMutableMap()
        val currentEntryRating = currentRatings[entryId] ?: EntryRatingState(
            entryId = entryId,
            entryName = "",
            scores = emptyMap(),
            note = ""
        )
        
        val updatedScores = currentEntryRating.scores.toMutableMap()
        updatedScores[parameterId] = score
        
        currentRatings[entryId] = currentEntryRating.copy(
            scores = updatedScores,
            isSubmitted = false
        )
        
        _entryRatings.value = currentRatings
    }
    
    fun updateNote(entryId: Long, note: String) {
        val currentRatings = _entryRatings.value.toMutableMap()
        val currentEntryRating = currentRatings[entryId] ?: EntryRatingState(
            entryId = entryId,
            entryName = "",
            scores = emptyMap(),
            note = ""
        )
        
        currentRatings[entryId] = currentEntryRating.copy(
            note = note,
            isSubmitted = false
        )
        
        _entryRatings.value = currentRatings
    }
    
    fun submitRating(entryId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val entryRating = _entryRatings.value[entryId]
            if (entryRating == null) {
                _uiState.value = _uiState.value.copy(
                    error = "未找到评分数据",
                    isLoading = false
                )
                return@launch
            }
            
            // Validate all parameters have scores
            val evaluationModel = _uiState.value.evaluationModel
            if (evaluationModel == null) {
                _uiState.value = _uiState.value.copy(
                    error = "未加载评价模型",
                    isLoading = false
                )
                return@launch
            }
            
            val missingParameters = evaluationModel.parameters.filter { param ->
                !entryRating.scores.containsKey(param.id)
            }
            
            if (missingParameters.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    error = "请为所有评价参数打分",
                    isLoading = false
                )
                return@launch
            }
            
            // Prepare request
            val scoreRequests = entryRating.scores.map { (parameterId, score) ->
                ScoreRequestDto(parameterId = parameterId, score = score)
            }
            
            val request = RatingRequestDto(
                competitionId = _uiState.value.competitionId,
                entryId = entryId,
                scores = scoreRequests,
                note = entryRating.note.ifBlank { null }
            )
            
            ratingRepository.submitRating(request)
                .onSuccess { response ->
                    // Mark as submitted
                    val currentRatings = _entryRatings.value.toMutableMap()
                    currentRatings[entryId] = entryRating.copy(isSubmitted = true)
                    _entryRatings.value = currentRatings
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
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
    
    fun setCurrentEntry(index: Int) {
        _uiState.value = _uiState.value.copy(
            currentEntryIndex = index,
            submissionSuccess = false,
            error = null
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSubmissionSuccess() {
        _uiState.value = _uiState.value.copy(submissionSuccess = false)
    }
}
