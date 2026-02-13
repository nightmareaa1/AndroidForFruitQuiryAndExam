package com.example.userauth.di

import android.content.Context
import android.content.pm.PackageManager
import com.example.userauth.data.api.ApiService
import com.example.userauth.data.api.AuthApiService
import com.example.userauth.data.api.AuthInterceptor
import com.example.userauth.data.api.FruitApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network-related dependencies
 * API URL配置说明：
 * - 开发环境: 修改 app/build.gradle 中 manifestPlaceholders 的 IP 地址
 * - 生产环境: 修改 app/build.gradle 中 release 的地址
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiBaseUrl(@ApplicationContext context: Context): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            appInfo.metaData.getString("API_BASE_URL") ?: "http://localhost:8080/api/"
        } catch (e: Exception) {
            "http://localhost:8080/api/"
        }
    }

    // Network timeouts
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
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
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        @ApplicationContext context: Context
    ): Retrofit {
        val baseUrl = provideApiBaseUrl(context)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
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