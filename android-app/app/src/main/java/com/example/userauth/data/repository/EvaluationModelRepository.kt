package com.example.userauth.data.repository

import com.example.userauth.data.api.EvaluationModelApi
import com.example.userauth.data.api.dto.EvaluationModelDto
import com.example.userauth.data.api.dto.EvaluationModelRequest
import com.example.userauth.data.api.dto.EvaluationParameterRequest
import com.example.userauth.data.api.dto.toDomainModel
import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvaluationModelRepository @Inject constructor(
    private val api: EvaluationModelApi
) {
    
    suspend fun getAllModels(): Result<List<EvaluationModel>> {
        return try {
            val response = api.getAllModels()
            if (response.isSuccessful) {
                val models = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.success(models)
            } else {
                Result.failure(Exception("Failed to fetch models: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getModelById(id: Long): Result<EvaluationModelDto> {
        return try {
            val response = api.getModelById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createModel(
        name: String,
        parameters: List<EvaluationParameter>
    ): Result<EvaluationModel> {
        return try {
            val request = EvaluationModelRequest(
                name = name,
                parameters = parameters.map {
                    EvaluationParameterRequest(name = it.name, weight = it.weight)
                }
            )
            val response = api.createModel(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateModel(
        id: Long,
        name: String,
        parameters: List<EvaluationParameter>
    ): Result<EvaluationModel> {
        return try {
            val request = EvaluationModelRequest(
                name = name,
                parameters = parameters.map {
                    EvaluationParameterRequest(name = it.name, weight = it.weight)
                }
            )
            val response = api.updateModel(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteModel(id: Long): Result<Unit> {
        return try {
            val response = api.deleteModel(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
