package com.example.userauth.data.api.dto

/**
 * DTO for competition entry data
 * Used for fetching entries that need to be rated
 */
data class CompetitionEntryDto(
    val id: Long,
    val entryName: String,
    val description: String?,
    val contestantName: String,
    val filePath: String?,
    val status: String,
    val submittedAt: String?
)

/**
 * DTO for competition details including entries and evaluation model
 */
data class CompetitionDetailDto(
    val id: Long,
    val name: String,
    val description: String?,
    val modelId: Long,
    val modelName: String?,
    val deadline: String,
    val status: String,
    val entries: List<CompetitionEntryDto>,
    val evaluationModel: EvaluationModelDto?
)
