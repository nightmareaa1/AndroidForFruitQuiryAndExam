package com.example.userauth.data.repository

import android.content.Context
import android.net.Uri
import com.example.userauth.data.api.CompetitionApi
import com.example.userauth.data.api.dto.CompetitionDto
import com.example.userauth.data.api.dto.EntryDto
import com.example.userauth.data.api.dto.EntryRequestDto
import com.example.userauth.data.api.dto.EntrySubmitResponseDto
import com.example.userauth.data.model.Competition
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CompetitionRepositoryTest {

    private lateinit var repository: CompetitionRepository
    private lateinit var api: CompetitionApi
    private lateinit var context: Context

    @Before
    fun setup() {
        api = mockk()
        context = mockk(relaxed = true)
        repository = CompetitionRepository(api, context)
    }

    @Test
    fun getAllCompetitions_success_returnsCompetitions() = runTest {
        // Given
        val competitions = listOf(
            CompetitionDto(1, "赛事A", null, 1, null, 1, null, "2026-12-31", "ACTIVE", null, null, null, null),
            CompetitionDto(2, "赛事B", null, 2, null, 2, null, "2026-11-30", "ACTIVE", null, null, null, null)
        )
        coEvery { api.getAllCompetitions() } returns Response.success(competitions)

        // When
        val result = repository.getAllCompetitions()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("赛事A", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun getAllCompetitions_failure_returnsError() = runTest {
        // Given
        coEvery { api.getAllCompetitions() } returns Response.error(500, okhttp3.ResponseBody.create(null, "Error"))

        // When
        val result = repository.getAllCompetitions()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Failed to fetch competitions") == true)
    }

    @Test
    fun getAllCompetitions_exception_returnsFailure() = runTest {
        // Given
        coEvery { api.getAllCompetitions() } throws Exception("Network error")

        // When
        val result = repository.getAllCompetitions()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getCompetitionById_success_returnsCompetition() = runTest {
        // Given
        val competition = CompetitionDto(1, "赛事A", null, 1, null, 1, null, "2026-12-31", "ACTIVE", null, null, null, null)
        coEvery { api.getCompetitionById(1) } returns Response.success(competition)

        // When
        val result = repository.getCompetitionById(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("赛事A", result.getOrNull()?.name)
    }

    @Test
    fun getCompetitionById_emptyBody_returnsError() = runTest {
        // Given
        coEvery { api.getCompetitionById(1) } returns Response.success(null)

        // When
        val result = repository.getCompetitionById(1)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Empty response", result.exceptionOrNull()?.message)
    }

    @Test
    fun createCompetition_success_returnsCompetition() = runTest {
        // Given
        val competition = CompetitionDto(1, "新赛事", null, 1, null, 1, null, "2026-12-31T23:59:59", "ACTIVE", null, null, null, null)
        coEvery { api.createCompetition(any()) } returns Response.success(competition)

        // When
        val result = repository.createCompetition("新赛事", "2026-12-31", 1, "描述")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("新赛事", result.getOrNull()?.name)
    }

    @Test
    fun createCompetition_failure_returnsError() = runTest {
        // Given
        coEvery { api.createCompetition(any()) } returns Response.error(400, okhttp3.ResponseBody.create(null, "Bad Request"))

        // When
        val result = repository.createCompetition("", "", 0, "")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun updateCompetition_success_returnsCompetition() = runTest {
        // Given
        val competition = CompetitionDto(1, "更新赛事", null, 1, null, 1, null, "2026-12-31T23:59:59", "ACTIVE", null, null, null, null)
        coEvery { api.updateCompetition(any(), any()) } returns Response.success(competition)

        // When
        val result = repository.updateCompetition(1, "更新赛事", "2026-12-31", 1, "新描述")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("更新赛事", result.getOrNull()?.name)
    }

    @Test
    fun deleteCompetition_success_returnsUnit() = runTest {
        // Given
        coEvery { api.deleteCompetition(1) } returns Response.success(null)

        // When
        val result = repository.deleteCompetition(1)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun deleteCompetition_failure_returnsError() = runTest {
        // Given
        coEvery { api.deleteCompetition(1) } returns Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"))

        // When
        val result = repository.deleteCompetition(1)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getCompetitionEntries_success_returnsEntries() = runTest {
        // Given
        val entries = listOf(
            EntryDto(1, "作品A", null, null, 1, "ACTIVE", null, null, null, null),
            EntryDto(2, "作品B", null, null, 2, "ACTIVE", null, null, null, null)
        )
        coEvery { api.getCompetitionEntries(1) } returns Response.success(entries)

        // When
        val result = repository.getCompetitionEntries(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun updateEntryStatus_success_returnsUnit() = runTest {
        // Given
        coEvery { api.updateEntryStatus(any(), any(), any()) } returns Response.success(null)

        // When
        val result = repository.updateEntryStatus(1, 100, "APPROVED")

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.updateEntryStatus(1, 100, any()) }
    }

    @Test
    fun deleteEntry_success_returnsUnit() = runTest {
        // Given
        coEvery { api.deleteEntry(any(), any()) } returns Response.success(null)

        // When
        val result = repository.deleteEntry(1, 100)

        // Then
        assertTrue(result.isSuccess)
    }
}
