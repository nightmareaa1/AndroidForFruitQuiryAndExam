package com.example.userauth.domain.model

/**
 * Domain model for authentication token
 */
data class AuthToken(
    val token: String,
    val username: String,
    val roles: List<String>
)