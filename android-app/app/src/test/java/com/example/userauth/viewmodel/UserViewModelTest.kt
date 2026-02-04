package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import io.mockk.every
import io.mockk.mockk
import com.example.userauth.data.repository.AuthRepository

class UserViewModelTest {
    @Test
    fun loadsUserData_adminFlag() {
        val repo: AuthRepository = mockk()
        every { repo.getUsername() } returns "alice"
        every { repo.isAdmin() } returns true
        val vm = UserViewModel(repo)
        val state = vm.userState.value
        assertEquals("alice", state.username)
        assertTrue(state.isAdmin)
    }

    @Test
    fun loadsUserData_nonAdminFlag() {
        val repo: AuthRepository = mockk()
        every { repo.getUsername() } returns "bob"
        every { repo.isAdmin() } returns false
        val vm = UserViewModel(repo)
        val state = vm.userState.value
        assertEquals("bob", state.username)
        assertFalse(state.isAdmin)
    }

    @Test
    fun getCurrentUsername_returnsUsername() {
        val repo: AuthRepository = mockk()
        every { repo.getUsername() } returns "charlie"
        every { repo.isAdmin() } returns true
        val vm = UserViewModel(repo)
        assertEquals("charlie", vm.getCurrentUsername())
    }
}
