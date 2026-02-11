package com.example.userauth.data.repository

import com.example.userauth.data.api.FruitAdminApi
import com.example.userauth.data.api.FlavorDataRequest
import com.example.userauth.data.api.FruitRequest
import com.example.userauth.data.api.NutritionDataRequest
import com.example.userauth.data.model.Fruit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FruitRepository @Inject constructor(
    private val api: FruitAdminApi
) {
    suspend fun getAllFruits(): Result<List<Fruit>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllFruits()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("获取水果列表失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFruitById(id: Long): Result<Fruit> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFruitById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("水果不存在"))
            } else {
                Result.failure(Exception("获取水果失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createFruit(name: String, description: String?): Result<Fruit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.createFruit(FruitRequest(name, description))
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("创建失败"))
                } else {
                    Result.failure(Exception("创建水果失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateFruit(id: Long, name: String, description: String?): Result<Fruit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.updateFruit(id, FruitRequest(name, description))
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("更新失败"))
                } else {
                    Result.failure(Exception("更新水果失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteFruit(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteFruit(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("删除水果失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addNutritionData(fruitId: Long, componentName: String, value: Double): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.addNutritionData(fruitId, NutritionDataRequest(componentName, value))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("添加营养数据失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun addFlavorData(fruitId: Long, componentName: String, value: Double): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.addFlavorData(fruitId, FlavorDataRequest(componentName, value))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("添加风味数据失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteNutritionData(fruitId: Long, dataId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.deleteNutritionData(fruitId, dataId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("删除营养数据失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteFlavorData(fruitId: Long, dataId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.deleteFlavorData(fruitId, dataId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("删除风味数据失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
