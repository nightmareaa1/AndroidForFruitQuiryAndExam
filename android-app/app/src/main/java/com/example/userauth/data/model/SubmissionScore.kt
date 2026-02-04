package com.example.userauth.data.model

data class SubmissionScore(
    val id: String,
    val contestant: String,
    val title: String,
    val scores: MutableList<ScoreParameter>
)
