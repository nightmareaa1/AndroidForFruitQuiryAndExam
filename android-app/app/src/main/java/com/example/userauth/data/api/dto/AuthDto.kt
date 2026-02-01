package com.example.userauth.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects for Authentication API
 */

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("roles")
    val roles: List<String>
)

data class UserResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val username: String,
    @SerializedName("roles")
    val roles: List<String>
)