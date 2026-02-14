package com.example.userauth.viewmodel

import com.example.userauth.data.api.EvaluationApiService
import com.example.userauth.data.api.dto.CompetitionDto
import io.mockk.coEvery
import io.mockk.every
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
class CompetitionViewModelTest {

    private lateinit var viewModel: CompetitionViewModel
    private lateinit var apiService: EvaluationApiService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        coEvery { apiService.getCompetitions() } returns Response.success(emptyList())
        viewModel = CompetitionViewModel(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadCompetitions_success_updatesState() = runTest {
        val competitions = listOf(
            CompetitionDto(
                id = 1,
                name = "春季赛",
                description = null,
                modelId = 1,
                modelName = null,
                creatorId = 1,
                creatorUsername = null,
                deadline = "2024-12-31",
                status = "ACTIVE",
                judges = null,
                entries = null,
                createdAt = null,
                updatedAt = null
            )
        )
        coEvery { apiService.getCompetitions() } returns Response.success(competitions)

        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()

        val competitionsResult = viewModel.competitions.first()
        val isLoading = viewModel.isLoading.first()

        assertEquals(1, competitionsResult.size)
        assertFalse(isLoading)
    }

    @Test
    fun loadCompetitions_failure_setsError() = runTest {
        coEvery { apiService.getCompetitions() } returns Response.error(404, mockk(relaxed = true))

        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.errorMessage.first()
        val isLoading = viewModel.isLoading.first()

        assertTrue(error?.contains("加载赛事列表失败") == true)
        assertFalse(isLoading)
    }

    @Test
    fun loadCompetitions_exception_setsNetworkError() = runTest {
        coEvery { apiService.getCompetitions() } throws Exception("Network unavailable")

        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.errorMessage.first()
        val isLoading = viewModel.isLoading.first()

        assertTrue(error?.contains("网络错误") == true)
        assertFalse(isLoading)
    }

    @Test
    fun clearError_clearsErrorState() = runTest {
        coEvery { apiService.getCompetitions() } returns Response.error(404, mockk(relaxed = true))
        viewModel.loadCompetitions()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val error = viewModel.errorMessage.first()
        assertNull(error)
    }

    @Test
    fun competitionDto_toCompetition_mapsCorrectly() = runTest {
        val dto = CompetitionDto(
            id = 1,
            name = "春季赛",
            description = null,
            modelId = 2,
            modelName = null,
            creatorId = 3,
            creatorUsername = null,
            deadline = "2024-12-31T23:59:59",
            status = "ACTIVE",
            judges = null,
            entries = null,
            createdAt = null,
            updatedAt = null
        )

        val competition = dto.toCompetition()

        assertEquals(1, competition.id)
        assertEquals("春季赛", competition.name)
        assertEquals(2, competition.modelId)
        assertEquals(3, competition.creatorId)
        assertEquals("2024-12-31", competition.deadline)
        assertEquals("ACTIVE", competition.status)
    }

    @Test
    fun competitionDto_toCompetition_handlesNulls() = runTest {
        val dto = CompetitionDto(
            id = 1,
            name = "春季赛",
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

        val competition = dto.toCompetition()

        assertEquals(1, competition.id)
        assertEquals("春季赛", competition.name)
        assertEquals(0, competition.modelId)
        assertEquals(0, competition.creatorId)
        assertEquals("", competition.deadline)
        assertEquals("ACTIVE", competition.status)
    }
}
