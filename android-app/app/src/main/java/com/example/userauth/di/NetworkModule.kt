package com.example.userauth.di

import com.example.userauth.data.api.ApiService
import com.example.userauth.data.api.dto.CompetitionDto
import com.example.userauth.data.api.dto.CompetitionDtoDeserializer
import com.example.userauth.data.api.dto.EvaluationModelDto
import com.example.userauth.data.api.dto.EvaluationModelDtoDeserializer
import com.example.userauth.data.api.AuthApiService
import com.example.userauth.data.api.AuthInterceptor
import com.example.userauth.data.api.FruitApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network-related dependencies
 * Provides Retrofit configuration with JWT authentication and logging
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Base URL for the backend API
    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    
    // Network timeouts
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(EvaluationModelDto::class.java, EvaluationModelDtoDeserializer())
            .registerTypeAdapter(CompetitionDto::class.java, CompetitionDtoDeserializer())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideCharsetInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val body = originalRequest.body
            if (body != null) {
                val contentType = body.contentType()
                if (contentType != null && contentType.type == "application" && contentType.subtype == "json") {
                    // Always ensure UTF-8 encoding
                    val buffer = okio.Buffer()
                    body.writeTo(buffer)
                    val bytes = buffer.readByteArray()
                    val charset = StandardCharsets.UTF_8
                    val newContentType = "application/json; charset=${charset.name()}"
                    val newBody = okhttp3.RequestBody.create(
                        newContentType.toMediaType(),
                        bytes
                    )
                    val newRequest = originalRequest.newBuilder()
                        .header("Content-Type", newContentType)
                        .method(originalRequest.method, newBody)
                        .build()
                    return@Interceptor chain.proceed(newRequest)
                }
            }
            chain.proceed(originalRequest)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        charsetInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(charsetInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFruitApiService(retrofit: Retrofit): FruitApiService {
        return retrofit.create(FruitApiService::class.java)
    }
}

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: java.lang.reflect.Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(formatter))
    }

    override fun deserialize(
        json: JsonElement?,
        typeT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        if (json == null) return null
        
        // Try string format first: "2026-02-06T12:58:43"
        json.asString?.let { str ->
            return try {
                LocalDateTime.parse(str, formatter)
            } catch (e: Exception) {
                null
            }
        }
        
        // Try array format: [2026,2,6,12,58,43]
        json.asJsonArray?.let { arr ->
            if (arr.size() >= 6) {
                return try {
                    LocalDateTime.of(
                        arr.get(0).asInt,
                        arr.get(1).asInt,
                        arr.get(2).asInt,
                        arr.get(3).asInt,
                        arr.get(4).asInt,
                        arr.get(5).asInt
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
        
        return null
    }
}