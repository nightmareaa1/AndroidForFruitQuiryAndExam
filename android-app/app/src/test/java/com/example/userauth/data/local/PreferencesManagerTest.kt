package com.example.userauth.data.local

import android.content.Context
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

/**
 * Unit tests for PreferencesManager
 * Tests JWT token storage and retrieval functionality
 */
@RunWith(MockitoJUnitRunner::class)
class PreferencesManagerTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        whenever(context.getSharedPreferences("user_auth_prefs", Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenReturn(editor)
        whenever(editor.putBoolean(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenReturn(editor)

        preferencesManager = PreferencesManager(context)
    }

    @Test
    fun `saveAuthToken should store token in SharedPreferences`() {
        val token = "test_jwt_token"

        preferencesManager.saveAuthToken(token)

        verify(editor).putString("auth_token", token)
        verify(editor).apply()
    }

    @Test
    fun `getAuthToken should return stored token`() {
        val expectedToken = "test_jwt_token"
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn(expectedToken)

        val actualToken = preferencesManager.getAuthToken()

        assertEquals(expectedToken, actualToken)
    }

    @Test
    fun `getAuthToken should return null when no token stored`() {
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn(null)

        val token = preferencesManager.getAuthToken()

        assertNull(token)
    }

    @Test
    fun `saveUserInfo should store username and admin status`() {
        val username = "testuser"
        val isAdmin = true

        preferencesManager.saveUserInfo(username, isAdmin)

        verify(editor).putString("username", username)
        verify(editor).putBoolean("is_admin", isAdmin)
        verify(editor).apply()
    }

    @Test
    fun `getUsername should return stored username`() {
        val expectedUsername = "testuser"
        whenever(sharedPreferences.getString("username", null)).thenReturn(expectedUsername)

        val actualUsername = preferencesManager.getUsername()

        assertEquals(expectedUsername, actualUsername)
    }

    @Test
    fun `isAdmin should return stored admin status`() {
        whenever(sharedPreferences.getBoolean("is_admin", false)).thenReturn(true)

        val isAdmin = preferencesManager.isAdmin()

        assertTrue(isAdmin)
    }

    @Test
    fun `isAdmin should return false by default`() {
        whenever(sharedPreferences.getBoolean("is_admin", false)).thenReturn(false)

        val isAdmin = preferencesManager.isAdmin()

        assertFalse(isAdmin)
    }

    @Test
    fun `clearAll should clear all stored data`() {
        whenever(editor.clear()).thenReturn(editor)
        
        preferencesManager.clearAll()

        verify(editor).clear()
        verify(editor).apply()
    }

    @Test
    fun `isLoggedIn should return true when token exists`() {
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn("test_token")

        val isLoggedIn = preferencesManager.isLoggedIn()

        assertTrue(isLoggedIn)
    }

    @Test
    fun `isLoggedIn should return false when no token exists`() {
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn(null)

        val isLoggedIn = preferencesManager.isLoggedIn()

        assertFalse(isLoggedIn)
    }
}