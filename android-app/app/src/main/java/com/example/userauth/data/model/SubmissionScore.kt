package com.example.userauth.data.model

data class SubmissionScore(
    val id: String,
    val contestant: String,
    val title: String,
    val imageUrl: String? = null,
    val scores: MutableList<ScoreParameter>,
    val averageTotalScore: Double = 0.0,
    val highestScore: Double = 0.0,
    val numberOfRatings: Int = 0,
    val totalJudges: Int = 0  // 评委总数
)
