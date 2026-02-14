package com.example.userauth.viewmodel

import android.net.Uri
import com.example.userauth.data.api.dto.EntrySubmitResponseDto
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
class EntryAddViewModelTest {

    private lateinit var viewModel: EntryAddViewModel
    private lateinit var repository: CompetitionRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = EntryAddViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        val state = viewModel.uiState.first()
        assertEquals(0L, state.competitionId)
        assertEquals("", state.entryName)
        assertEquals("", state.description)
        assertNull(state.imageUri)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.submissionSuccess)
    }

    @Test
    fun setCompetitionId_updatesState() = runTest {
        viewModel.setCompetitionId(123)

        val state = viewModel.uiState.first()
        assertEquals(123L, state.competitionId)
    }

    @Test
    fun updateEntryName_updatesState() = runTest {
        viewModel.updateEntryName("My Entry")

        val state = viewModel.uiState.first()
        assertEquals("My Entry", state.entryName)
    }

    @Test
    fun updateDescription_updatesState() = runTest {
        viewModel.updateDescription("A beautiful entry")

        val state = viewModel.uiState.first()
        assertEquals("A beautiful entry", state.description)
    }

    @Test
    fun updateImageUri_updatesState() = runTest {
        val uri = mockk<Uri>()
        viewModel.updateImageUri(uri)

        val state = viewModel.uiState.first()
        assertEquals(uri, state.imageUri)
    }

    @Test
    fun submitEntry_emptyName_showsValidationError() = runTest {
        viewModel.setCompetitionId(1)
        viewModel.submitEntry()

        val state = viewModel.uiState.first()
        assertEquals("参赛作品名称不能为空", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun submitEntry_success_updatesState() = runTest {
        // Given
        viewModel.setCompetitionId(1)
        viewModel.updateEntryName("Test Entry")
        
        val response = EntrySubmitResponseDto(100, "Success")
        coEvery { repository.submitEntry(any(), any(), any(), any()) } returns Result.success(response)

        // When
        viewModel.submitEntry()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.submissionSuccess)
        assertNull(state.error)
    }

    @Test
    fun submitEntry_failure_updatesError() = runTest {
        // Given
        viewModel.setCompetitionId(1)
        viewModel.updateEntryName("Test Entry")
        
        coEvery { repository.submitEntry(any(), any(), any(), any()) } returns Result.failure(Exception("Network error"))

        // When
        viewModel.submitEntry()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.submissionSuccess)
        assertEquals("Network error", state.error)
    }

    @Test
    fun clearError_clearsErrorMessage() = runTest {
        // Given
        viewModel.setCompetitionId(1)
        viewModel.submitEntry() // Triggers validation error

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.error)
    }

    @Test
    fun reset_clearsFormButKeepsCompetitionId() = runTest {
        // Given
        viewModel.setCompetitionId(5)
        viewModel.updateEntryName("Test Entry")
        viewModel.updateDescription("Description")

        // When
        viewModel.reset()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(5L, state.competitionId)
        assertEquals("", state.entryName)
        assertEquals("", state.description)
    }
}
