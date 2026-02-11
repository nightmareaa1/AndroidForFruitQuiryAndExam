package com.example.userauth.data.repository

import com.example.userauth.data.api.FruitDataApi
import com.example.userauth.data.api.FieldDataRequest
import com.example.userauth.data.api.FruitDataResponse
import com.example.userauth.data.api.FruitOption
import com.example.userauth.data.api.FieldOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FruitDataRepository @Inject constructor(
    private val api: FruitDataApi
) {
    suspend fun getFruits(): Result<List<FruitOption>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFruits()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("获取水果列表失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFields(dataType: String): Result<List<FieldOption>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFields(dataType)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("获取字段列表失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun query(fruit: String, dataType: String): Result<FruitDataResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.query(fruit, dataType)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("未找到数据"))
            } else {
                Result.failure(Exception("查询失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllData(dataType: String): Result<List<FruitDataResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllData(dataType)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("获取数据失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
