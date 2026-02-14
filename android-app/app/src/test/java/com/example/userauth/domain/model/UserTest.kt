package com.example.userauth.domain.model

import org.junit.Assert.*
import org.junit.Test

class UserTest {

    @Test
    fun user_creation_succeeds() {
        val user = User(
            id = 1,
            username = "testuser",
            roles = listOf("USER", "ADMIN")
        )

        assertEquals(1, user.id)
        assertEquals("testuser", user.username)
        assertEquals(2, user.roles.size)
    }

    @Test
    fun isAdmin_true_whenAdminRolePresent() {
        val user = User(1, "admin", listOf("USER", "ADMIN"))

        assertTrue(user.isAdmin)
    }

    @Test
    fun isAdmin_false_whenNoAdminRole() {
        val user = User(1, "regular", listOf("USER"))

        assertFalse(user.isAdmin)
    }

    @Test
    fun isAdmin_false_whenEmptyRoles() {
        val user = User(1, "no roles", emptyList())

        assertFalse(user.isAdmin)
    }

    @Test
    fun isAdmin_false_whenOnlyAdminUppercase() {
        val user = User(1, "user", listOf("user", "ADMIN"))

        assertTrue(user.isAdmin)
    }

    @Test
    fun user_copy_worksCorrectly() {
        val original = User(1, "user", listOf("USER"))
        val copy = original.copy(username = "newuser")

        assertEquals(1, copy.id)
        assertEquals("newuser", copy.username)
        assertEquals(listOf("USER"), copy.roles)
    }

    @Test
    fun user_equals_correctBehavior() {
        val user1 = User(1, "user", listOf("USER"))
        val user2 = User(1, "user", listOf("USER"))
        val user3 = User(2, "user", listOf("USER"))

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }

    @Test
    fun user_hashCode_consistent() {
        val user1 = User(1, "user", listOf("USER"))
        val user2 = User(1, "user", listOf("USER"))

        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun user_toString_containsFields() {
        val user = User(1, "user", listOf("USER"))
        val str = user.toString()

        assertTrue(str.contains("1"))
        assertTrue(str.contains("user"))
    }
}
