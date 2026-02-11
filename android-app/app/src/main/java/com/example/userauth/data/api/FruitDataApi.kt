package com.example.userauth.data.api

import retrofit2.Response
import retrofit2.http.*

interface FruitDataApi {
    @GET("fruit-data/fruits")
    suspend fun getFruits(): Response<List<FruitOption>>

    @GET("fruit-data/fields/{dataType}")
    suspend fun getFields(@Path("dataType") dataType: String): Response<List<FieldOption>>

    @GET("fruit-data/query")
    suspend fun query(
        @Query("fruit") fruit: String,
        @Query("dataType") dataType: String
    ): Response<FruitDataResponse>

    @GET("fruit-data/all/{dataType}")
    suspend fun getAllData(@Path("dataType") dataType: String): Response<List<FruitDataResponse>>
}

data class FieldOption(
    val id: Long,
    val name: String,
    val unit: String?
)

data class FruitDataResponse(
    val fruitName: String,
    val dataType: String,
    val data: Map<String, Double>
)

data class FieldDataRequest(
    val fieldType: String,
    val fieldName: String,
    val fieldUnit: String? = null
)
