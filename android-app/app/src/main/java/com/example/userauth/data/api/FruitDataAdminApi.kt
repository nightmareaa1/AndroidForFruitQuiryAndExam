package com.example.userauth.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface FruitDataAdminApi {

    @GET("admin/fruit-data/data-types")
    suspend fun getAllDataTypes(): Response<List<DataTypeInfo>>

    @POST("admin/fruit-data/data-types")
    suspend fun createDataType(@Body request: DataTypeCreateRequest): Response<Map<String, Any>>

    @DELETE("admin/fruit-data/data-types/{dataType}")
    suspend fun deleteDataType(@Path("dataType") dataType: String): Response<Map<String, Any>>

    @GET("admin/fruit-data/data")
    suspend fun getTableData(
        @Query("fruitName") fruitName: String,
        @Query("dataType") dataType: String
    ): Response<FruitDataEntity>

    @POST("admin/fruit-data/data")
    suspend fun addOrUpdateData(@Body request: FruitDataFieldRequest): Response<Map<String, Any>>

    @DELETE("admin/fruit-data/data")
    suspend fun deleteDataField(
        @Query("fruitName") fruitName: String,
        @Query("dataType") dataType: String,
        @Query("fieldName") fieldName: String
    ): Response<Map<String, Any>>

    @DELETE("admin/fruit-data/table")
    suspend fun deleteTable(
        @Query("fruitName") fruitName: String,
        @Query("dataType") dataType: String
    ): Response<Map<String, Any>>

    @Multipart
    @POST("admin/fruit-data/import")
    suspend fun importCsv(
        @Part("dataType") dataType: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any>>

    @GET("admin/fruit-data/fields/{dataType}")
    suspend fun getFields(@Path("dataType") dataType: String): Response<List<FieldOption>>

    @POST("admin/fruit-data/fields")
    suspend fun addField(@Body request: FieldCreateRequest): Response<FruitDataFieldEntity>

    @DELETE("admin/fruit-data/fields/{id}")
    suspend fun deleteField(@Path("id") id: Long): Response<Void>

    @GET("admin/fruit-data/fruits")
    suspend fun getFruits(): Response<List<FruitOption>>
}
data class DataTypeInfo(
    val dataType: String,
    val totalFields: Long,
    val activeFields: Long
)

data class DataTypeCreateRequest(
    val dataType: String,
    val firstFieldName: String,
    val firstFieldUnit: String? = null
)

data class FruitDataFieldRequest(
    val fruitName: String,
    val dataType: String,
    val fieldName: String,
    val value: Double
)

data class FruitDataEntity(
    val id: Long?,
    val fruitName: String,
    val dataType: String,
    val dataValues: Map<String, Double>,
    val createdAt: String?,
    val updatedAt: String?
)

data class FruitDataFieldEntity(
    val id: Long?,
    val fieldType: String,
    val fieldName: String,
    val fieldUnit: String?,
    val displayOrder: Int?,
    val isActive: Boolean?
)

data class FruitOption(
    val id: Long,
    val name: String
)

data class FieldCreateRequest(
    val fieldType: String,
    val fieldName: String,
    val fieldUnit: String? = null,
    val displayOrder: Int? = 0
)
