package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter
import com.example.userauth.data.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for data display screen
 * Loads and displays competition rating data including entry scores and averages
 */
@HiltViewModel
class DataDisplayViewModel @Inject constructor(
    private val ratingRepository: RatingRepository
) : ViewModel() {
    private val _submissions = MutableStateFlow<List<SubmissionScore>>(emptyList())
    val submissions: StateFlow<List<SubmissionScore>> = _submissions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _modelId = MutableStateFlow<Long?>(null)
    val modelId: StateFlow<Long?> = _modelId.asStateFlow()

    private var currentCompetitionId: Long = 0

    init {
        _submissions.value = emptyList()
    }

    /**
     * Load rating data for a specific competition
     * @param competitionId The ID of the competition to load data for
     */
    fun loadSubmissions(competitionId: Long) {
        currentCompetitionId = competitionId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = ratingRepository.getCompetitionRatingData(competitionId)
                result.onSuccess { ratingData ->
                    _modelId.value = ratingData.modelId
                    _submissions.value = ratingData.entries.map { entry ->
                        SubmissionScore(
                            id = entry.entryId.toString(),
                            contestant = entry.contestantName,
                            title = entry.entryName,
                            scores = entry.parameterScores.map { param ->
                                ScoreParameter(
                                    name = param.parameterName,
                                    max = param.weight,
                                    score = param.averageScore.toInt()
                                )
                            }.toMutableList()
                        )
                    }
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load rating data"
                    _submissions.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                _submissions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh data for the current competition
     */
    fun refresh() {
        if (currentCompetitionId > 0) {
            loadSubmissions(currentCompetitionId)
        }
    }

    data class SubmissionAverage(
        val id: String,
        val contestant: String,
        val title: String,
        val average: Float
    )

    data class ParameterAverage(
        val name: String,
        val average: Float
    )

    // Average score per submission across its parameters
    fun averagePerSubmission(sub: SubmissionScore): Float {
        if (sub.scores.isEmpty()) return 0f
        val total = sub.scores.sumOf { it.score }
        return total.toFloat() / sub.scores.size
    }

    // All submissions' per-submission averages
    fun allSubmissionsAverages(): List<SubmissionAverage> {
        return submissions.value.map { s ->
            SubmissionAverage(s.id, s.contestant, s.title, averagePerSubmission(s))
        }
    }

    // Per-parameter averages across all submissions
    fun parameterAverages(): List<ParameterAverage> {
        val params = mutableSetOf<String>()
        val perSub = submissions.value.map { it.scores }
        perSub.forEach { list -> list.forEach { p -> params.add(p.name) } }
        return params.map { name ->
            val total = submissions.value.map { sub ->
                val m = sub.scores.find { it.name == name }
                m?.score ?: 0
            }.sum()
            val count = submissions.value.map { it.scores.find { it.name == name } ?: 0 }.size
            val avg = if (count > 0) total.toFloat() / count else 0f
            ParameterAverage(name, avg)
        }
    }
}
