package com.example.userauth.data.api.dto

import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
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

data class EvaluationModelDto(
    val id: Long,
    val name: String,
    val description: String?,
    val parameters: List<EvaluationParameterDto>,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class EvaluationParameterDto(
    val id: Long,
    val name: String,
    val weight: Int,
    val maxScore: Int? = null
)

data class EvaluationModelRequest(
    val name: String,
    val description: String? = null,
    val parameters: List<EvaluationParameterRequest>
)

data class EvaluationParameterRequest(
    val name: String,
    val weight: Int
)

/**
 * Custom deserializer for EvaluationModelDto to handle array format dates
 */
class EvaluationModelDtoDeserializer : JsonDeserializer<EvaluationModelDto> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EvaluationModelDto {
        if (json == null || !json.isJsonObject) {
            throw JsonParseException("Expected JsonObject")
        }
        
        val obj = json.asJsonObject
        
        val id = obj.get("id").asLong
        val name = obj.get("name").asString
        val description = obj.get("description")?.asString
        val parameters = context?.deserialize<List<EvaluationParameterDto>>(
            obj.get("parameters"),
            object : TypeToken<List<EvaluationParameterDto>>() {}.type
        ) ?: emptyList()
        
        // Handle createdAt - could be string or array
        val createdAt = parseDateTime(obj.get("createdAt"))
        // Handle updatedAt - could be string or array
        val updatedAt = parseDateTime(obj.get("updatedAt"))
        
        return EvaluationModelDto(
            id = id,
            name = name,
            description = description,
            parameters = parameters,
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

fun EvaluationModelDto.toDomainModel(): EvaluationModel {
    return EvaluationModel(
        id = id.toString(),
        name = name,
        description = description ?: "",
        parameters = parameters.map { 
            EvaluationParameter(
                id = it.id.toString(),
                name = it.name,
                weight = it.weight,
                maxScore = it.maxScore ?: 10
            )
        }
    )
}
