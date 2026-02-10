package com.example.userauth.data.model

data class SubmissionScore(
    val id: String,
    val contestant: String,
    val title: String,
    val imageUrl: String? = null,
    val scores: MutableList<ScoreParameter>
)
