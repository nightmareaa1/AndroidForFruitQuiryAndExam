package com.example.userauth.viewmodel

import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.data.api.dto.UserResponse
import com.example.userauth.data.repository.AuthRepository
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
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_registrationStateIsEmpty() = runTest {
        val state = viewModel.registrationState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isSuccess)
        assertNull(state.userResponse)
    }

    @Test
    fun initialState_loginStateIsEmpty() = runTest {
        val state = viewModel.loginState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isSuccess)
        assertNull(state.authResponse)
    }

    @Test
    fun register_success_updatesState() = runTest {
        // Given
        val userResponse = UserResponse(1, "testuser", listOf("USER"))
        coEvery { repository.register("testuser", "password123") } returns Result.success(userResponse)

        // When
        viewModel.register("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.registrationState.first()
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.error)
        assertEquals("testuser", state.userResponse?.username)
    }

    @Test
    fun register_failure_updatesError() = runTest {
        // Given
        coEvery { repository.register(any(), any()) } returns Result.failure(Exception("Username exists"))

        // When
        viewModel.register("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.registrationState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertEquals("Username exists", state.error)
    }

    @Test
    fun register_invalidUsername_showsValidationError() = runTest {
        // When
        viewModel.register("ab", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.registrationState.first()
        assertFalse(state.isLoading)
        assertTrue(state.error?.contains("at least 3 characters") == true)
    }

    @Test
    fun register_invalidPassword_showsValidationError() = runTest {
        // When
        viewModel.register("testuser", "123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.registrationState.first()
        assertFalse(state.isLoading)
        assertTrue(state.error?.contains("at least 8 characters") == true)
    }

    @Test
    fun login_success_updatesState() = runTest {
        // Given
        val authResponse = AuthResponse("token123", "testuser", listOf("USER"))
        coEvery { repository.login("testuser", "password123") } returns Result.success(authResponse)

        // When
        viewModel.login("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.loginState.first()
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.error)
        assertEquals("token123", state.authResponse?.token)
    }

    @Test
    fun login_failure_updatesError() = runTest {
        // Given
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception("Invalid credentials"))

        // When
        viewModel.login("testuser", "wrongpassword")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.loginState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertEquals("Invalid credentials", state.error)
    }

    @Test
    fun login_emptyUsername_showsValidationError() = runTest {
        // When
        viewModel.login("", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.loginState.first()
        assertFalse(state.isLoading)
        assertTrue(state.error?.contains("cannot be empty") == true)
    }

    @Test
    fun clearRegistrationState_resetsState() = runTest {
        // Given - trigger a registration first
        coEvery { repository.register(any(), any()) } returns Result.failure(Exception("Error"))
        viewModel.register("test", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearRegistrationState()
        
        // Then
        val state = viewModel.registrationState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isSuccess)
    }

    @Test
    fun clearLoginState_resetsState() = runTest {
        // Given
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception("Error"))
        viewModel.login("test", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearLoginState()

        // Then
        val state = viewModel.loginState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isSuccess)
    }

    @Test
    fun logout_callsRepositoryAndClearsState() {
        // When
        viewModel.logout()

        // Then
        val loginState = viewModel.loginState.value
        val registrationState = viewModel.registrationState.value
        assertFalse(loginState.isLoading)
        assertFalse(registrationState.isLoading)
    }

    @Test
    fun isLoggedIn_delegatesToRepository() {
        // Given
        every { repository.isLoggedIn() } returns true

        // When
        val result = viewModel.isLoggedIn()

        // Then
        assertTrue(result)
    }

    @Test
    fun getCurrentUsername_delegatesToRepository() {
        // Given
        every { repository.getUsername() } returns "testuser"

        // When
        val result = viewModel.getCurrentUsername()

        // Then
        assertEquals("testuser", result)
    }

    @Test
    fun isCurrentUserAdmin_delegatesToRepository() {
        // Given
        every { repository.isAdmin() } returns true

        // When
        val result = viewModel.isCurrentUserAdmin()

        // Then
        assertTrue(result)
    }
}
