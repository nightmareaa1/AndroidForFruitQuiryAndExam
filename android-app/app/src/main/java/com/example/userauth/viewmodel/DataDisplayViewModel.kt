package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter
import com.example.userauth.data.api.EvaluationApiService
import kotlinx.coroutines.launch
import javax.inject.Inject

// Simple data-display view model that derives aggregates from ScoreViewModel data
class DataDisplayViewModel @Inject constructor(
    private val evaluationApiService: EvaluationApiService
) : ViewModel() {
    private val _submissions = MutableStateFlow<List<SubmissionScore>>(emptyList())
    val submissions: StateFlow<List<SubmissionScore>> = _submissions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Initialize with placeholder data
        _submissions.value = emptyList()
    }

    /**
     * Load submissions for data display
     */
    fun loadSubmissions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Load actual data from API when endpoints are available
                _submissions.value = emptyList()
            } catch (e: Exception) {
                _submissions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
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
