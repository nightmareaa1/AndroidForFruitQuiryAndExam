package com.example.userauth.di

import com.example.userauth.data.api.ApiService
import com.example.userauth.data.api.AuthApiService
import com.example.userauth.data.api.AuthInterceptor
import com.example.userauth.data.api.EvaluationApiService
import com.example.userauth.data.api.FruitApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    // Using 10.0.2.2 for Android emulator to access localhost
    // In production, this should be the actual server URL
    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    
    // Network timeouts
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Add JWT token to requests
            .addInterceptor(loggingInterceptor) // Add logging for debugging
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Legacy API service (to be removed in future tasks)
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // Specific API services
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEvaluationApiService(retrofit: Retrofit): EvaluationApiService {
        return retrofit.create(EvaluationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFruitApiService(retrofit: Retrofit): FruitApiService {
        return retrofit.create(FruitApiService::class.java)
    }
}