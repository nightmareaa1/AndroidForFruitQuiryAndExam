package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.userauth.data.repository.AuthRepository

/**
 * ViewModel for admin/user information in the Android client.
 *
 * Responsibilities (per task 22.1):
 * - Retrieve current username
 * - Check whether the current user has admin privileges
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Local UI state representing current user information
    data class UserState(
        val username: String? = null,
        val isAdmin: Boolean = false
    )

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    init {
        // Initialize with current stored user data
        loadUser()
    }

    /** Load current user information from repository (sync read from storage) */
    fun loadUser() {
        val username = authRepository.getUsername()
        val isAdmin = authRepository.isAdmin()
        _userState.value = UserState(username = username, isAdmin = isAdmin)
    }

    /** Expose current username (nullable) */
    fun getCurrentUsername(): String? = _userState.value.username

    /** Expose whether the current user is an administrator */
    fun isCurrentUserAdmin(): Boolean = _userState.value.isAdmin
}
