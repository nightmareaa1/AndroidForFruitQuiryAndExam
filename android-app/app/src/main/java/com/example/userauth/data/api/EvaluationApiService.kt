package com.example.userauth.data.api

import com.example.userauth.data.api.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API service interface for evaluation system endpoints
 * Handles evaluation models, competitions, and ratings
 */
interface EvaluationApiService {

    // Evaluation Models
    /**
     * Get all evaluation models
     * GET /evaluation-models
     */
    @GET("evaluation-models")
    suspend fun getEvaluationModels(): Response<List<EvaluationModelDto>>

    @POST("evaluation-models")
    suspend fun createEvaluationModel(@Body request: EvaluationModelRequest): Response<EvaluationModelDto>

    @GET("evaluation-models/{id}")
    suspend fun getEvaluationModel(@Path("id") id: Long): Response<EvaluationModelDto>

    @PUT("evaluation-models/{id}")
    suspend fun updateEvaluationModel(
        @Path("id") id: Long,
        @Body request: EvaluationModelRequest
    ): Response<EvaluationModelDto>

    /**
     * Delete evaluation model (Admin only)
     * DELETE /evaluation-models/{id}
     */
    @DELETE("evaluation-models/{id}")
    suspend fun deleteEvaluationModel(@Path("id") id: Long): Response<Unit>

    // Competitions
    /**
     * Get competitions (filtered by user role)
     * GET /competitions
     */
    @GET("competitions")
    suspend fun getCompetitions(): Response<List<CompetitionDto>>

    @POST("competitions")
    suspend fun createCompetition(@Body request: CompetitionRequest): Response<CompetitionDto>

    @GET("competitions/{id}")
    suspend fun getCompetition(@Path("id") id: Long): Response<CompetitionDto>

    @PUT("competitions/{id}")
    suspend fun updateCompetition(
        @Path("id") id: Long,
        @Body request: CompetitionRequest
    ): Response<CompetitionDto>

    /**
     * Delete competition (Admin only)
     * DELETE /competitions/{id}
     */
    @DELETE("competitions/{id}")
    suspend fun deleteCompetition(@Path("id") id: Long): Response<Unit>

    // Competition Entries
    /**
     * Submit competition entries (Admin only)
     * POST /competitions/{id}/entries
     */
    @Multipart
    @POST("competitions/{id}/entries")
    suspend fun submitEntries(
        @Path("id") competitionId: Long,
        @Part("entries") entries: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<List<Long>>

    // Ratings
    /**
     * Submit rating for competition entry (Judge only)
     * POST /ratings
     */
    @POST("ratings")
    suspend fun submitRating(@Body request: RatingRequestDto): Response<RatingResponseDto>

    @GET("ratings/{competitionId}")
    suspend fun getCompetitionRatings(@Path("competitionId") competitionId: Long): Response<List<RatingResponseDto>>

    /**
     * Export competition data as CSV (Admin only)
     * GET /competitions/{id}/export
     */
    @GET("competitions/{id}/export")
    suspend fun exportCompetitionData(@Path("id") competitionId: Long): Response<String>
}