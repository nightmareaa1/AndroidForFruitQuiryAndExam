package com.example.userauth.data.api

import com.example.userauth.data.model.Fruit
import com.example.userauth.data.model.FruitDataItem
import retrofit2.Response
import retrofit2.http.*

interface FruitAdminApi {
    @GET("admin/fruits")
    suspend fun getAllFruits(): Response<List<Fruit>>

    @GET("admin/fruits/{id}")
    suspend fun getFruitById(@Path("id") id: Long): Response<Fruit>

    @POST("admin/fruits")
    suspend fun createFruit(@Body request: FruitRequest): Response<Fruit>

    @PUT("admin/fruits/{id}")
    suspend fun updateFruit(@Path("id") id: Long, @Body request: FruitRequest): Response<Fruit>

    @DELETE("admin/fruits/{id}")
    suspend fun deleteFruit(@Path("id") id: Long): Response<Void>

    @POST("admin/fruits/{id}/nutrition")
    suspend fun addNutritionData(
        @Path("id") fruitId: Long,
        @Body request: NutritionDataRequest
    ): Response<FruitDataItem>

    @POST("admin/fruits/{id}/flavor")
    suspend fun addFlavorData(
        @Path("id") fruitId: Long,
        @Body request: FlavorDataRequest
    ): Response<FruitDataItem>

    @DELETE("admin/fruits/{fruitId}/nutrition/{dataId}")
    suspend fun deleteNutritionData(
        @Path("fruitId") fruitId: Long,
        @Path("dataId") dataId: Long
    ): Response<Void>

    @DELETE("admin/fruits/{fruitId}/flavor/{dataId}")
    suspend fun deleteFlavorData(
        @Path("fruitId") fruitId: Long,
        @Path("dataId") dataId: Long
    ): Response<Void>
}

data class FruitRequest(
    val name: String,
    val description: String? = null
)

data class NutritionDataRequest(
    val componentName: String,
    val componentValue: Double
)

data class FlavorDataRequest(
    val componentName: String,
    val componentValue: Double
)
