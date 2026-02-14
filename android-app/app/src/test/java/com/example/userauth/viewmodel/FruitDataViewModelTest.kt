package com.example.userauth.viewmodel

import com.example.userauth.data.api.DataTypeInfo
import com.example.userauth.data.api.FruitDataAdminApi
import com.example.userauth.data.api.FruitDataResponse
import com.example.userauth.data.api.FruitOption
import com.example.userauth.data.repository.FruitDataRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class FruitDataViewModelTest {

    private lateinit var viewModel: FruitDataViewModel
    private lateinit var repository: FruitDataRepository
    private lateinit var adminApi: FruitDataAdminApi
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        adminApi = mockk(relaxed = true)
        coEvery { adminApi.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }
        coEvery { repository.getFruits() } returns Result.success(emptyList())
        viewModel = FruitDataViewModel(repository, adminApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state.dataTypes.isEmpty())
        assertTrue(state.fruits.isEmpty())
        assertTrue(state.queryResults.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun loadDataTypes_success_updatesState() = runTest {
        // Given
        val dataTypes = listOf(
            DataTypeInfo("营养成分", 10, 8),
            DataTypeInfo("风味特征", 5, 5)
        )
        coEvery { adminApi.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns dataTypes
        }

        // When
        viewModel.loadDataTypes()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.dataTypes.size)
        assertEquals("营养成分", state.dataTypes[0])
    }

    @Test
    fun loadFruits_success_updatesState() = runTest {
        // Given
        val fruits = listOf(
            FruitOption(1, "苹果"),
            FruitOption(2, "香蕉")
        )
        coEvery { repository.getFruits() } returns Result.success(fruits)

        // When
        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.fruits.size)
        assertEquals("苹果", state.fruits[0].name)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadFruits_failure_setsError() = runTest {
        // Given
        coEvery { repository.getFruits() } returns Result.failure(Exception("Network error"))

        // When
        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("加载水果列表失败") == true)
        assertFalse(state.isLoading)
    }

    @Test
    fun selectFruit_updatesSelection() = runTest {
        // Given
        val fruit = FruitOption(1, "苹果")

        // When
        viewModel.selectFruit(fruit)

        // Then
        val state = viewModel.uiState.first()
        assertEquals("苹果", state.selectedFruit?.name)
    }

    @Test
    fun selectFruit_null_clearsSelection() = runTest {
        // Given
        viewModel.selectFruit(FruitOption(1, "苹果"))

        // When
        viewModel.selectFruit(null)

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.selectedFruit)
    }

    @Test
    fun query_success_updatesState() = runTest {
        // Given
        val response = FruitDataResponse(
            fruitName = "苹果",
            dataType = "营养成分",
            data = mapOf("维生素C" to 10.0, "糖分" to 12.0)
        )
        coEvery { repository.query("苹果", "营养成分") } returns Result.success(response)

        // When
        viewModel.query("苹果", "营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(1, state.queryResults.size)
        assertEquals("苹果", state.queryResults[0].fruitName)
        assertFalse(state.isLoading)
    }

    @Test
    fun query_failure_setsError() = runTest {
        // Given
        coEvery { repository.query(any(), any()) } returns Result.failure(Exception("Not found"))

        // When
        viewModel.query("苹果", "营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("查询失败") == true)
        assertFalse(state.isLoading)
    }

    @Test
    fun clearError_clearsErrorState() = runTest {
        // Given
        coEvery { repository.getFruits() } returns Result.failure(Exception("Error"))
        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.error)
    }
}
