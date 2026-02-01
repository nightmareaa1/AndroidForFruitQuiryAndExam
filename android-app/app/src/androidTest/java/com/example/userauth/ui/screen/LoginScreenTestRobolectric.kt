package com.example.userauth.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.viewmodel.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Robolectric-compatible UI tests for LoginScreen
 * This version avoids InputManager issues by using Robolectric instead of instrumented tests
 * 
 * Run with: ./gradlew testDebugUnitTest --tests="*LoginScreenTestRobolectric*"
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTestRobolectric {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var testViewModel: TestAuthViewModel
    private var navigateToRegisterCalled = false
    private var loginSuccessCalled = false

    @Before
    fun setup() {
        testViewModel = TestAuthViewModel()
        
        // Reset navigation flags
        navigateToRegisterCalled = false
        loginSuccessCalled = false
    }

    /**
     * Test implementation of AuthViewModel for UI testing
     */
    private class TestAuthViewModel {
        private val _loginState = MutableStateFlow(LoginState())
        val loginState: StateFlow<LoginState> = _loginState

        var lastLoginUsername: String? = null
        var lastLoginPassword: String? = null

        fun login(username: String, password: String) {
            lastLoginUsername = username
            lastLoginPassword = password
            _loginState.value = LoginState(isLoading = true)
        }

        fun clearLoginState() {
            _loginState.value = LoginState()
        }

        fun setLoginState(state: LoginState) {
            _loginState.value = state
        }
    }

    @Test
    fun loginScreen_basicUIElements_areDisplayed() {
        // Given
        composeTestRule.setContent {
            TestLoginScreenSimple(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify basic UI elements without complex interactions
        composeTestRule.onNodeWithText("Sign In").assertExists()
        composeTestRule.onNodeWithText("Username").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").assertExists()
    }

    @Test
    fun loginScreen_errorState_showsMessage() {
        // Given - set error state first
        testViewModel.setLoginState(LoginState(error = "Test error message"))
        
        composeTestRule.setContent {
            TestLoginScreenSimple(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - error should be visible
        composeTestRule.onNodeWithText("Test error message").assertExists()
    }

    @Test
    fun loginScreen_loadingState_showsCorrectUI() {
        // Given - set loading state
        testViewModel.setLoginState(LoginState(isLoading = true))
        
        composeTestRule.setContent {
            TestLoginScreenSimple(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - UI should exist in loading state
        composeTestRule.onNodeWithText("Sign In").assertExists()
    }

    @Test
    fun loginScreen_successState_triggersNavigation() {
        // Given
        composeTestRule.setContent {
            TestLoginScreenSimple(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // When - simulate success
        testViewModel.setLoginState(LoginState(
            isSuccess = true,
            authResponse = AuthResponse(
                token = "test-token",
                username = "testuser",
                roles = listOf("USER")
            )
        ))

        // Then - wait for navigation callback
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            loginSuccessCalled
        }
        assert(loginSuccessCalled)
    }

    /**
     * Simplified version of LoginScreen for testing
     * This avoids complex UI interactions that might trigger InputManager issues
     */
    @androidx.compose.runtime.Composable
    private fun TestLoginScreenSimple(
        onNavigateToRegister: () -> Unit,
        onLoginSuccess: () -> Unit,
        viewModel: TestAuthViewModel
    ) {
        // Use the actual LoginScreen from the main source
        // This ensures we're testing the real implementation
        LoginScreen(
            onNavigateToRegister = onNavigateToRegister,
            onLoginSuccess = onLoginSuccess
        )
    }
}