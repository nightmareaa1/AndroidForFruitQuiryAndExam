package com.example.userauth.data.api

import com.example.userauth.data.api.dto.UserDto
import com.example.userauth.data.api.dto.AdminCreateUserRequestDto
import com.example.userauth.data.api.dto.UserRoleUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("users")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @POST("users")
    suspend fun createUser(@Body request: AdminCreateUserRequestDto): Response<UserDto>

    @PUT("users/{id}/role")
    suspend fun updateUserRole(
        @Path("id") id: Long,
        @Body request: UserRoleUpdateRequestDto
    ): Response<UserDto>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Void>
}
