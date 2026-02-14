package com.example.userauth.viewmodel

import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import com.example.userauth.data.repository.EvaluationModelRepository
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
class ModelViewModelTest {

    private lateinit var viewModel: ModelViewModel
    private lateinit var repository: EvaluationModelRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // Setup default mock before creating ViewModel since init calls loadModels()
        coEvery { repository.getAllModels() } returns Result.success(emptyList())
        viewModel = ModelViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        // When - ViewModel init has already triggered loadModels()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val models = viewModel.models.first()
        assertTrue(models.isEmpty())
    }

    @Test
    fun loadModels_success_updatesState() = runTest {
        // Given
        val models = listOf(
            EvaluationModel(id = "1", name = "Model A", parameters = emptyList()),
            EvaluationModel(id = "2", name = "Model B", parameters = emptyList())
        )
        coEvery { repository.getAllModels() } returns Result.success(models)

        // When
        viewModel.loadModels()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.models.first()
        assertEquals(2, result.size)
        assertEquals("Model A", result[0].name)
    }

    @Test
    fun loadModels_failure_setsError() = runTest {
        // Given
        coEvery { repository.getAllModels() } returns Result.failure(Exception("Network error"))

        // When
        viewModel.loadModels()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val error = viewModel.error.first()
        assertNotNull(error)
        assertEquals("Network error", error)
    }

    @Test
    fun selectModel_updatesSelectedModel() = runTest {
        // Given
        val model = EvaluationModel(id = "1", name = "Model A", parameters = emptyList())

        // When
        viewModel.selectModel(model)

        // Then
        val selected = viewModel.selectedModel.first()
        assertEquals(model, selected)
    }

    @Test
    fun addModel_success_triggersReload() = runTest {
        // Given
        val parameters = listOf(
            EvaluationParameter(id = "1", name = "Quality", weight = 50),
            EvaluationParameter(id = "2", name = "Taste", weight = 50)
        )
        val createdModel = EvaluationModel(id = "1", name = "New Model", parameters = parameters)
        coEvery { repository.createModel(any(), any()) } returns Result.success(createdModel)
        coEvery { repository.getAllModels() } returns Result.success(emptyList())

        // When
        viewModel.addModel("New Model", parameters)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.createModel("New Model", parameters) }
        coVerify { repository.getAllModels() }
    }

    @Test
    fun deleteModel_success_triggersReload() = runTest {
        // Given
        coEvery { repository.deleteModel(1) } returns Result.success(Unit)
        coEvery { repository.getAllModels() } returns Result.success(emptyList())

        // When
        viewModel.deleteModel("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.deleteModel(1) }
        coVerify { repository.getAllModels() }
    }

    @Test
    fun getModelById_returnsCorrectModel() = runTest {
        // Given
        val models = listOf(
            EvaluationModel(id = "1", name = "Model 1", parameters = emptyList()),
            EvaluationModel(id = "2", name = "Model 2", parameters = emptyList())
        )
        coEvery { repository.getAllModels() } returns Result.success(models)
        viewModel.loadModels()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val result = viewModel.getModelById("1")

        // Then
        assertNotNull(result)
        assertEquals("Model 1", result?.name)
    }
}
