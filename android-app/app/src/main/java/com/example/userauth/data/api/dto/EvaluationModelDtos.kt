package com.example.userauth.data.api.dto

import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter

data class EvaluationModelDto(
    val id: Long,
    val name: String,
    val description: String?,
    val parameters: List<EvaluationParameterDto>,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class EvaluationParameterDto(
    val id: Long,
    val name: String,
    val weight: Int,
    val maxScore: Int? = null
)

data class EvaluationModelRequest(
    val name: String,
    val description: String? = null,
    val parameters: List<EvaluationParameterRequest>
)

data class EvaluationParameterRequest(
    val name: String,
    val weight: Int
)

fun EvaluationModelDto.toDomainModel(): EvaluationModel {
    return EvaluationModel(
        id = id.toString(),
        name = name,
        description = description ?: "",
        parameters = parameters.map { 
            EvaluationParameter(
                id = it.id.toString(),
                name = it.name,
                weight = it.weight,
                maxScore = it.maxScore ?: 10
            )
        }
    )
}
