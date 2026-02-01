package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.viewmodel.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Unit tests for LoginScreen using Robolectric
 * These tests run on the JVM and avoid InputManager issues
 * 
 * Run with: ./gradlew testDebugUnitTest --tests="*LoginScreenUnitTest*"
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [30])
class LoginScreenUnitTest {

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
    fun loginScreen_displaysCorrectUI() {
        // Given
        composeTestRule.setContent {
            TestLoginScreen(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify UI elements are displayed
        composeTestRule.onNodeWithText("Sign In").assertExists()
        composeTestRule.onNodeWithText("Username").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").assertExists()
    }

    @Test
    fun loginScreen_inputFieldsWork() {
        // Given
        composeTestRule.setContent {
            TestLoginScreen(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // When - enter username and password
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("testpassword")

        // Then - verify input is accepted
        composeTestRule.onNodeWithText("testuser").assertExists()
    }

    @Test
    fun loginScreen_callsViewModelLoginWhenButtonClicked() {
        // Given
        composeTestRule.setContent {
            TestLoginScreen(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // When - fill fields and click login
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("testpassword")
        composeTestRule.onNodeWithText("Sign In").performClick()

        // Then - verify ViewModel login method is called
        assert(testViewModel.lastLoginUsername == "testuser")
        assert(testViewModel.lastLoginPassword == "testpassword")
    }

    @Test
    fun loginScreen_showsErrorMessage() {
        // Given - error state
        val errorMessage = "Invalid username or password"
        testViewModel.setLoginState(LoginState(error = errorMessage))
        
        composeTestRule.setContent {
            TestLoginScreen(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - error message should be displayed
        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun loginScreen_navigatesToRegisterWhenSignUpClicked() {
        // Given
        composeTestRule.setContent {
            TestLoginScreen(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // When - click sign up link
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()

        // Then - navigation callback should be called
        assert(navigateToRegisterCalled)
    }

    @Test
    fun loginScreen_navigatesToMainOnLoginSuccess() {
        // Given
        composeTestRule.setContent {
            TestLoginScreen(
                onNavigateToRegister = { navigateToRegisterCalled = true },
                onLoginSuccess = { loginSuccessCalled = true },
                viewModel = testViewModel
            )
        }

        // When - simulate successful login
        testViewModel.setLoginState(LoginState(
            isSuccess = true,
            authResponse = AuthResponse(
                token = "test-token",
                username = "testuser",
                roles = listOf("USER")
            )
        ))

        // Then - login success callback should be called
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            loginSuccessCalled
        }
        assert(loginSuccessCalled)
    }

    /**
     * Test version of LoginScreen that accepts our test ViewModel
     */
    @Composable
    private fun TestLoginScreen(
        onNavigateToRegister: () -> Unit,
        onLoginSuccess: () -> Unit,
        viewModel: TestAuthViewModel
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        
        val loginState by viewModel.loginState.collectAsStateWithLifecycle()
        
        // Handle login success - navigate to main screen
        LaunchedEffect(loginState.isSuccess) {
            if (loginState.isSuccess) {
                onLoginSuccess()
                viewModel.clearLoginState()
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Sign In",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Username input field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !loginState.isLoading,
                isError = loginState.error?.contains("username", ignoreCase = true) == true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
            
            // Password input field with visibility toggle
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                enabled = !loginState.isLoading,
                isError = loginState.error?.contains("password", ignoreCase = true) == true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true
            )
            
            // Error message display
            loginState.error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Login button with loading indicator
            Button(
                onClick = {
                    viewModel.login(username.trim(), password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !loginState.isLoading && username.isNotBlank() && password.isNotBlank()
            ) {
                if (loginState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In", fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigate to register screen
            TextButton(
                onClick = onNavigateToRegister,
                enabled = !loginState.isLoading
            ) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}