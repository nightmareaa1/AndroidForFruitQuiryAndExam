package com.example.userauth.data.repository

import com.example.userauth.data.api.AuthApiService
import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.data.api.dto.LoginRequest
import com.example.userauth.data.api.dto.RegisterRequest
import com.example.userauth.data.api.dto.UserResponse
import com.example.userauth.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication-related data operations
 * Handles user registration, login, and token management
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val preferencesManager: PreferencesManager
) {

    /**
     * Register a new user
     * @param username User's chosen username
     * @param password User's chosen password
     * @return Result containing UserResponse or error
     */
    suspend fun register(username: String, password: String): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(username, password)
                val response = authApiService.register(request)
                
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        Result.success(userResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid username or password format"
                        409 -> "Username already exists"
                        else -> "Registration failed: ${response.message()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Login user and get authentication token
     * @param username User's username
     * @param password User's password
     * @return Result containing AuthResponse or error
     */
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(username, password)
                val response = authApiService.login(request)
                
                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        // Save token and user info locally
                        saveToken(authResponse.token)
                        saveUserInfo(authResponse.username, authResponse.roles.contains("ADMIN"))
                        Result.success(authResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid username or password"
                        else -> "Login failed: ${response.message()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Save authentication token locally
     * @param token JWT token to save
     */
    fun saveToken(token: String) {
        preferencesManager.saveAuthToken(token)
    }

    /**
     * Get stored authentication token
     * @return JWT token or null if not found
     */
    fun getToken(): String? {
        return preferencesManager.getAuthToken()
    }

    /**
     * Save user information locally
     * @param username User's username
     * @param isAdmin Whether user has admin privileges
     */
    fun saveUserInfo(username: String, isAdmin: Boolean) {
        preferencesManager.saveUserInfo(username, isAdmin)
    }

    /**
     * Get username from JWT token for real-time accuracy.
     * @return Username or null if not found
     */
    fun getUsername(): String? {
        // Try to get username from token first for real-time accuracy
        return preferencesManager.getUsernameFromToken()
            ?: preferencesManager.getUsername() // Fallback to cached value
    }

    /**
     * Check if user has admin privileges
     * @return True if user is admin, false otherwise
     */
    fun isAdmin(): Boolean {
        return preferencesManager.isAdmin()
    }

    /**
     * Check if user is logged in
     * @return True if user has valid token, false otherwise
     */
    fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }

    /**
     * Logout user by clearing all stored data
     */
    fun logout() {
        preferencesManager.clearAll()
    }
}