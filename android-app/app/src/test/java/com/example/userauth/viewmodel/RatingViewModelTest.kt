package com.example.userauth.viewmodel

import com.example.userauth.data.api.dto.EvaluationModelDto
import com.example.userauth.data.api.dto.EvaluationParameterDto
import com.example.userauth.data.api.dto.RatingRequestDto
import com.example.userauth.data.api.dto.RatingResponseDto
import com.example.userauth.data.repository.EvaluationModelRepository
import com.example.userauth.data.repository.RatingRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class RatingViewModelTest {

    private lateinit var viewModel: RatingViewModel
    private lateinit var ratingRepository: RatingRepository
    private lateinit var modelRepository: EvaluationModelRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        ratingRepository = mockk()
        modelRepository = mockk()
        viewModel = RatingViewModel(ratingRepository, modelRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        val state = viewModel.uiState.first()
        assertEquals(0L, state.competitionId)
        assertEquals(0L, state.entryId)
        assertEquals("", state.entryName)
        assertTrue(state.currentScores.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isSubmitted)
        assertNull(state.error)
    }

    @Test
    fun loadRatingData_success_updatesState() = runTest {
        // Given
        val model = EvaluationModelDto(1, "Model A", null, listOf(
            EvaluationParameterDto(1, "Quality", 50),
            EvaluationParameterDto(2, "Taste", 50)
        ))
        coEvery { modelRepository.getModelById(1) } returns Result.success(model)
        coEvery { ratingRepository.getMyRatings(1) } returns Result.success(emptyList())

        // When
        viewModel.loadRatingData(1, 100, "Entry A", 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(1L, state.competitionId)
        assertEquals(100L, state.entryId)
        assertEquals("Entry A", state.entryName)
        assertEquals(2, state.evaluationModel?.parameters?.size)
    }

    @Test
    fun loadRatingData_failure_setsError() = runTest {
        // Given
        coEvery { modelRepository.getModelById(1) } returns Result.failure(Exception("Model not found"))

        // When
        viewModel.loadRatingData(1, 100, "Entry A", 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("加载评价模型失败") == true)
    }

    @Test
    fun updateScore_updatesCurrentScores() = runTest {
        viewModel.updateScore(1, 8.5)

        val state = viewModel.uiState.first()
        assertEquals(8.5, state.currentScores[1])
        assertFalse(state.isSubmitted)
    }

    @Test
    fun updateNote_updatesNote() = runTest {
        viewModel.updateNote("Good entry")

        val state = viewModel.uiState.first()
        assertEquals("Good entry", state.note)
        assertFalse(state.isSubmitted)
    }

    @Test
    fun submitRating_noModel_showsError() = runTest {
        // Given - no model loaded
        viewModel.updateScore(1, 8.0)

        // When
        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("评价模型未加载", state.error)
    }

    @Test
    fun submitRating_missingScores_showsError() = runTest {
        // Given - model loaded but not all scores
        val model = EvaluationModelDto(1, "Model A", null, listOf(
            EvaluationParameterDto(1, "Quality", 50),
            EvaluationParameterDto(2, "Taste", 50)
        ))
        coEvery { modelRepository.getModelById(1) } returns Result.success(model)
        coEvery { ratingRepository.getMyRatings(1) } returns Result.success(emptyList())
        
        viewModel.loadRatingData(1, 100, "Entry A", 1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Only set one score
        viewModel.updateScore(1, 8.0)

        // When
        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("请为所有评价参数打分") == true)
    }

    @Test
    fun submitRating_success_updatesState() = runTest {
        // Given
        val model = EvaluationModelDto(1, "Model A", null, listOf(
            EvaluationParameterDto(1, "Quality", 50),
            EvaluationParameterDto(2, "Taste", 50)
        ))
        coEvery { modelRepository.getModelById(1) } returns Result.success(model)
        coEvery { ratingRepository.getMyRatings(1) } returns Result.success(emptyList())
        coEvery { ratingRepository.submitRating(any()) } returns Result.success(
            RatingResponseDto(1, 1, 100, "Entry A", 1, "Judge", null, "Good", "2026-01-01")
        )
        
        viewModel.loadRatingData(1, 100, "Entry A", 1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Set all scores
        viewModel.updateScore(1, 8.0)
        viewModel.updateScore(2, 9.0)

        // When
        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.submissionSuccess)
        assertTrue(state.isSubmitted)
    }

    @Test
    fun clearError_clearsErrorMessage() = runTest {
        // Given
        viewModel.submitRating() // Triggers error due to no model

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.error)
    }

    @Test
    fun clearSubmissionSuccess_clearsFlag() = runTest {
        // Given
        val model = EvaluationModelDto(1, "Model A", null, listOf(
            EvaluationParameterDto(1, "Quality", 50)
        ))
        coEvery { modelRepository.getModelById(1) } returns Result.success(model)
        coEvery { ratingRepository.getMyRatings(1) } returns Result.success(emptyList())
        coEvery { ratingRepository.submitRating(any()) } returns Result.success(
            RatingResponseDto(1, 1, 100, "Entry A", 1, "Judge", null, null, null)
        )
        
        viewModel.loadRatingData(1, 100, "Entry A", 1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateScore(1, 8.0)
        viewModel.submitRating()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearSubmissionSuccess()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.submissionSuccess)
    }
}
