package com.example.userauth.domain.model

import org.junit.Assert.*
import org.junit.Test

class AuthTokenTest {

    @Test
    fun authToken_creation_succeeds() {
        val token = AuthToken(
            token = "abc123",
            username = "testuser",
            roles = listOf("USER", "ADMIN")
        )

        assertEquals("abc123", token.token)
        assertEquals("testuser", token.username)
        assertEquals(2, token.roles.size)
    }

    @Test
    fun authToken_emptyRoles_handlesCorrectly() {
        val token = AuthToken(
            token = "abc123",
            username = "testuser",
            roles = emptyList()
        )

        assertTrue(token.roles.isEmpty())
    }

    @Test
    fun authToken_copy_worksCorrectly() {
        val original = AuthToken("token1", "user1", listOf("USER"))
        val copy = original.copy(token = "token2")

        assertEquals("token2", copy.token)
        assertEquals("user1", copy.username)
        assertEquals(listOf("USER"), copy.roles)
    }

    @Test
    fun authToken_equals_correctBehavior() {
        val token1 = AuthToken("token", "user", listOf("USER"))
        val token2 = AuthToken("token", "user", listOf("USER"))
        val token3 = AuthToken("different", "user", listOf("USER"))

        assertEquals(token1, token2)
        assertNotEquals(token1, token3)
    }

    @Test
    fun authToken_hashCode_consistent() {
        val token1 = AuthToken("token", "user", listOf("USER"))
        val token2 = AuthToken("token", "user", listOf("USER"))

        assertEquals(token1.hashCode(), token2.hashCode())
    }

    @Test
    fun authToken_toString_containsFields() {
        val token = AuthToken("token", "user", listOf("USER"))
        val str = token.toString()

        assertTrue(str.contains("token"))
        assertTrue(str.contains("user"))
    }
}
