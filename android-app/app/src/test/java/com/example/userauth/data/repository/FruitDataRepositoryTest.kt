package com.example.userauth.data.repository

import com.example.userauth.data.api.FieldOption
import com.example.userauth.data.api.FruitDataApi
import com.example.userauth.data.api.FruitDataResponse
import com.example.userauth.data.api.FruitOption
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FruitDataRepositoryTest {

    private lateinit var repository: FruitDataRepository
    private lateinit var api: FruitDataApi

    @Before
    fun setup() {
        api = mockk()
        repository = FruitDataRepository(api)
    }

    @Test
    fun getFruits_success_returnsFruitOptions() = runTest {
        // Given
        val fruits = listOf(
            FruitOption(1, "苹果"),
            FruitOption(2, "香蕉"),
            FruitOption(3, "橙子")
        )
        coEvery { api.getFruits() } returns Response.success(fruits)

        // When
        val result = repository.getFruits()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals("苹果", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun getFruits_empty_returnsEmptyList() = runTest {
        // Given
        coEvery { api.getFruits() } returns Response.success(emptyList())

        // When
        val result = repository.getFruits()

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun getFruits_failure_returnsError() = runTest {
        // Given
        coEvery { api.getFruits() } returns Response.error(500, okhttp3.ResponseBody.create(null, "Error"))

        // When
        val result = repository.getFruits()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("获取水果列表失败") == true)
    }

    @Test
    fun getFields_success_returnsFieldOptions() = runTest {
        // Given
        val fields = listOf(
            FieldOption(1, "维生素C", "mg"),
            FieldOption(2, "糖分", "g")
        )
        coEvery { api.getFields("nutrition") } returns Response.success(fields)

        // When
        val result = repository.getFields("nutrition")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        coVerify { api.getFields("nutrition") }
    }

    @Test
    fun getFields_failure_returnsError() = runTest {
        // Given
        coEvery { api.getFields(any()) } returns Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"))

        // When
        val result = repository.getFields("invalid")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun query_success_returnsData() = runTest {
        // Given
        val data = FruitDataResponse(
            fruitName = "苹果",
            dataType = "nutrition",
            data = mapOf("维生素C" to 50.0, "糖分" to 10.0)
        )
        coEvery { api.query("apple", "nutrition") } returns Response.success(data)

        // When
        val result = repository.query("apple", "nutrition")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("苹果", result.getOrNull()?.fruitName)
        assertEquals(50.0, result.getOrNull()?.data?.get("维生素C"))
    }

    @Test
    fun query_notFound_returnsError() = runTest {
        // Given
        coEvery { api.query(any(), any()) } returns Response.success(null)

        // When
        val result = repository.query("unknown", "nutrition")

        // Then
        assertTrue(result.isFailure)
        assertEquals("未找到数据", result.exceptionOrNull()?.message)
    }

    @Test
    fun getAllData_success_returnsDataList() = runTest {
        // Given
        val dataList = listOf(
            FruitDataResponse("苹果", "nutrition", mapOf("维生素C" to 50.0)),
            FruitDataResponse("香蕉", "nutrition", mapOf("维生素C" to 30.0))
        )
        coEvery { api.getAllData("nutrition") } returns Response.success(dataList)

        // When
        val result = repository.getAllData("nutrition")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun getAllData_failure_returnsError() = runTest {
        // Given
        coEvery { api.getAllData(any()) } returns Response.error(500, okhttp3.ResponseBody.create(null, "Server Error"))

        // When
        val result = repository.getAllData("nutrition")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun query_exception_returnsFailure() = runTest {
        // Given
        coEvery { api.query(any(), any()) } throws Exception("Network timeout")

        // When
        val result = repository.query("apple", "nutrition")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network timeout", result.exceptionOrNull()?.message)
    }
}
