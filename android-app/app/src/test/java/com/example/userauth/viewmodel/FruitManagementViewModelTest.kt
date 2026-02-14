package com.example.userauth.viewmodel

import com.example.userauth.data.model.Fruit
import com.example.userauth.data.model.FruitDataItem
import com.example.userauth.data.repository.FruitRepository
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
class FruitManagementViewModelTest {

    private lateinit var viewModel: FruitManagementViewModel
    private lateinit var repository: FruitRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getAllFruits() } returns Result.success(emptyList())
        viewModel = FruitManagementViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state.fruits.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun loadFruits_success_updatesState() = runTest {
        val fruits = listOf(
            Fruit(1, "苹果", "一种常见水果", null, emptyList(), emptyList()),
            Fruit(2, "香蕉", "热带水果", null, emptyList(), emptyList())
        )
        coEvery { repository.getAllFruits() } returns Result.success(fruits)

        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(2, state.fruits.size)
        assertEquals("苹果", state.fruits[0].name)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadFruits_failure_setsError() = runTest {
        coEvery { repository.getAllFruits() } returns Result.failure(Exception("Network error"))

        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("加载失败") == true)
        assertFalse(state.isLoading)
    }

    @Test
    fun selectFruit_updatesSelection() = runTest {
        val fruit = Fruit(1, "苹果", "描述", null, emptyList(), emptyList())

        viewModel.selectFruit(fruit)

        val state = viewModel.uiState.first()
        assertEquals("苹果", state.selectedFruit?.name)
    }

    @Test
    fun clearSelection_clearsSelection() = runTest {
        viewModel.selectFruit(Fruit(1, "苹果", "描述", null, emptyList(), emptyList()))

        viewModel.clearSelection()

        val state = viewModel.uiState.first()
        assertNull(state.selectedFruit)
    }

    @Test
    fun createFruit_success_updatesState() = runTest {
        coEvery { repository.createFruit("葡萄", "甜葡萄") } returns Result.success(mockk())

        viewModel.createFruit("葡萄", "甜葡萄")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("创建成功", state.success)
    }

    @Test
    fun createFruit_failure_setsError() = runTest {
        coEvery { repository.createFruit(any(), any()) } returns Result.failure(Exception("Already exists"))

        viewModel.createFruit("葡萄", "甜葡萄")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("创建失败") == true)
    }

    @Test
    fun updateFruit_success_updatesState() = runTest {
        coEvery { repository.updateFruit(1, "青苹果", "酸甜") } returns Result.success(mockk())

        viewModel.updateFruit(1, "青苹果", "酸甜")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("更新成功", state.success)
    }

    @Test
    fun updateFruit_failure_setsError() = runTest {
        coEvery { repository.updateFruit(any(), any(), any()) } returns Result.failure(Exception("Not found"))

        viewModel.updateFruit(1, "青苹果", "酸甜")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("更新失败") == true)
    }

    @Test
    fun deleteFruit_success_updatesState() = runTest {
        coEvery { repository.deleteFruit(1) } returns Result.success(mockk())

        viewModel.deleteFruit(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("删除成功", state.success)
    }

    @Test
    fun deleteFruit_failure_setsError() = runTest {
        coEvery { repository.deleteFruit(any()) } returns Result.failure(Exception("Cannot delete"))

        viewModel.deleteFruit(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("删除失败") == true)
    }

    @Test
    fun addNutritionData_success_updatesState() = runTest {
        coEvery { repository.addNutritionData(1, "维生素C", 10.0) } returns Result.success(mockk())

        viewModel.addNutritionData(1, "维生素C", 10.0)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("添加成功", state.success)
    }

    @Test
    fun addNutritionData_failure_setsError() = runTest {
        coEvery { repository.addNutritionData(any(), any(), any()) } returns Result.failure(Exception("Invalid data"))

        viewModel.addNutritionData(1, "维生素C", 10.0)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("添加失败") == true)
    }

    @Test
    fun addFlavorData_success_updatesState() = runTest {
        coEvery { repository.addFlavorData(1, "甜度", 8.5) } returns Result.success(mockk())

        viewModel.addFlavorData(1, "甜度", 8.5)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("添加成功", state.success)
    }

    @Test
    fun addFlavorData_failure_setsError() = runTest {
        coEvery { repository.addFlavorData(any(), any(), any()) } returns Result.failure(Exception("Invalid data"))

        viewModel.addFlavorData(1, "甜度", 8.5)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("添加失败") == true)
    }

    @Test
    fun deleteNutritionData_success_updatesState() = runTest {
        coEvery { repository.deleteNutritionData(1, 1) } returns Result.success(mockk())

        viewModel.deleteNutritionData(1, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("删除成功", state.success)
    }

    @Test
    fun deleteNutritionData_failure_setsError() = runTest {
        coEvery { repository.deleteNutritionData(any(), any()) } returns Result.failure(Exception("Not found"))

        viewModel.deleteNutritionData(1, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("删除失败") == true)
    }

    @Test
    fun deleteFlavorData_success_updatesState() = runTest {
        coEvery { repository.deleteFlavorData(1, 1) } returns Result.success(mockk())

        viewModel.deleteFlavorData(1, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("删除成功", state.success)
    }

    @Test
    fun deleteFlavorData_failure_setsError() = runTest {
        coEvery { repository.deleteFlavorData(any(), any()) } returns Result.failure(Exception("Not found"))

        viewModel.deleteFlavorData(1, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("删除失败") == true)
    }

    @Test
    fun clearError_clearsErrorState() = runTest {
        coEvery { repository.getAllFruits() } returns Result.failure(Exception("Error"))
        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.first()
        assertNull(state.error)
    }

    @Test
    fun clearSuccess_clearsSuccessState() = runTest {
        coEvery { repository.createFruit(any(), any()) } returns Result.success(mockk())
        viewModel.createFruit("葡萄", "描述")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearSuccess()

        val state = viewModel.uiState.first()
        assertNull(state.success)
    }
}
