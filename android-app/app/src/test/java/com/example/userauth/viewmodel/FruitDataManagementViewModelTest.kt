package com.example.userauth.viewmodel

import android.net.Uri
import com.example.userauth.data.api.DataTypeInfo
import com.example.userauth.data.api.FruitAdminApi
import com.example.userauth.data.api.FruitDataAdminApi
import com.example.userauth.data.api.FruitDataEntity
import com.example.userauth.data.api.FruitOption
import com.example.userauth.data.api.FieldOption
import com.example.userauth.data.repository.FruitDataAdminRepository
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
class FruitDataManagementViewModelTest {

    private lateinit var viewModel: FruitDataManagementViewModel
    private lateinit var api: FruitDataAdminApi
    private lateinit var fruitAdminApi: FruitAdminApi
    private lateinit var fruitDataAdminRepository: FruitDataAdminRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk(relaxed = true)
        fruitAdminApi = mockk(relaxed = true)
        fruitDataAdminRepository = mockk(relaxed = true)
        coEvery { api.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }
        coEvery { api.getFruits() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }
        viewModel = FruitDataManagementViewModel(api, fruitAdminApi, fruitDataAdminRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasEmptyDataTypes() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state.dataTypes.isEmpty())
        assertTrue(state.fruits.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun loadDataTypes_success_updatesState() = runTest {
        // Given
        val dataTypes = listOf(
            DataTypeInfo("营养成分", 10, 8),
            DataTypeInfo("风味特征", 5, 5)
        )
        coEvery { api.getAllDataTypes() } returns mockk {
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
        assertEquals("风味特征", state.dataTypes[1])
    }

    @Test
    fun loadDataTypes_failure_setsLoadingFalse() = runTest {
        // Given
        coEvery { api.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns false
        }

        // When
        viewModel.loadDataTypes()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
    }

    @Test
    fun loadFruits_success_updatesState() = runTest {
        // Given
        val fruits = listOf(
            FruitOption(1, "苹果"),
            FruitOption(2, "香蕉")
        )
        coEvery { api.getFruits() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns fruits
        }

        // When
        viewModel.loadFruits()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.fruits.size)
        assertEquals("苹果", state.fruits[0].name)
    }

    @Test
    fun selectDataType_updatesState() = runTest {
        // When
        viewModel.selectDataType("营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("营养成分", state.selectedDataType)
    }

    @Test
    fun selectDataType_null_clearsSelection() = runTest {
        // Given
        viewModel.selectDataType("营养成分")

        // When
        viewModel.selectDataType(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.selectedDataType)
    }

    @Test
    fun selectFruit_updatesState() = runTest {
        // Given
        val fruit = FruitOption(1, "苹果")

        // When
        viewModel.selectFruit(fruit)

        // Then
        val state = viewModel.uiState.first()
        assertEquals("苹果", state.selectedFruit?.name)
    }

    @Test
    fun loadFields_success_updatesState() = runTest {
        // Given
        val fields = listOf(
            FieldOption(1, "维生素C", "mg/100g"),
            FieldOption(2, "糖分", "g/100g")
        )
        coEvery { api.getFields("营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns fields
        }

        // When
        viewModel.loadFields("营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.fields.size)
    }

    @Test
    fun checkTableExists_success_updatesState() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        viewModel.selectFruit(FruitOption(1, "苹果"))

        val tableData = FruitDataEntity(
            id = 1,
            fruitName = "苹果",
            dataType = "营养成分",
            dataValues = mapOf("维生素C" to 10.0),
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        coEvery { api.getTableData("苹果", "营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns tableData
        }

        // When
        viewModel.checkTableExists()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.tableExists)
        assertNotNull(state.tableData)
    }

    @Test
    fun checkTableExists_notFound_setsTableExistsFalse() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        viewModel.selectFruit(FruitOption(1, "苹果"))

        coEvery { api.getTableData("苹果", "营养成分") } returns mockk {
            every { isSuccessful } returns false
        }

        // When
        viewModel.checkTableExists()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.tableExists)
        assertNull(state.tableData)
    }

    @Test
    fun createDataType_success_updatesState() = runTest {
        // Given
        coEvery { api.createDataType(any()) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns listOf(DataTypeInfo("新类型", 0, 0))
        }

        // When
        viewModel.createDataType("新类型", "字段1", "单位")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("数据类型创建成功", state.success)
    }

    @Test
    fun createDataType_failure_setsError() = runTest {
        // Given
        coEvery { api.createDataType(any()) } returns mockk {
            every { isSuccessful } returns false
        }

        // When
        viewModel.createDataType("新类型", "字段1", "单位")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("创建失败", state.error)
    }

    @Test
    fun deleteDataType_success_updatesState() = runTest {
        // Given
        coEvery { api.deleteDataType("营养成分") } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }

        // When
        viewModel.deleteDataType("营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("数据类型已删除", state.success)
    }

    @Test
    fun addField_success_updatesState() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        coEvery { api.addField(any()) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getFields("营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns listOf(FieldOption(1, "新字段", "单位"))
        }

        // When
        viewModel.addField("新字段", "单位")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("字段添加成功", state.success)
    }

    @Test
    fun deleteField_success_updatesState() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        coEvery { api.deleteField(1) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getFields("营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }

        // When
        viewModel.deleteField(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("字段已删除", state.success)
    }

    @Test
    fun addOrUpdateData_success_updatesState() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        viewModel.selectFruit(FruitOption(1, "苹果"))
        coEvery { api.addOrUpdateData(any()) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getTableData("苹果", "营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns FruitDataEntity(1, "苹果", "营养成分", emptyMap(), null, null)
        }

        // When
        viewModel.addOrUpdateData("维生素C", 10.0)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("数据保存成功", state.success)
    }

    @Test
    fun deleteDataField_success_updatesState() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        viewModel.selectFruit(FruitOption(1, "苹果"))
        coEvery { api.deleteDataField("苹果", "营养成分", "维生素C") } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getTableData("苹果", "营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns null
        }

        // When
        viewModel.deleteDataField("维生素C")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("数据已删除", state.success)
    }

    @Test
    fun deleteTable_success_updatesState() = runTest {
        // Given
        viewModel.selectDataType("营养成分")
        viewModel.selectFruit(FruitOption(1, "苹果"))
        coEvery { api.deleteTable("苹果", "营养成分") } returns mockk {
            every { isSuccessful } returns true
        }

        // When
        viewModel.deleteTable()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("表格已删除", state.success)
        assertFalse(state.tableExists)
        assertNull(state.tableData)
    }

    @Test
    fun uploadFile_success_updatesState() = runTest {
        // Given
        viewModel.selectFruit(FruitOption(1, "苹果"))
        coEvery { fruitDataAdminRepository.uploadCsv(any(), "营养成分", "苹果") } returns Result.success(10)

        val mockUri = mockk<Uri>()
        coEvery { api.getTableData("苹果", "营养成分") } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns FruitDataEntity(1, "苹果", "营养成分", emptyMap(), null, null)
        }

        // When
        viewModel.uploadFile(mockUri, "营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("导入成功，共 10 条数据", state.success)
    }

    @Test
    fun uploadFile_failure_setsError() = runTest {
        // Given
        viewModel.selectFruit(FruitOption(1, "苹果"))
        coEvery { fruitDataAdminRepository.uploadCsv(any(), "营养成分", "苹果") } returns Result.failure(Exception("文件格式错误"))

        val mockUri = mockk<Uri>()

        // When
        viewModel.uploadFile(mockUri, "营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.error?.contains("文件格式错误") == true)
    }

    @Test
    fun uploadFile_noFruit_selected_setsError() = runTest {
        // Given - no fruit selected

        // When
        val mockUri = mockk<Uri>()
        viewModel.uploadFile(mockUri, "营养成分")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("请先选择水果", state.error)
    }

    @Test
    fun createFruit_success_updatesState() = runTest {
        // Given
        coEvery { fruitAdminApi.createFruit(any()) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getFruits() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns listOf(FruitOption(1, "葡萄"))
        }

        // When
        viewModel.createFruit("葡萄")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("水果创建成功", state.success)
    }

    @Test
    fun deleteFruit_success_updatesState() = runTest {
        // Given
        coEvery { fruitAdminApi.deleteFruit(1) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getFruits() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }

        // When
        viewModel.deleteFruit(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("水果已删除", state.success)
    }

    @Test
    fun clearError_clearsErrorState() = runTest {
        // Given - trigger an error
        coEvery { api.createDataType(any()) } returns mockk {
            every { isSuccessful } returns false
        }
        viewModel.createDataType("类型", "字段", null)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.error)
    }

    @Test
    fun clearSuccess_clearsSuccessState() = runTest {
        // Given
        coEvery { api.createDataType(any()) } returns mockk {
            every { isSuccessful } returns true
        }
        coEvery { api.getAllDataTypes() } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyList()
        }
        viewModel.createDataType("类型", "字段", null)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearSuccess()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.success)
    }
}
