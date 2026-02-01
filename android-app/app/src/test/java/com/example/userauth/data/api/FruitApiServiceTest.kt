package com.example.userauth.data.api

import com.example.userauth.data.api.dto.FruitQueryResponse
import com.example.userauth.data.api.dto.QueryDataItem
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * Unit tests for FruitApiService
 * Tests fruit query API calls using MockWebServer
 */
@RunWith(JUnit4::class)
class FruitApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var fruitApiService: FruitApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fruitApiService = retrofit.create(FruitApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `queryFruit should send correct request for nutrition data`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "fruit": "mango",
                    "type": "nutrition",
                    "data": [
                        {
                            "componentName": "Calories",
                            "value": 60.0
                        },
                        {
                            "componentName": "Vitamin C",
                            "value": 36.4
                        }
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = fruitApiService.queryFruit("nutrition", "mango")

        assertTrue(response.isSuccessful)
        val fruitResponse = response.body()!!
        assertEquals("mango", fruitResponse.fruit)
        assertEquals("nutrition", fruitResponse.type)
        assertEquals(2, fruitResponse.data.size)
        assertEquals("Calories", fruitResponse.data[0].componentName)
        assertEquals(60.0, fruitResponse.data[0].value)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/fruit/query?type=nutrition&fruit=mango", recordedRequest.path)
    }

    @Test
    fun `queryFruit should send correct request for flavor data`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "fruit": "banana",
                    "type": "flavor",
                    "data": [
                        {
                            "componentName": "Sweetness",
                            "value": 8.5
                        },
                        {
                            "componentName": "Acidity",
                            "value": 2.3
                        }
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = fruitApiService.queryFruit("flavor", "banana")

        assertTrue(response.isSuccessful)
        val fruitResponse = response.body()!!
        assertEquals("banana", fruitResponse.fruit)
        assertEquals("flavor", fruitResponse.type)
        assertEquals(2, fruitResponse.data.size)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/fruit/query?type=flavor&fruit=banana", recordedRequest.path)
    }

    @Test
    fun `queryFruit should handle not found error`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
            .setBody("""
                {
                    "message": "Fruit not found"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = fruitApiService.queryFruit("nutrition", "unknownfruit")

        assertEquals(404, response.code())
        assertTrue(!response.isSuccessful)
    }

    @Test
    fun `queryFruit should handle bad request error`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""
                {
                    "message": "Invalid query parameters"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = fruitApiService.queryFruit("invalid", "mango")

        assertEquals(400, response.code())
        assertTrue(!response.isSuccessful)
    }
}