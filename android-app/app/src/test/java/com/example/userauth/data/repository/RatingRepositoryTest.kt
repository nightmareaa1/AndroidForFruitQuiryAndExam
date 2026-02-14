package com.example.userauth.data.repository

import com.example.userauth.data.api.RatingApi
import com.example.userauth.data.api.dto.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RatingRepositoryTest {

    private lateinit var repository: RatingRepository
    private lateinit var api: RatingApi

    @Before
    fun setup() {
        api = mockk()
        repository = RatingRepository(api)
    }

    @Test
    fun submitRating_success_returnsRating() = runTest {
        // Given
        val request = RatingRequestDto(1, 100, listOf(ScoreRequestDto(1, 8.5)), "很好")
        val response = RatingResponseDto(1, 1, 100, "作品A", 1, "评委1", null, "很好", "2026-01-01")
        coEvery { api.submitRating(any()) } returns Response.success(response)

        // When
        val result = repository.submitRating(request)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull()?.id)
        coVerify { api.submitRating(request) }
    }

    @Test
    fun submitRating_emptyBody_returnsError() = runTest {
        // Given
        val request = RatingRequestDto(1, 100, emptyList(), null)
        coEvery { api.submitRating(any()) } returns Response.success(null)

        // When
        val result = repository.submitRating(request)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Empty response", result.exceptionOrNull()?.message)
    }

    @Test
    fun submitRating_failure_returnsError() = runTest {
        // Given
        val request = RatingRequestDto(1, 100, emptyList(), null)
        coEvery { api.submitRating(any()) } returns Response.error(400, okhttp3.ResponseBody.create(null, "Bad Request"))

        // When
        val result = repository.submitRating(request)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getRatingsByEntry_success_returnsRatings() = runTest {
        // Given
        val ratings = listOf(
            RatingResponseDto(1, 1, 100, "作品A", 1, "评委1", null, null, null),
            RatingResponseDto(2, 1, 100, "作品A", 2, "评委2", null, null, null)
        )
        coEvery { api.getRatingsByEntry(100) } returns Response.success(ratings)

        // When
        val result = repository.getRatingsByEntry(100)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun getRatingsByCompetition_success_returnsRatings() = runTest {
        // Given
        val ratings = listOf(
            RatingResponseDto(1, 1, 100, "作品A", 1, "评委1", null, null, null)
        )
        coEvery { api.getRatingsByCompetition(1) } returns Response.success(ratings)

        // When
        val result = repository.getRatingsByCompetition(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun getMyRatings_success_returnsRatings() = runTest {
        // Given
        val ratings = listOf(
            RatingResponseDto(1, 1, 100, "作品A", 1, "评委1", null, null, null)
        )
        coEvery { api.getMyRatings(1) } returns Response.success(ratings)

        // When
        val result = repository.getMyRatings(1)

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.getMyRatings(1) }
    }

    @Test
    fun getRatingCompletionStatus_completed_returnsTrue() = runTest {
        // Given
        coEvery { api.getRatingCompletionStatus(100) } returns Response.success(RatingCompletionResponseDto(true))

        // When
        val result = repository.getRatingCompletionStatus(100)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun getRatingCompletionStatus_notCompleted_returnsFalse() = runTest {
        // Given
        coEvery { api.getRatingCompletionStatus(100) } returns Response.success(RatingCompletionResponseDto(false))

        // When
        val result = repository.getRatingCompletionStatus(100)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun getCompetitionRatingData_success_returnsData() = runTest {
        // Given
        val entries = listOf(
            EntryRatingDataDto(100, "作品A", "选手A", null, 85.5, 3, emptyList())
        )
        val data = CompetitionRatingDataResponseDto(1, "赛事A", 1, entries)
        coEvery { api.getCompetitionRatingData(1) } returns Response.success(data)

        // When
        val result = repository.getCompetitionRatingData(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("赛事A", result.getOrNull()?.competitionName)
        assertEquals(1, result.getOrNull()?.entries?.size)
    }

    @Test
    fun getCompetitionRatingData_emptyBody_returnsError() = runTest {
        // Given
        coEvery { api.getCompetitionRatingData(1) } returns Response.success(null)

        // When
        val result = repository.getCompetitionRatingData(1)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Empty response", result.exceptionOrNull()?.message)
    }

    @Test
    fun submitRating_exception_returnsFailure() = runTest {
        // Given
        val request = RatingRequestDto(1, 100, emptyList(), null)
        coEvery { api.submitRating(any()) } throws Exception("Network error")

        // When
        val result = repository.submitRating(request)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
