package com.example.userauth.data.api

import com.example.userauth.data.api.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API interface for rating operations
 * Matches backend RatingController endpoints
 */
interface RatingApi {
    
    /**
     * Submit or update ratings for a competition entry
     * POST /api/ratings
     */
    @POST("ratings")
    suspend fun submitRating(@Body request: RatingRequestDto): Response<RatingResponseDto>
    
    /**
     * Get all ratings for a specific competition entry
     * GET /api/ratings/entry/{entryId}
     */
    @GET("ratings/entry/{entryId}")
    suspend fun getRatingsByEntry(@Path("entryId") entryId: Long): Response<List<RatingResponseDto>>
    
    /**
     * Get all ratings for a specific competition
     * GET /api/ratings/competition/{competitionId}
     */
    @GET("ratings/competition/{competitionId}")
    suspend fun getRatingsByCompetition(@Path("competitionId") competitionId: Long): Response<List<RatingResponseDto>>
    
    /**
     * Get ratings submitted by current user (judge) for a specific competition
     * GET /api/ratings/competition/{competitionId}/my-ratings
     */
    @GET("ratings/competition/{competitionId}/my-ratings")
    suspend fun getMyRatings(@Path("competitionId") competitionId: Long): Response<List<RatingResponseDto>>
    
    /**
     * Check if current user (judge) has completed rating for a specific entry
     * GET /api/ratings/entry/{entryId}/completion-status
     */
    @GET("ratings/entry/{entryId}/completion-status")
    suspend fun getRatingCompletionStatus(@Path("entryId") entryId: Long): Response<RatingCompletionResponseDto>
    
    /**
     * Get aggregated rating data for a competition (with averages and statistics)
     * GET /api/ratings/{competitionId}
     */
    @GET("ratings/{competitionId}")
    suspend fun getCompetitionRatingData(@Path("competitionId") competitionId: Long): Response<CompetitionRatingDataResponseDto>
}
