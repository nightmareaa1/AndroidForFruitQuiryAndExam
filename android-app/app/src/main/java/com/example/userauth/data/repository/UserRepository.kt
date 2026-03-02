package com.example.userauth.data.repository

import com.example.userauth.data.api.UserApi
import com.example.userauth.data.api.dto.UserDto
import com.example.userauth.data.api.dto.AdminCreateUserRequestDto
import com.example.userauth.data.api.dto.UserRoleUpdateRequestDto
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

    suspend fun createUser(username: String, password: String, isAdmin: Boolean): Result<UserDto> {
        return try {
            val response = userApi.createUser(AdminCreateUserRequestDto(username, password, isAdmin))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserRole(userId: Long, isAdmin: Boolean): Result<UserDto> {
        return try {
            val response = userApi.updateUserRole(userId, UserRoleUpdateRequestDto(isAdmin))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update role: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: Long): Result<Unit> {
        return try {
            val response = userApi.deleteUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
