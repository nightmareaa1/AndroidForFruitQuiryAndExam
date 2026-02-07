package com.example.userauth.data.api.dto

/**
 * DTO for submitting a rating request
 * Matches backend RatingRequest
 */
data class RatingRequestDto(
    val competitionId: Long,
    val entryId: Long,
    val scores: List<ScoreRequestDto>,
    val note: String? = null
)

data class ScoreRequestDto(
    val parameterId: Long,
    val score: Double
)

/**
 * DTO for rating response
 * Matches backend RatingResponse
 */
data class RatingResponseDto(
    val id: Long?,
    val competitionId: Long,
    val entryId: Long,
    val entryName: String?,
    val judgeId: Long?,
    val judgeName: String?,
    val scores: List<ScoreResponseDto>?,
    val note: String?,
    val submittedAt: String?
)

data class ScoreResponseDto(
    val parameterId: Long,
    val parameterName: String,
    val parameterWeight: Int,
    val score: Double
)

/**
 * DTO for rating completion status response
 * Matches backend RatingCompletionResponse
 */
data class RatingCompletionResponseDto(
    val completed: Boolean
)

/**
 * DTO for competition rating data response (aggregated data with statistics)
 * Matches backend CompetitionRatingDataResponse
 */
data class CompetitionRatingDataResponseDto(
    val competitionId: Long,
    val competitionName: String,
    val entries: List<EntryRatingDataDto>
)

data class EntryRatingDataDto(
    val entryId: Long,
    val entryName: String,
    val contestantName: String,
    val averageTotalScore: Double,
    val numberOfRatings: Int,
    val parameterScores: List<ParameterAverageScoreDto>
)

data class ParameterAverageScoreDto(
    val parameterId: Long,
    val parameterName: String,
    val averageScore: Double,
    val weight: Int
)
