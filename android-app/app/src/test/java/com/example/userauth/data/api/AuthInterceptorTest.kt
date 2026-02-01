package com.example.userauth.data.api

import com.example.userauth.data.local.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

/**
 * Unit tests for AuthInterceptor
 * Tests JWT token injection into HTTP requests
 */
@RunWith(MockitoJUnitRunner::class)
class AuthInterceptorTest {

    @Mock
    private lateinit var preferencesManager: PreferencesManager

    @Mock
    private lateinit var chain: Interceptor.Chain

    @Mock
    private lateinit var originalRequest: Request

    @Mock
    private lateinit var response: Response

    private lateinit var authInterceptor: AuthInterceptor

    @Before
    fun setup() {
        authInterceptor = AuthInterceptor(preferencesManager)
    }

    @Test
    fun `intercept should add Authorization header when token exists`() {
        val token = "test_jwt_token"
        val requestBuilder = Request.Builder().url("http://example.com")
        val originalRequest = requestBuilder.build()
        
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(preferencesManager.getAuthToken()).thenReturn(token)
        whenever(chain.proceed(org.mockito.kotlin.any())).thenReturn(response)

        authInterceptor.intercept(chain)

        verify(chain).proceed(org.mockito.kotlin.argThat { request ->
            request.header("Authorization") == "Bearer $token"
        })
    }

    @Test
    fun `intercept should not add Authorization header when no token exists`() {
        val requestBuilder = Request.Builder().url("http://example.com")
        val originalRequest = requestBuilder.build()
        
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(preferencesManager.getAuthToken()).thenReturn(null)
        whenever(chain.proceed(org.mockito.kotlin.any())).thenReturn(response)

        authInterceptor.intercept(chain)

        verify(chain).proceed(org.mockito.kotlin.argThat { request ->
            request.header("Authorization") == null
        })
    }

    @Test
    fun `intercept should not add Authorization header when token is empty`() {
        val requestBuilder = Request.Builder().url("http://example.com")
        val originalRequest = requestBuilder.build()
        
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(preferencesManager.getAuthToken()).thenReturn("")
        whenever(chain.proceed(org.mockito.kotlin.any())).thenReturn(response)

        authInterceptor.intercept(chain)

        verify(chain).proceed(org.mockito.kotlin.argThat { request ->
            request.header("Authorization") == null
        })
    }
}