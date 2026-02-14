package com.example.userauth.data.model

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class DataModelTest {

    private val gson = Gson()

    @Test
    fun competition_serialization_roundTrip() {
        // Given
        val competition = Competition(
            id = 1,
            name = "Test Competition",
            description = "Test Description",
            deadline = "2026-12-31",
            modelId = 1,
            status = "ACTIVE",
            creatorId = 1
        )

        // When
        val json = gson.toJson(competition)
        val result = gson.fromJson(json, Competition::class.java)

        // Then
        assertEquals(competition.id, result.id)
        assertEquals(competition.name, result.name)
        assertEquals(competition.deadline, result.deadline)
        assertEquals(competition.status, result.status)
        assertEquals(competition.creatorId, result.creatorId)
    }

    @Test
    fun evaluationModel_serialization_roundTrip() {
        // Given
        val model = EvaluationModel(
            id = "1",
            name = "Fruit Model",
            description = "Test model",
            parameters = listOf(
                EvaluationParameter(id = "1", name = "Taste", weight = 40),
                EvaluationParameter(id = "2", name = "Appearance", weight = 30),
                EvaluationParameter(id = "3", name = "Texture", weight = 30)
            )
        )

        // When
        val json = gson.toJson(model)
        val result = gson.fromJson(json, EvaluationModel::class.java)

        // Then
        assertEquals(model.id, result.id)
        assertEquals(model.name, result.name)
        assertEquals(model.description, result.description)
        assertEquals(3, result.parameters.size)
    }

    @Test
    fun scoreParameter_serialization_roundTrip() {
        // Given
        val param = ScoreParameter(name = "Quality", max = 10, score = 7)

        // When
        val json = gson.toJson(param)
        val result = gson.fromJson(json, ScoreParameter::class.java)

        // Then
        assertEquals(param.name, result.name)
        assertEquals(param.max, result.max)
        assertEquals(param.score, result.score)
    }

    @Test
    fun submissionScore_serialization_roundTrip() {
        // Given
        val submission = SubmissionScore(
            id = "submission-1",
            contestant = "Contestant A",
            title = "Work A",
            imageUrl = "http://example.com/image.jpg",
            scores = mutableListOf(
                ScoreParameter(name = "创新性", max = 10, score = 8),
                ScoreParameter(name = "技术性", max = 10, score = 7)
            )
        )

        // When
        val json = gson.toJson(submission)
        val result = gson.fromJson(json, SubmissionScore::class.java)

        // Then
        assertEquals(submission.id, result.id)
        assertEquals(submission.contestant, result.contestant)
        assertEquals(submission.title, result.title)
        assertEquals(submission.imageUrl, result.imageUrl)
        assertEquals(2, result.scores.size)
    }

    @Test
    fun evaluationParameter_defaultValues() {
        // Given & When
        val param = EvaluationParameter(id = "1", name = "Test", weight = 50)

        // Then
        assertEquals(10, param.maxScore)
    }

    @Test
    fun scoreParameter_defaultScore() {
        // Given & When
        val param = ScoreParameter(name = "Test", max = 10)

        // Then
        assertEquals(0, param.score)
    }
}
