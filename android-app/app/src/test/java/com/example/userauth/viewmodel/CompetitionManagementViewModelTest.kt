package com.example.userauth.viewmodel

import com.example.userauth.data.model.Competition
import com.example.userauth.data.repository.CompetitionRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class CompetitionManagementViewModelTest {

    private lateinit var viewModel: CompetitionManagementViewModel
    private lateinit var repository: CompetitionRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // Setup default mock before creating ViewModel since init calls loadCompetitions()
        coEvery { repository.getAllCompetitions() } returns Result.success(emptyList())
        viewModel = CompetitionManagementViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        // When - ViewModel init has already triggered loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val competitions = viewModel.competitions.first()
        assertTrue(competitions.isEmpty())
    }
    
    @Test
    fun loadCompetitions_success_updatesState() = runTest {
        // Given
        val competitions = listOf(
            Competition(id = 1, name = "Test Competition", deadline = "2026-12-31", modelId = 1, creatorId = 1, status = "ACTIVE"),
            Competition(id = 2, name = "Another Competition", deadline = "2026-11-30", modelId = 2, creatorId = 2, status = "ACTIVE")
        )
        coEvery { repository.getAllCompetitions() } returns Result.success(competitions)
        
        // When
        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val result = viewModel.competitions.first()
        assertEquals(2, result.size)
        assertEquals("Test Competition", result[0].name)
    }

    @Test
    fun loadCompetitions_failure_setsError() = runTest {
        // Given
        coEvery { repository.getAllCompetitions() } returns Result.failure(Exception("Network error"))
        
        // When
        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val error = viewModel.error.first()
        assertNotNull(error)
        assertEquals("Network error", error)
    }
    
    @Test
    fun selectCompetition_updatesSelectedCompetition() = runTest {
        // Given
        val competition = Competition(id = 1, name = "Test Competition", deadline = "2026-12-31", modelId = 1, creatorId = 1, status = "ACTIVE")
        
        // When
        viewModel.selectCompetition(competition)
        
        // Then
        val selected = viewModel.selectedCompetition.first()
        assertEquals(competition, selected)
    }
    
    @Test
    fun addCompetition_success_triggersReload() = runTest {
        // Given
        val createdCompetition = Competition(id = 1, name = "New Competition", deadline = "2026-12-31", modelId = 1, creatorId = 1, status = "ACTIVE")
        coEvery { repository.createCompetition(any(), any(), any(), any()) } returns Result.success(createdCompetition)
        coEvery { repository.getAllCompetitions() } returns Result.success(emptyList())
        
        // When
        viewModel.addCompetition("New Competition", "2026-12-31", 1, "Description")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { repository.createCompetition("New Competition", "2026-12-31", 1, "Description") }
        coVerify { repository.getAllCompetitions() }
    }
    
    @Test
    fun deleteCompetition_success_triggersReload() = runTest {
        // Given
        coEvery { repository.deleteCompetition(1) } returns Result.success(Unit)
        coEvery { repository.getAllCompetitions() } returns Result.success(emptyList())
        
        // When
        viewModel.deleteCompetition(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { repository.deleteCompetition(1) }
        coVerify { repository.getAllCompetitions() }
    }
    
    @Test
    fun getCompetitionById_returnsCorrectCompetition() = runTest {
        // Given
        val competitions = listOf(
            Competition(id = 1, name = "Competition 1", deadline = "2026-12-31", modelId = 1, creatorId = 1, status = "ACTIVE"),
            Competition(id = 2, name = "Competition 2", deadline = "2026-11-30", modelId = 2, creatorId = 2, status = "ACTIVE")
        )
        coEvery { repository.getAllCompetitions() } returns Result.success(competitions)
        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        val result = viewModel.getCompetitionById(1)
        
        // Then
        assertNotNull(result)
        assertEquals("Competition 1", result?.name)
    }
}
