package com.example.userauth.viewmodel

import com.example.userauth.data.api.EvaluationApiService
import com.example.userauth.data.api.dto.CompetitionDto
import com.example.userauth.data.model.ScoreParameter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreViewModelTest {

    private lateinit var viewModel: ScoreViewModel
    private lateinit var apiService: EvaluationApiService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        viewModel = ScoreViewModel(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        // Then
        val submissions = viewModel.submissions.first()
        val competitionName = viewModel.competitionName.first()
        assertTrue(submissions.isEmpty())
        assertEquals("", competitionName)
    }

    @Test
    fun loadCompetition_success_updatesName() = runTest {
        // Given
        val competitionId = 1L
        val competition = CompetitionDto(
            id = competitionId,
            name = "Test Competition",
            description = null,
            modelId = null,
            modelName = null,
            creatorId = null,
            creatorUsername = null,
            deadline = null,
            status = null,
            judges = null,
            entries = null,
            createdAt = null,
            updatedAt = null
        )
        coEvery { apiService.getCompetition(competitionId) } returns Response.success(competition)

        // When
        viewModel.loadCompetition(competitionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val name = viewModel.competitionName.first()
        assertEquals("Test Competition", name)
    }

    @Test
    fun loadCompetition_failure_setsError() = runTest {
        // Given
        val competitionId = 1L
        coEvery { apiService.getCompetition(competitionId) } throws Exception("Network error")

        // When
        viewModel.loadCompetition(competitionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val error = viewModel.errorMessage.first()
        assertNotNull(error)
        assertTrue(error!!.contains("Network error"))
    }

    @Test
    fun updateScore_changesSubmissionScore() = runTest {
        // Given - load initial data first
        val competitionId = 1L
        val competition = CompetitionDto(
            id = competitionId,
            name = "Test Competition",
            description = null,
            modelId = null,
            modelName = null,
            creatorId = null,
            creatorUsername = null,
            deadline = null,
            status = null,
            judges = null,
            entries = null,
            createdAt = null,
            updatedAt = null
        )
        coEvery { apiService.getCompetition(competitionId) } returns Response.success(competition)

        viewModel.loadCompetition(competitionId)
        testDispatcher.scheduler.advanceUntilIdle()

        val submissions = viewModel.submissions.first()
        assertTrue(submissions.isNotEmpty())

        val submissionId = submissions[0].id

        // When
        viewModel.updateScore(submissionId, "创新性", 8)

        // Then
        val updated = viewModel.submissions.first().find { it.id == submissionId }
        assertNotNull(updated)
        val score = updated?.scores?.find { it.name == "创新性" }
        assertEquals(8, score?.score)
    }

    @Test
    fun clearError_resetsErrorMessage() = runTest {
        // Given - trigger an error first
        val competitionId = 1L
        coEvery { apiService.getCompetition(competitionId) } throws Exception("Server error")
        viewModel.loadCompetition(competitionId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.errorMessage.first())

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.errorMessage.first())
    }
}
