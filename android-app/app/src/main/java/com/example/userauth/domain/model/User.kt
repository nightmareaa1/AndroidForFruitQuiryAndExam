package com.example.userauth.domain.model

/**
 * Domain model for User
 */
data class User(
    val id: Long,
    val username: String,
    val roles: List<String>
) {
    val isAdmin: Boolean
        get() = roles.contains("ADMIN")
}