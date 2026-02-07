package com.example.userauth.data.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.Response
import com.example.userauth.data.api.dto.*

interface EvaluationModelApi {
    @GET("evaluation-models")
    suspend fun getAllModels(): Response<List<EvaluationModelDto>>
    
    @GET("evaluation-models/{id}")
    suspend fun getModelById(@Path("id") id: Long): Response<EvaluationModelDto>
    
    @POST("evaluation-models")
    suspend fun createModel(@Body request: EvaluationModelRequest): Response<EvaluationModelDto>
    
    @PUT("evaluation-models/{id}")
    suspend fun updateModel(
        @Path("id") id: Long,
        @Body request: EvaluationModelRequest
    ): Response<EvaluationModelDto>
    
    @DELETE("evaluation-models/{id}")
    suspend fun deleteModel(@Path("id") id: Long): Response<Void>
}
