package com.example.userauth.data.repository

import com.example.userauth.data.api.CompetitionApi
import com.example.userauth.data.api.dto.CompetitionDto
import com.example.userauth.data.api.dto.CompetitionRequest
import com.example.userauth.data.model.Competition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompetitionRepository @Inject constructor(
    private val api: CompetitionApi
) {
    suspend fun getAllCompetitions(): Result<List<Competition>> {
        return try {
            val response = api.getAllCompetitions()
            if (response.isSuccessful) {
                val competitions = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.success(competitions)
            } else {
                Result.failure(Exception("Failed to fetch competitions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCompetitionById(id: Long): Result<CompetitionDto> {
        return try {
            val response = api.getCompetitionById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createCompetition(
        name: String,
        deadline: String,
        modelId: Long,
        description: String = ""
    ): Result<Competition> {
        return try {
            val isoDeadline = "${deadline}T23:59:59"
            val request = CompetitionRequest(
                name = name,
                description = description,
                modelId = modelId,
                deadline = isoDeadline,
                judgeIds = null
            )
            val response = api.createCompetition(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCompetition(
        id: Long,
        name: String,
        deadline: String,
        modelId: Long,
        description: String = ""
    ): Result<Competition> {
        return try {
            val isoDeadline = "${deadline}T23:59:59"
            val request = CompetitionRequest(
                name = name,
                description = description,
                modelId = modelId,
                deadline = isoDeadline,
                judgeIds = null
            )
            val response = api.updateCompetition(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCompetition(id: Long): Result<Unit> {
        return try {
            val response = api.deleteCompetition(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun CompetitionDto.toDomainModel(): Competition {
    return Competition(
        id = id,
        name = name,
        modelId = modelId ?: 0,
        creatorId = creatorId ?: 0,
        deadline = deadline?.substring(0, 10) ?: "",
        status = status ?: "ACTIVE"
    )
}
