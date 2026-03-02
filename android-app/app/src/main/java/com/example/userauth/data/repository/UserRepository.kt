package com.example.userauth.data.repository

import com.example.userauth.data.api.UserApi
import com.example.userauth.data.api.dto.UserDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getAllUsers(): Result<List<UserDto>> {
        return try {
            val response = userApi.getAllUsers()
            if (response.isSuccessful) {
                val users = response.body() ?: emptyList()
                Result.success(users)
            } else {
                Result.failure(Exception("Failed to fetch users: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
