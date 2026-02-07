package com.example.userauth.data.repository

import com.example.userauth.data.api.RatingApi
import com.example.userauth.data.api.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingRepository @Inject constructor(
    private val api: RatingApi
) {
    
    suspend fun submitRating(request: RatingRequestDto): Result<RatingResponseDto> {
        return try {
            val response = api.submitRating(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to submit rating: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRatingsByEntry(entryId: Long): Result<List<RatingResponseDto>> {
        return try {
            val response = api.getRatingsByEntry(entryId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch ratings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRatingsByCompetition(competitionId: Long): Result<List<RatingResponseDto>> {
        return try {
            val response = api.getRatingsByCompetition(competitionId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch ratings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMyRatings(competitionId: Long): Result<List<RatingResponseDto>> {
        return try {
            val response = api.getMyRatings(competitionId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch my ratings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRatingCompletionStatus(entryId: Long): Result<Boolean> {
        return try {
            val response = api.getRatingCompletionStatus(entryId)
            if (response.isSuccessful) {
                Result.success(response.body()?.completed ?: false)
            } else {
                Result.failure(Exception("Failed to check completion status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCompetitionRatingData(competitionId: Long): Result<CompetitionRatingDataResponseDto> {
        return try {
            val response = api.getCompetitionRatingData(competitionId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch rating data: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
