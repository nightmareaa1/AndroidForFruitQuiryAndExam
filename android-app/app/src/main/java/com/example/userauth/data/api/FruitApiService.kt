package com.example.userauth.data.api

import com.example.userauth.data.api.dto.FruitQueryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API service interface for fruit nutrition query endpoints
 * Handles fruit nutrition and flavor data queries
 */
interface FruitApiService {

    /**
     * Query fruit nutrition or flavor data
     * GET /fruit/query?type={type}&fruit={fruit}
     * 
     * @param type Query type: "nutrition" or "flavor"
     * @param fruit Fruit name: "mango" or "banana"
     */
    @GET("fruit/query")
    suspend fun queryFruit(
        @Query("type") type: String,
        @Query("fruit") fruit: String
    ): Response<FruitQueryResponse>
}