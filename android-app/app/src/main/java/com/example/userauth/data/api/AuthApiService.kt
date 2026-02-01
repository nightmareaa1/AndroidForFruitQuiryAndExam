package com.example.userauth.data.api

import com.example.userauth.data.api.dto.AuthResponse
import com.example.userauth.data.api.dto.LoginRequest
import com.example.userauth.data.api.dto.RegisterRequest
import com.example.userauth.data.api.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API service interface for authentication endpoints
 * Handles user registration and login operations
 */
interface AuthApiService {

    /**
     * Register a new user
     * POST /auth/register
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    /**
     * Login user and get authentication token
     * POST /auth/login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}