package com.example.userauth.data.api.dto

import com.example.userauth.data.model.Competition
import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data class CompetitionDto(
    val id: Long,
    val name: String,
    val description: String?,
    val modelId: Long?,
    val modelName: String?,
    val creatorId: Long?,
    val creatorUsername: String?,
    val deadline: String?,
    val status: String?,
    val judges: List<JudgeDto>?,
    val entries: List<EntryDto>?,
    val createdAt: String?,
    val updatedAt: String?
)

data class JudgeDto(
    val id: Long,
    val userId: Long,
    val username: String,
    val createdAt: String?
)

data class EntryDto(
    val id: Long,
    val entryName: String,
    val description: String?,
    val filePath: String?,
    val displayOrder: Int?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CompetitionRequest(
    val name: String,
    val description: String? = null,
    val modelId: Long,
    val deadline: String,
    val judgeIds: List<Long>? = null
)

/**
 * Custom deserializer for CompetitionDto to handle array format dates
 */
class CompetitionDtoDeserializer : JsonDeserializer<CompetitionDto> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CompetitionDto {
        if (json == null || !json.isJsonObject) {
            throw JsonParseException("Expected JsonObject")
        }
        
        val obj = json.asJsonObject
        
        val id = obj.get("id").asLong
        val name = obj.get("name").asString
        val description = obj.get("description")?.asString
        val modelId = obj.get("modelId")?.asLong
        val modelName = obj.get("modelName")?.asString
        val creatorId = obj.get("creatorId")?.asLong
        val creatorUsername = obj.get("creatorUsername")?.asString
        val deadline = parseDateTime(obj.get("deadline"))
        val status = obj.get("status")?.asString
        
        // Handle arrays
        val judges = context?.deserialize<List<JudgeDto>>(
            obj.get("judges"),
            object : TypeToken<List<JudgeDto>>() {}.type
        )
        val entries = context?.deserialize<List<EntryDto>>(
            obj.get("entries"),
            object : TypeToken<List<EntryDto>>() {}.type
        )
        
        // Handle createdAt
        val createdAt = parseDateTime(obj.get("createdAt"))
        // Handle updatedAt
        val updatedAt = parseDateTime(obj.get("updatedAt"))
        
        return CompetitionDto(
            id = id,
            name = name,
            description = description,
            modelId = modelId,
            modelName = modelName,
            creatorId = creatorId,
            creatorUsername = creatorUsername,
            deadline = deadline,
            status = status,
            judges = judges,
            entries = entries,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun parseDateTime(jsonElement: JsonElement?): String? {
        if (jsonElement == null) return null
        
        return when {
            jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isString -> {
                jsonElement.asString
            }
            jsonElement.isJsonArray -> {
                val arr = jsonElement.asJsonArray
                if (arr.size() >= 6) {
                    String.format("%04d-%02d-%02dT%02d:%02d:%02d",
                        arr.get(0).asInt,
                        arr.get(1).asInt,
                        arr.get(2).asInt,
                        arr.get(3).asInt,
                        arr.get(4).asInt,
                        arr.get(5).asInt
                    )
                } else null
            }
            else -> null
        }
    }
}

fun CompetitionDto.toDomainModel(): Competition {
    return Competition(
        id = id,
        name = name,
        modelId = modelId ?: 0L,
        creatorId = creatorId ?: 0L,
        deadline = deadline ?: "",
        status = status ?: ""
    )
}
