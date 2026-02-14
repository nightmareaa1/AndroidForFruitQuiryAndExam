package com.example.userauth.data.repository

import com.example.userauth.data.api.FruitAdminApi
import com.example.userauth.data.api.FlavorDataRequest
import com.example.userauth.data.api.FruitRequest
import com.example.userauth.data.api.NutritionDataRequest
import com.example.userauth.data.model.Fruit
import com.example.userauth.data.model.FruitDataItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FruitRepositoryTest {

    private lateinit var repository: FruitRepository
    private lateinit var api: FruitAdminApi

    @Before
    fun setup() {
        api = mockk()
        repository = FruitRepository(api)
    }

    @Test
    fun getAllFruits_success_returnsFruitList() = runTest {
        // Given
        val fruits = listOf(
            Fruit(1, "苹果", "红苹果", null, emptyList(), emptyList()),
            Fruit(2, "香蕉", "黄香蕉", null, emptyList(), emptyList())
        )
        coEvery { api.getAllFruits() } returns Response.success(fruits)

        // When
        val result = repository.getAllFruits()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("苹果", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun getAllFruits_failure_returnsError() = runTest {
        // Given
        coEvery { api.getAllFruits() } returns Response.error(500, okhttp3.ResponseBody.create(null, "Error"))

        // When
        val result = repository.getAllFruits()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("获取水果列表失败") == true)
    }

    @Test
    fun getFruitById_success_returnsFruit() = runTest {
        // Given
        val fruit = Fruit(1, "苹果", "红苹果", null, emptyList(), emptyList())
        coEvery { api.getFruitById(1) } returns Response.success(fruit)

        // When
        val result = repository.getFruitById(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("苹果", result.getOrNull()?.name)
    }

    @Test
    fun getFruitById_notFound_returnsError() = runTest {
        // Given
        coEvery { api.getFruitById(999) } returns Response.success(null)

        // When
        val result = repository.getFruitById(999)

        // Then
        assertTrue(result.isFailure)
        assertEquals("水果不存在", result.exceptionOrNull()?.message)
    }

    @Test
    fun createFruit_success_returnsCreatedFruit() = runTest {
        // Given
        val createdFruit = Fruit(1, "橙子", "甜橙子", null, emptyList(), emptyList())
        coEvery { api.createFruit(any()) } returns Response.success(createdFruit)

        // When
        val result = repository.createFruit("橙子", "甜橙子")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("橙子", result.getOrNull()?.name)
        coVerify { api.createFruit(FruitRequest("橙子", "甜橙子")) }
    }

    @Test
    fun createFruit_failure_returnsError() = runTest {
        // Given
        coEvery { api.createFruit(any()) } returns Response.error(400, okhttp3.ResponseBody.create(null, "Bad Request"))

        // When
        val result = repository.createFruit("", "")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun updateFruit_success_returnsUpdatedFruit() = runTest {
        // Given
        val updatedFruit = Fruit(1, "红苹果", "更新后的描述", null, emptyList(), emptyList())
        coEvery { api.updateFruit(1, any()) } returns Response.success(updatedFruit)

        // When
        val result = repository.updateFruit(1, "红苹果", "更新后的描述")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("红苹果", result.getOrNull()?.name)
    }

    @Test
    fun deleteFruit_success_returnsUnit() = runTest {
        // Given
        coEvery { api.deleteFruit(1) } returns Response.success(null)

        // When
        val result = repository.deleteFruit(1)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun addNutritionData_success_returnsUnit() = runTest {
        // Given
        coEvery { api.addNutritionData(1, any()) } returns Response.success(FruitDataItem("维生素C", 50.0))

        // When
        val result = repository.addNutritionData(1, "维生素C", 50.0)

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.addNutritionData(1, NutritionDataRequest("维生素C", 50.0)) }
    }

    @Test
    fun addFlavorData_success_returnsUnit() = runTest {
        // Given
        coEvery { api.addFlavorData(1, any()) } returns Response.success(FruitDataItem("甜度", 8.5))

        // When
        val result = repository.addFlavorData(1, "甜度", 8.5)

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.addFlavorData(1, FlavorDataRequest("甜度", 8.5)) }
    }

    @Test
    fun deleteNutritionData_success_returnsUnit() = runTest {
        // Given
        coEvery { api.deleteNutritionData(1, 100) } returns Response.success(null)

        // When
        val result = repository.deleteNutritionData(1, 100)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun deleteFlavorData_success_returnsUnit() = runTest {
        // Given
        coEvery { api.deleteFlavorData(1, 200) } returns Response.success(null)

        // When
        val result = repository.deleteFlavorData(1, 200)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun getAllFruits_exception_returnsFailure() = runTest {
        // Given
        coEvery { api.getAllFruits() } throws Exception("Network error")

        // When
        val result = repository.getAllFruits()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
