package com.example.userauth.data.api.dto

/**
 * DTO for user information
 * Matches backend UserResponse
 */
data class UserDto(
    val id: Long,
    val username: String?,
    val roles: List<String>,
    val createdAt: String?
)

data class AdminCreateUserRequestDto(
    val username: String,
    val password: String,
    val isAdmin: Boolean
)

data class UserRoleUpdateRequestDto(
    val isAdmin: Boolean
)
