package com.example.userauth.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects for Evaluation Model API
 */

data class EvaluationModelRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("parameters")
    val parameters: List<ParameterRequest>
)

data class ParameterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("weight")
    val weight: Int
)

data class EvaluationModelResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("parameters")
    val parameters: List<ParameterResponse>
)

data class ParameterResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("displayOrder")
    val displayOrder: Int
)

data class CompetitionRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("modelId")
    val modelId: Long,
    @SerializedName("deadline")
    val deadline: String, // ISO 8601 format
    @SerializedName("judgeIds")
    val judgeIds: List<Long>
)

data class CompetitionResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("modelId")
    val modelId: Long,
    @SerializedName("creatorId")
    val creatorId: Long,
    @SerializedName("deadline")
    val deadline: String,
    @SerializedName("status")
    val status: String
)

data class EntryRequest(
    @SerializedName("entryName")
    val entryName: String,
    @SerializedName("displayOrder")
    val displayOrder: Int
)

data class RatingRequest(
    @SerializedName("competitionId")
    val competitionId: Long,
    @SerializedName("entryId")
    val entryId: Long,
    @SerializedName("scores")
    val scores: List<ScoreRequest>,
    @SerializedName("note")
    val note: String?
)

data class ScoreRequest(
    @SerializedName("parameterId")
    val parameterId: Long,
    @SerializedName("score")
    val score: Double
)

data class RatingResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("competitionId")
    val competitionId: Long,
    @SerializedName("entryId")
    val entryId: Long,
    @SerializedName("judgeId")
    val judgeId: Long,
    @SerializedName("scores")
    val scores: List<ScoreResponse>,
    @SerializedName("note")
    val note: String?
)

data class ScoreResponse(
    @SerializedName("parameterId")
    val parameterId: Long,
    @SerializedName("score")
    val score: Double
)