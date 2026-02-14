package com.example.userauth.data.repository

import com.example.userauth.data.api.AuthApiService
import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.data.api.dto.LoginRequest
import com.example.userauth.data.api.dto.RegisterRequest
import com.example.userauth.data.api.dto.UserResponse
import com.example.userauth.data.local.PreferencesManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var repository: AuthRepository
    private lateinit var apiService: AuthApiService
    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        apiService = mockk()
        preferencesManager = mockk(relaxed = true)
        repository = AuthRepository(apiService, preferencesManager)
    }

    @Test
    fun login_success_returnsAuthResponse() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val authResponse = AuthResponse(token = "test-token", username = username, roles = listOf("USER"))
        coEvery { apiService.login(any()) } returns Response.success(authResponse)

        // When
        val result = repository.login(username, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(authResponse, result.getOrNull())
        coVerify { apiService.login(LoginRequest(username, password)) }
        verify { preferencesManager.saveAuthToken("test-token") }
        verify { preferencesManager.saveUserInfo(username, false) }
    }

    @Test
    fun login_success_withAdminRole() = runTest {
        // Given
        val username = "admin"
        val password = "admin123"
        val authResponse = AuthResponse(token = "admin-token", username = username, roles = listOf("ADMIN", "USER"))
        coEvery { apiService.login(any()) } returns Response.success(authResponse)

        // When
        val result = repository.login(username, password)

        // Then
        assertTrue(result.isSuccess)
        verify { preferencesManager.saveUserInfo(username, true) }
    }

    @Test
    fun login_failure_invalidCredentials() = runTest {
        // Given
        coEvery { apiService.login(any()) } returns Response.error(401, okhttp3.ResponseBody.create(null, "Unauthorized"))

        // When
        val result = repository.login("user", "wrongpass")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid username or password") == true)
    }

    @Test
    fun login_failure_emptyBody() = runTest {
        // Given
        coEvery { apiService.login(any()) } returns Response.success(null)

        // When
        val result = repository.login("user", "pass")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun register_success_returnsUserResponse() = runTest {
        // Given
        val username = "newuser"
        val password = "password123"
        val userResponse = UserResponse(id = 1, username = username, roles = listOf("USER"))
        coEvery { apiService.register(any()) } returns Response.success(userResponse)

        // When
        val result = repository.register(username, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userResponse, result.getOrNull())
        coVerify { apiService.register(RegisterRequest(username, password)) }
    }

    @Test
    fun register_failure_usernameExists() = runTest {
        // Given
        coEvery { apiService.register(any()) } returns Response.error(409, okhttp3.ResponseBody.create(null, "Conflict"))

        // When
        val result = repository.register("existinguser", "password123")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun register_failure_invalidInput() = runTest {
        // Given
        coEvery { apiService.register(any()) } returns Response.error(400, okhttp3.ResponseBody.create(null, "Bad Request"))

        // When
        val result = repository.register("ab", "123")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid") == true)
    }

    @Test
    fun getToken_returnsStoredToken() {
        // Given
        every { preferencesManager.getAuthToken() } returns "stored-token"

        // When
        val result = repository.getToken()

        // Then
        assertEquals("stored-token", result)
    }

    @Test
    fun isLoggedIn_delegatesToPreferencesManager() {
        // Given
        every { preferencesManager.isLoggedIn() } returns true

        // When
        val result = repository.isLoggedIn()

        // Then
        assertTrue(result)
        verify { preferencesManager.isLoggedIn() }
    }

    @Test
    fun isAdmin_delegatesToPreferencesManager() {
        // Given
        every { preferencesManager.isAdmin() } returns false

        // When
        val result = repository.isAdmin()

        // Then
        assertFalse(result)
        verify { preferencesManager.isAdmin() }
    }

    @Test
    fun getUsername_delegatesToPreferencesManager() {
        // Given
        every { preferencesManager.getUsernameFromToken() } returns "testuser"

        // When
        val result = repository.getUsername()

        // Then
        assertEquals("testuser", result)
        verify { preferencesManager.getUsernameFromToken() }
    }

    @Test
    fun getUsername_fallbackToCached() {
        // Given
        every { preferencesManager.getUsernameFromToken() } returns null
        every { preferencesManager.getUsername() } returns "cacheduser"

        // When
        val result = repository.getUsername()

        // Then
        assertEquals("cacheduser", result)
    }

    @Test
    fun logout_clearsPreferences() {
        // When
        repository.logout()

        // Then
        verify { preferencesManager.clearAll() }
    }

    @Test
    fun saveToken_delegatesToPreferencesManager() {
        // When
        repository.saveToken("new-token")

        // Then
        verify { preferencesManager.saveAuthToken("new-token") }
    }

    @Test
    fun saveUserInfo_delegatesToPreferencesManager() {
        // When
        repository.saveUserInfo("user", true)

        // Then
        verify { preferencesManager.saveUserInfo("user", true) }
    }
}
