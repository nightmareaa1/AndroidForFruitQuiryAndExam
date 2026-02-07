package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.EvaluationApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter
import javax.inject.Inject

open class ScoreViewModel @Inject constructor(
    private val evaluationApiService: EvaluationApiService
) : ViewModel() {
    
    private val _competitionName = MutableStateFlow("")
    val competitionName: StateFlow<String> = _competitionName.asStateFlow()
    
    private val _submissions = MutableStateFlow<List<SubmissionScore>>(emptyList())
    val submissions: StateFlow<List<SubmissionScore>> = _submissions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Load competition data including submissions and details
     */
    open fun loadCompetition(competitionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Fetch competition details
                val competitionResponse = evaluationApiService.getCompetition(competitionId)
                if (competitionResponse.isSuccessful) {
                    competitionResponse.body()?.let { competition ->
                        _competitionName.value = competition.name
                    }
                }
                
                // TODO: Fetch competition entries/submissions when API is available
                // For now, use placeholder data
                _submissions.value = listOf(
                    SubmissionScore(
                        id = UUID.randomUUID().toString(),
                        contestant = "选手A",
                        title = "作品A",
                        scores = mutableListOf(
                            ScoreParameter("创新性", 10, 5),
                            ScoreParameter("技术性", 10, 5),
                            ScoreParameter("可观感", 10, 5)
                        )
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = "加载赛事数据失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    open fun updateScore(submissionId: String, paramName: String, value: Int) {
        _submissions.value = _submissions.value.map { s ->
            if (s.id != submissionId) s
            else {
                val updated = s.scores.map { p -> if (p.name == paramName) p.copy(score = value) else p }.toMutableList()
                s.copy(scores = updated)
            }
        }
    }
    
    open fun submitScores(submissionId: String) {
        // Placeholder for backend submission
    }
    
    open fun clearError() {
        _errorMessage.value = null
    }
}
