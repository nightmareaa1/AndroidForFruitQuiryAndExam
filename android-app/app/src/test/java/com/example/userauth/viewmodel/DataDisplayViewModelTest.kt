package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import com.example.userauth.data.model.ScoreParameter
import com.example.userauth.data.model.SubmissionScore

class DataDisplayViewModelTest {
    private fun calculateAverage(sub: SubmissionScore): Float {
        if (sub.scores.isEmpty()) return 0f
        val total = sub.scores.sumOf { it.score }
        return total.toFloat() / sub.scores.size
    }

    private fun calculateParameterAverages(submissions: List<SubmissionScore>): List<Pair<String, Float>> {
        val params = mutableSetOf<String>()
        submissions.forEach { sub -> sub.scores.forEach { params.add(it.name) } }
        return params.map { name ->
            val total = submissions.map { sub ->
                sub.scores.find { it.name == name }?.score ?: 0
            }.sum()
            val count = submissions.map { sub -> sub.scores.find { it.name == name } }.size
            val avg = if (count > 0) total.toFloat() / count else 0f
            name to avg
        }
    }

    @Test
    fun initialSubmissionsAverageAndParameterAverages() {
        val submissions = listOf(
            SubmissionScore(
                id = "sub-1",
                contestant = "选手A",
                title = "作品A",
                scores = mutableListOf(
                    ScoreParameter("创新性", 10, 5),
                    ScoreParameter("技术性", 10, 5),
                    ScoreParameter("可观感", 10, 5)
                )
            )
        )

        // Test average calculation
        val averages = submissions.map { sub -> sub.id to calculateAverage(sub) }
        assertEquals(1, averages.size)
        assertEquals(5.0f, averages[0].second, 0.001f)

        // Test parameter average calculation
        val paramAverages = calculateParameterAverages(submissions)
        assertEquals(3, paramAverages.size)
        assertEquals("创新性", paramAverages[0].first)
        assertEquals(5.0f, paramAverages[0].second, 0.001f)
    }

    @Test
    fun emptySubmissionsReturnsEmptyAverages() {
        val submissions = emptyList<SubmissionScore>()
        val averages = submissions.map { sub -> sub.id to calculateAverage(sub) }
        assertEquals(0, averages.size)

        val paramAverages = calculateParameterAverages(submissions)
        assertEquals(0, paramAverages.size)
    }
}
