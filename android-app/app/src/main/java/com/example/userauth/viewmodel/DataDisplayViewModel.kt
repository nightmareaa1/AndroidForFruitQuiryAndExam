package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter

// Simple data-display view model that derives aggregates from ScoreViewModel data
class DataDisplayViewModel(private val scoreViewModel: ScoreViewModel = ScoreViewModel()) : ViewModel() {
    val submissions: StateFlow<List<SubmissionScore>> = scoreViewModel.submissions

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
