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
    suspend fun getEvaluationModels(): Response<List<EvaluationModelResponse>>

    /**
     * Create new evaluation model (Admin only)
     * POST /evaluation-models
     */
    @POST("evaluation-models")
    suspend fun createEvaluationModel(@Body request: EvaluationModelRequest): Response<EvaluationModelResponse>

    /**
     * Get evaluation model by ID
     * GET /evaluation-models/{id}
     */
    @GET("evaluation-models/{id}")
    suspend fun getEvaluationModel(@Path("id") id: Long): Response<EvaluationModelResponse>

    /**
     * Update evaluation model (Admin only)
     * PUT /evaluation-models/{id}
     */
    @PUT("evaluation-models/{id}")
    suspend fun updateEvaluationModel(
        @Path("id") id: Long,
        @Body request: EvaluationModelRequest
    ): Response<EvaluationModelResponse>

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
    suspend fun getCompetitions(): Response<List<CompetitionResponse>>

    /**
     * Create new competition (Admin only)
     * POST /competitions
     */
    @POST("competitions")
    suspend fun createCompetition(@Body request: CompetitionRequest): Response<CompetitionResponse>

    /**
     * Get competition by ID
     * GET /competitions/{id}
     */
    @GET("competitions/{id}")
    suspend fun getCompetition(@Path("id") id: Long): Response<CompetitionResponse>

    /**
     * Update competition (Admin only)
     * PUT /competitions/{id}
     */
    @PUT("competitions/{id}")
    suspend fun updateCompetition(
        @Path("id") id: Long,
        @Body request: CompetitionRequest
    ): Response<CompetitionResponse>

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
    suspend fun submitRating(@Body request: RatingRequest): Response<RatingResponse>

    /**
     * Get competition rating data
     * GET /ratings/{competitionId}
     */
    @GET("ratings/{competitionId}")
    suspend fun getCompetitionRatings(@Path("competitionId") competitionId: Long): Response<List<RatingResponse>>

    /**
     * Export competition data as CSV (Admin only)
     * GET /competitions/{id}/export
     */
    @GET("competitions/{id}/export")
    suspend fun exportCompetitionData(@Path("id") competitionId: Long): Response<String>
}