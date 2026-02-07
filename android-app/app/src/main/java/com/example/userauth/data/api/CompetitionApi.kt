package com.example.userauth.data.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.Response
import com.example.userauth.data.api.dto.*

interface CompetitionApi {
    @GET("competitions")
    suspend fun getAllCompetitions(): Response<List<CompetitionDto>>
    
    @GET("competitions/{id}")
    suspend fun getCompetitionById(@Path("id") id: Long): Response<CompetitionDto>
    
    @POST("competitions")
    suspend fun createCompetition(@Body request: CompetitionRequest): Response<CompetitionDto>
    
    @PUT("competitions/{id}")
    suspend fun updateCompetition(
        @Path("id") id: Long,
        @Body request: CompetitionRequest
    ): Response<CompetitionDto>
    
    @DELETE("competitions/{id}")
    suspend fun deleteCompetition(@Path("id") id: Long): Response<Void>
}
