package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.data.api.dto.UserResponse
import com.example.userauth.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication-related operations
 * Manages registration and login state, handles input validation
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Registration state
    private val _registrationState = MutableStateFlow(RegistrationState())
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // Login state
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    /**
     * Register a new user
     * @param username User's chosen username
     * @param password User's chosen password
     */
    fun register(username: String, password: String) {
        // Validate input
        val validationError = validateRegistrationInput(username, password)
        if (validationError != null) {
            _registrationState.value = _registrationState.value.copy(
                isLoading = false,
                error = validationError,
                isSuccess = false
            )
            return
        }

        _registrationState.value = _registrationState.value.copy(
            isLoading = true,
            error = null,
            isSuccess = false
        )

        viewModelScope.launch {
            authRepository.register(username, password)
                .onSuccess { userResponse ->
                    _registrationState.value = _registrationState.value.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                        userResponse = userResponse
                    )
                }
                .onFailure { exception ->
                    _registrationState.value = _registrationState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration failed",
                        isSuccess = false
                    )
                }
        }
    }

    /**
     * Login user
     * @param username User's username
     * @param password User's password
     */
    fun login(username: String, password: String) {
        // Validate input
        val validationError = validateLoginInput(username, password)
        if (validationError != null) {
            _loginState.value = _loginState.value.copy(
                isLoading = false,
                error = validationError,
                isSuccess = false
            )
            return
        }

        _loginState.value = _loginState.value.copy(
            isLoading = true,
            error = null,
            isSuccess = false
        )

        viewModelScope.launch {
            authRepository.login(username, password)
                .onSuccess { authResponse ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                        authResponse = authResponse
                    )
                }
                .onFailure { exception ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Login failed",
                        isSuccess = false
                    )
                }
        }
    }

    /**
     * Clear registration state
     */
    fun clearRegistrationState() {
        _registrationState.value = RegistrationState()
    }

    /**
     * Clear login state
     */
    fun clearLoginState() {
        _loginState.value = LoginState()
    }

    /**
     * Logout user
     */
    fun logout() {
        authRepository.logout()
        clearLoginState()
        clearRegistrationState()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    /**
     * Get current username
     */
    fun getCurrentUsername(): String? {
        return authRepository.getUsername()
    }

    /**
     * Check if current user is admin
     */
    fun isCurrentUserAdmin(): Boolean {
        return authRepository.isAdmin()
    }

    /**
     * Validate registration input
     * @param username Username to validate
     * @param password Password to validate
     * @return Error message or null if valid
     */
    private fun validateRegistrationInput(username: String, password: String): String? {
        return when {
            username.isBlank() -> "Username cannot be empty"
            username.length < 3 -> "Username must be at least 3 characters"
            username.length > 20 -> "Username must be at most 20 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscores"
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
    }

    /**
     * Validate login input
     * @param username Username to validate
     * @param password Password to validate
     * @return Error message or null if valid
     */
    private fun validateLoginInput(username: String, password: String): String? {
        return when {
            username.isBlank() -> "Username cannot be empty"
            password.isBlank() -> "Password cannot be empty"
            else -> null
        }
    }
}

/**
 * Data class representing registration state
 */
data class RegistrationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val userResponse: UserResponse? = null
)

/**
 * Data class representing login state
 */
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val authResponse: AuthResponse? = null
)