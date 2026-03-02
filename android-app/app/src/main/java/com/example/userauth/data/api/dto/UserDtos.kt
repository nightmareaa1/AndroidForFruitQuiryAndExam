package com.example.userauth.data.api.dto

/**
 * DTO for user information
 * Matches backend UserResponse
 */
data class UserDto(
    val id: Long,
    val username: String,
    val roles: List<String>,
    val createdAt: String?
)
