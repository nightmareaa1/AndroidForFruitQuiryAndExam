package com.example.userauth.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for user information
 * Matches backend UserResponse
 */
data class UserDto(
    val id: Long,
    val username: String?,
    val roles: List<String>,
    val createdAt: String?,
    @SerializedName(value = "password", alternate = ["initialPassword", "initial_password"])
    val password: String?
)

data class AdminCreateUserRequestDto(
    val username: String,
    val password: String,
    val isAdmin: Boolean
)

data class UserRoleUpdateRequestDto(
    val isAdmin: Boolean
)
