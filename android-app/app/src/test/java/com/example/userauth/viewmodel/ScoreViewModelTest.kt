package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import com.example.userauth.data.model.ScoreParameter
import com.example.userauth.data.model.SubmissionScore

class ScoreViewModelTest {
    @Test
    fun initialSubmissionHasScores() {
        val submissions = listOf(
            SubmissionScore(
                id = "test-id-1",
                contestant = "选手A",
                title = "作品A",
                scores = mutableListOf(
                    ScoreParameter("创新性", 10, 5),
                    ScoreParameter("技术性", 10, 5),
                    ScoreParameter("可观感", 10, 5)
                )
            )
        )
        assertEquals(1, submissions.size)
        assertEquals(3, submissions[0].scores.size)
    }

    @Test
    fun updateScoreChangesValue() {
        val submissions = listOf(
            SubmissionScore(
                id = "test-id-1",
                contestant = "选手A",
                title = "作品A",
                scores = mutableListOf(
                    ScoreParameter("创新性", 10, 5),
                    ScoreParameter("技术性", 10, 5)
                )
            )
        )

        // Update score using immutable approach
        val updatedSubmissions = submissions.map { sub ->
            if (sub.id == "test-id-1") {
                val updatedScores = sub.scores.map { param ->
                    if (param.name == "创新性") ScoreParameter(param.name, param.max, 9) else param
                }
                SubmissionScore(sub.id, sub.contestant, sub.title, mutableListOf<ScoreParameter>().apply { addAll(updatedScores) })
            } else sub
        }

        val updated = updatedSubmissions.first()
        val p = updated.scores.find { it.name == "创新性" }
        assertNotNull(p)
        assertEquals(9, p!!.score)
    }
}
