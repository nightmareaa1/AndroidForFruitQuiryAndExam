package com.example.userauth.data.local

import android.util.Base64
import com.auth0.android.jwt.JWT
import org.json.JSONObject

/**
 * Utility class for parsing JWT tokens and extracting claims.
 * Used to get real-time admin status from token instead of relying on cached values.
 */
object JwtTokenParser {

    /**
     * Extract isAdmin claim from JWT token.
     * @param token JWT token string
     * @return true if user is admin, false if not or if token is invalid
     */
    fun extractIsAdmin(token: String?): Boolean {
        if (token.isNullOrBlank()) return false

        return try {
            val jwt = JWT(token)
            jwt.getClaim("isAdmin").asBoolean() ?: false
        } catch (e: Exception) {
            // Fallback to manual parsing if JWT library fails
            extractIsAdminManually(token)
        }
    }

    /**
     * Extract username from JWT token.
     * @param token JWT token string
     * @return username or null if token is invalid
     */
    fun extractUsername(token: String?): String? {
        if (token.isNullOrBlank()) return null

        return try {
            val jwt = JWT(token)
            jwt.getClaim("username").asString()
                ?: jwt.getClaim("sub").asString()
        } catch (e: Exception) {
            extractClaimManually(token, "username")
                ?: extractClaimManually(token, "sub")
        }
    }

    /**
     * Extract userId from JWT token.
     * @param token JWT token string
     * @return userId or null if token is invalid
     */
    fun extractUserId(token: String?): Long? {
        if (token.isNullOrBlank()) return null

        return try {
            val jwt = JWT(token)
            jwt.getClaim("userId").asLong()
        } catch (e: Exception) {
            extractClaimManually(token, "userId")?.toLongOrNull()
        }
    }

    /**
     * Extract roles from JWT token.
     * @param token JWT token string
     * @return list of roles or empty list if token is invalid
     */
    fun extractRoles(token: String?): List<String> {
        if (token.isNullOrBlank()) return emptyList()

        return try {
            val jwt = JWT(token)
            jwt.getClaim("roles").asList(String::class.java) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Manual parsing fallback for isAdmin claim.
     * Parses the payload part of JWT token manually.
     */
    private fun extractIsAdminManually(token: String): Boolean {
        return try {
            val claims = parseTokenPayload(token)
            claims?.optBoolean("isAdmin") ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Manual parsing fallback for extracting a claim.
     */
    private fun extractClaimManually(token: String, claimName: String): String? {
        return try {
            val claims = parseTokenPayload(token)
            claims?.optString(claimName)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse JWT token payload (middle part) into JSONObject.
     */
    private fun parseTokenPayload(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val decodedPayload = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            JSONObject(String(decodedPayload))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if token is expired.
     * @param token JWT token string
     * @return true if expired or invalid, false if valid and not expired
     */
    fun isTokenExpired(token: String?): Boolean {
        if (token.isNullOrBlank()) return true

        return try {
            val jwt = JWT(token)
            jwt.isExpired(0)
        } catch (e: Exception) {
            true
        }
    }
}
