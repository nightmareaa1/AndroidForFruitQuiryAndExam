package com.example.userauth.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for local data storage using SharedPreferences
 * Handles JWT token storage and retrieval for authentication
 */
@Singleton
open class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_auth_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_ADMIN = "is_admin"
    }

    /**
     * Save JWT authentication token
     */
    open fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Get JWT authentication token
     */
    open fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Save user information
     */
    open fun saveUserInfo(username: String, isAdmin: Boolean) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }

    /**
     * Get username
     */
    open fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Check if user is admin by parsing the JWT token.
     * This ensures real-time permission status from the backend.
     */
    open fun isAdmin(): Boolean {
        val token = getAuthToken()
        return JwtTokenParser.extractIsAdmin(token)
    }

    /**
     * Get username from JWT token for real-time accuracy.
     * Falls back to cached value if token parsing fails.
     */
    open fun getUsernameFromToken(): String? {
        val token = getAuthToken()
        return JwtTokenParser.extractUsername(token)
    }

    /**
     * Clear all stored data (logout)
     */
    open fun clearAll() {
        prefs.edit().clear().apply()
    }

    /**
     * Check if user is logged in
     */
    open fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
}