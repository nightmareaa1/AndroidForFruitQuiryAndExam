package com.example.userauth.data.api.dto

import com.example.userauth.data.model.Competition

data class CompetitionDto(
    val id: Long,
    val name: String,
    val description: String?,
    val modelId: Long?,
    val modelName: String?,
    val creatorId: Long?,
    val creatorUsername: String?,
    val deadline: String?,
    val status: String?,
    val judges: List<JudgeDto>?,
    val entries: List<EntryDto>?,
    val createdAt: String?,
    val updatedAt: String?
)

data class JudgeDto(
    val id: Long,
    val userId: Long,
    val username: String,
    val createdAt: String?
)

data class EntryDto(
    val id: Long,
    val entryName: String,
    val description: String?,
    val filePath: String?,
    val displayOrder: Int?,
    val status: String?,
    val contestantId: Long?,
    val contestantName: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CompetitionRequest(
    val name: String,
    val description: String? = null,
    val modelId: Long,
    val deadline: String,
    val judgeIds: List<Long>? = null
)

fun CompetitionDto.toDomainModel(): Competition {
    return Competition(
        id = id,
        name = name,
        modelId = modelId ?: 0L,
        creatorId = creatorId ?: 0L,
        deadline = deadline?.substring(0, 10) ?: "",
        status = status ?: "ACTIVE",
        description = description ?: ""
    )
}

data class EntryRequestDto(
    val entryName: String,
    val description: String? = null
)

data class EntrySubmitResponseDto(
    val entryId: Long?,
    val message: String?
)
