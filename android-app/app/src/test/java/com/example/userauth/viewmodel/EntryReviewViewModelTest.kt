package com.example.userauth.viewmodel

import com.example.userauth.data.api.dto.EntryDto
import com.example.userauth.data.repository.CompetitionRepository
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
class EntryReviewViewModelTest {

    private lateinit var viewModel: EntryReviewViewModel
    private lateinit var repository: CompetitionRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = EntryReviewViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        val entries = viewModel.entries.first()
        val isLoading = viewModel.isLoading.first()
        val error = viewModel.error.first()
        val updateSuccess = viewModel.updateSuccess.first()

        assertTrue(entries.isEmpty())
        assertFalse(isLoading)
        assertNull(error)
        assertFalse(updateSuccess)
    }

    @Test
    fun loadEntries_success_updatesState() = runTest {
        val entries = listOf(
            EntryDto(
                id = 1,
                entryName = "作品1",
                description = null,
                filePath = null,
                displayOrder = null,
                status = "PENDING",
                contestantId = null,
                contestantName = null,
                createdAt = "2024-01-01",
                updatedAt = null
            ),
            EntryDto(
                id = 2,
                entryName = "作品2",
                description = null,
                filePath = null,
                displayOrder = null,
                status = "PENDING",
                contestantId = null,
                contestantName = null,
                createdAt = "2024-01-02",
                updatedAt = null
            )
        )
        coEvery { repository.getCompetitionEntries(1) } returns Result.success(entries)

        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val entriesResult = viewModel.entries.first()
        val isLoading = viewModel.isLoading.first()

        assertEquals(2, entriesResult.size)
        assertEquals("作品1", entriesResult[0].entryName)
        assertFalse(isLoading)
    }

    @Test
    fun loadEntries_failure_setsError() = runTest {
        coEvery { repository.getCompetitionEntries(1) } returns Result.failure(Exception("Not found"))

        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.error.first()
        val isLoading = viewModel.isLoading.first()

        assertTrue(error?.contains("加载作品列表失败") == true)
        assertFalse(isLoading)
    }

    @Test
    fun updateEntryStatus_approved_removesFromList() = runTest {
        val entries = listOf(
            EntryDto(
                id = 1,
                entryName = "作品1",
                description = null,
                filePath = null,
                displayOrder = null,
                status = "PENDING",
                contestantId = null,
                contestantName = null,
                createdAt = "2024-01-01",
                updatedAt = null
            ),
            EntryDto(
                id = 2,
                entryName = "作品2",
                description = null,
                filePath = null,
                displayOrder = null,
                status = "PENDING",
                contestantId = null,
                contestantName = null,
                createdAt = "2024-01-02",
                updatedAt = null
            )
        )
        coEvery { repository.getCompetitionEntries(1) } returns Result.success(entries)
        coEvery { repository.updateEntryStatus(1, 1, "APPROVED") } returns Result.success(mockk())

        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateEntryStatus(1, 1, "APPROVED")
        testDispatcher.scheduler.advanceUntilIdle()

        val entriesResult = viewModel.entries.first()
        val updateSuccess = viewModel.updateSuccess.first()

        assertEquals(1, entriesResult.size)
        assertTrue(updateSuccess)
    }

    @Test
    fun updateEntryStatus_rejected_removesFromList() = runTest {
        val entries = listOf(
            EntryDto(
                id = 1,
                entryName = "作品1",
                description = null,
                filePath = null,
                displayOrder = null,
                status = "PENDING",
                contestantId = null,
                contestantName = null,
                createdAt = "2024-01-01",
                updatedAt = null
            )
        )
        coEvery { repository.getCompetitionEntries(1) } returns Result.success(entries)
        coEvery { repository.updateEntryStatus(1, 1, "REJECTED") } returns Result.success(mockk())

        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateEntryStatus(1, 1, "REJECTED")
        testDispatcher.scheduler.advanceUntilIdle()

        val entriesResult = viewModel.entries.first()
        assertTrue(entriesResult.isEmpty())
    }

    @Test
    fun updateEntryStatus_pending_reloadsEntries() = runTest {
        val entries = listOf(
            EntryDto(
                id = 1,
                entryName = "作品1",
                description = null,
                filePath = null,
                displayOrder = null,
                status = "PENDING",
                contestantId = null,
                contestantName = null,
                createdAt = "2024-01-01",
                updatedAt = null
            )
        )
        coEvery { repository.getCompetitionEntries(1) } returns Result.success(entries)
        coEvery { repository.updateEntryStatus(1, 1, "PENDING") } returns Result.success(mockk())

        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateEntryStatus(1, 1, "PENDING")
        testDispatcher.scheduler.advanceUntilIdle()

        val entriesResult = viewModel.entries.first()
        assertEquals(1, entriesResult.size)
    }

    @Test
    fun updateEntryStatus_failure_setsError() = runTest {
        coEvery { repository.updateEntryStatus(any(), any(), any()) } returns Result.failure(Exception("Update failed"))

        viewModel.updateEntryStatus(1, 1, "APPROVED")
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.error.first()
        val isLoading = viewModel.isLoading.first()

        assertTrue(error?.contains("更新状态失败") == true)
        assertFalse(isLoading)
    }

    @Test
    fun clearError_clearsErrorState() = runTest {
        coEvery { repository.getCompetitionEntries(1) } returns Result.failure(Exception("Error"))
        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val error = viewModel.error.first()
        assertNull(error)
    }

    @Test
    fun clearUpdateSuccess_clearsSuccessState() = runTest {
        coEvery { repository.getCompetitionEntries(1) } returns Result.success(emptyList())
        coEvery { repository.updateEntryStatus(any(), any(), any()) } returns Result.success(mockk())

        viewModel.loadEntries(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateEntryStatus(1, 1, "APPROVED")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearUpdateSuccess()

        val updateSuccess = viewModel.updateSuccess.first()
        assertFalse(updateSuccess)
    }
}
