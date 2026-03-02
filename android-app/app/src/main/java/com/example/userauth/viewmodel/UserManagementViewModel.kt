package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.dto.UserDto
import com.example.userauth.data.repository.AuthRepository
import com.example.userauth.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val currentUsername: String?
        get() = authRepository.getUsername()

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.getAllUsers()
                .onSuccess { _users.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun createUser(username: String, password: String, isAdmin: Boolean, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.createUser(username, password, isAdmin)
                .onSuccess {
                    loadUsers()
                    onSuccess()
                }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.deleteUser(userId)
                .onSuccess { loadUsers() }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun toggleAdminRole(user: UserDto) {
        val makeAdmin = !user.roles.contains("ADMIN")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.updateUserRole(user.id, makeAdmin)
                .onSuccess { loadUsers() }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
