package com.example.userauth.data.api

import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.data.api.dto.LoginRequest
import com.example.userauth.data.api.dto.RegisterRequest
import com.example.userauth.data.api.dto.UserResponse
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
 * Unit tests for AuthApiService
 * Tests authentication API calls using MockWebServer
 */
@RunWith(JUnit4::class)
class AuthApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var authApiService: AuthApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApiService = retrofit.create(AuthApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `register should send correct request and parse response`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(201)
            .setBody("""
                {
                    "id": 1,
                    "username": "testuser",
                    "roles": ["USER"]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = RegisterRequest("testuser", "password123")
        val response = authApiService.register(request)

        assertTrue(response.isSuccessful)
        val userResponse = response.body()!!
        assertEquals(1L, userResponse.id)
        assertEquals("testuser", userResponse.username)
        assertEquals(listOf("USER"), userResponse.roles)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/auth/register", recordedRequest.path)
        val requestBody = recordedRequest.body.readUtf8()
        assertTrue(requestBody.contains("testuser"))
        assertTrue(requestBody.contains("password123"))
    }

    @Test
    fun `register should handle error response`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""
                {
                    "message": "Username already exists"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = RegisterRequest("existinguser", "password123")
        val response = authApiService.register(request)

        assertEquals(400, response.code())
        assertTrue(!response.isSuccessful)
    }

    @Test
    fun `login should send correct request and parse response`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "token": "jwt_token_here",
                    "username": "testuser",
                    "roles": ["USER"]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = LoginRequest("testuser", "password123")
        val response = authApiService.login(request)

        assertTrue(response.isSuccessful)
        val authResponse = response.body()!!
        assertEquals("jwt_token_here", authResponse.token)
        assertEquals("testuser", authResponse.username)
        assertEquals(listOf("USER"), authResponse.roles)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/auth/login", recordedRequest.path)
    }

    @Test
    fun `login should handle authentication failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("""
                {
                    "message": "Invalid credentials"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = LoginRequest("testuser", "wrongpassword")
        val response = authApiService.login(request)

        assertEquals(401, response.code())
        assertTrue(!response.isSuccessful)
    }
}