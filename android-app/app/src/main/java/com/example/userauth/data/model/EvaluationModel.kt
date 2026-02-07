package com.example.userauth.data.model

data class EvaluationModel(
    val id: String,
    val name: String,
    val description: String = "",
    val parameters: List<EvaluationParameter> = emptyList()
)

data class EvaluationParameter(
    val id: String,
    val name: String,
    val weight: Int,
    val maxScore: Int = 10
)
