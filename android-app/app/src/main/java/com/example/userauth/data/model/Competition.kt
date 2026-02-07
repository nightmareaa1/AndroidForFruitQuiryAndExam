package com.example.userauth.data.model

data class Competition(
    val id: Long,
    val name: String,
    val modelId: Long,
    val creatorId: Long,
    val deadline: String,
    val status: String
)
