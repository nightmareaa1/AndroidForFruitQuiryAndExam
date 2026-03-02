package com.example.userauth.data.api

import com.example.userauth.data.api.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("users")
    suspend fun getAllUsers(): Response<List<UserDto>>
}
