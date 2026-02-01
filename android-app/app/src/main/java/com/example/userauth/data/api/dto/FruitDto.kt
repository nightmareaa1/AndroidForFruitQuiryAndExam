package com.example.userauth.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects for Fruit Query API
 */

data class FruitQueryResponse(
    @SerializedName("fruit")
    val fruit: String,
    @SerializedName("type")
    val type: String, // "nutrition" or "flavor"
    @SerializedName("data")
    val data: List<QueryDataItem>
)

data class QueryDataItem(
    @SerializedName("componentName")
    val componentName: String,
    @SerializedName("value")
    val value: Double
)