package com.example.userauth.data.local

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class JwtTokenParserTest {

    @Before
    fun setup() {
        mockkStatic(Base64::class)
        every { Base64.decode(any<String>(), any()) } answers {
            java.util.Base64.getUrlDecoder().decode(firstArg<String>())
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(Base64::class)
    }

    @Test
    fun extractIsAdmin_withValidToken_returnsTrue() {
        // JWT token with isAdmin=true
        // Header: {"alg":"none","typ":"JWT"}
        // Payload: {"isAdmin":true}
        val token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc0FkbWluIjp0cnVlfQ."

        val result = JwtTokenParser.extractIsAdmin(token)

        assertTrue(result)
    }

    @Test
    fun extractIsAdmin_withValidToken_returnsFalse() {
        // JWT token with isAdmin=false
        val token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc0FkbWluIjpmYWxzZX0."

        val result = JwtTokenParser.extractIsAdmin(token)

        assertFalse(result)
    }

    @Test
    fun extractIsAdmin_withNullToken_returnsFalse() {
        val result = JwtTokenParser.extractIsAdmin(null)

        assertFalse(result)
    }

    @Test
    fun extractIsAdmin_withEmptyToken_returnsFalse() {
        val result = JwtTokenParser.extractIsAdmin("")

        assertFalse(result)
    }

    @Test
    fun extractIsAdmin_withInvalidToken_returnsFalse() {
        val result = JwtTokenParser.extractIsAdmin("invalid-token")

        assertFalse(result)
    }

    @Test
    fun extractUsername_withValidToken_returnsUsername() {
        // Payload: {"username":"testuser"}
        val token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJ1c2VybmFtZSI6InRlc3R1c2VyIn0."

        val result = JwtTokenParser.extractUsername(token)

        assertEquals("testuser", result)
    }

    @Test
    fun extractUsername_withSubClaim_returnsUsername() {
        // Payload: {"sub":"johndoe"}
        val token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiJqb2huZG9lIn0."

        val result = JwtTokenParser.extractUsername(token)

        assertEquals("johndoe", result)
    }

    @Test
    fun extractUsername_withNullToken_returnsNull() {
        val result = JwtTokenParser.extractUsername(null)

        assertNull(result)
    }

    @Test
    fun extractUserId_withValidToken_returnsUserId() {
        // Payload: {"userId":123}
        val token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJ1c2VySWQiOjEyM30."

        val result = JwtTokenParser.extractUserId(token)

        assertEquals(123L, result)
    }

    @Test
    fun extractUserId_withNullToken_returnsNull() {
        val result = JwtTokenParser.extractUserId(null)

        assertNull(result)
    }

    @Test
    fun extractRoles_withValidToken_returnsRoles() {
        // Payload: {"roles":["ADMIN","USER"]}
        val token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXX0."

        val result = JwtTokenParser.extractRoles(token)

        assertEquals(listOf("ADMIN", "USER"), result)
    }

    @Test
    fun extractRoles_withNullToken_returnsEmptyList() {
        val result = JwtTokenParser.extractRoles(null)

        assertTrue(result.isEmpty())
    }

    @Test
    fun isTokenExpired_withNullToken_returnsTrue() {
        val result = JwtTokenParser.isTokenExpired(null)

        assertTrue(result)
    }

    @Test
    fun isTokenExpired_withInvalidToken_returnsTrue() {
        val result = JwtTokenParser.isTokenExpired("invalid")

        assertTrue(result)
    }
}
