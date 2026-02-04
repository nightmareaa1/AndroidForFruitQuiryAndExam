package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter

open class ScoreViewModel : ViewModel() {
    private val _submissions = MutableStateFlow<List<SubmissionScore>>(listOf(
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
    ))
    val submissions: StateFlow<List<SubmissionScore>> = _submissions.asStateFlow()

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
        // placeholder for backend submission
    }
}
