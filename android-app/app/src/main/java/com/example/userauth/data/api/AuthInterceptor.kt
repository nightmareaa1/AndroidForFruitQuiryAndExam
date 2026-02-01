package com.example.userauth.data.api

import com.example.userauth.data.local.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that adds JWT token to requests
 * Automatically adds Authorization header with Bearer token for authenticated requests
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get the auth token from preferences
        val token = preferencesManager.getAuthToken()
        
        // If no token, proceed with original request
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Add Authorization header with Bearer token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
}