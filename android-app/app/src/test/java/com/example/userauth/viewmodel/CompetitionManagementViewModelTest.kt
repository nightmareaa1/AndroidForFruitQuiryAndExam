package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import com.example.userauth.data.model.Competition

class CompetitionManagementViewModelTest {
    @Test
    fun initialCompetitionLoaded() {
        val vm = CompetitionManagementViewModel()
        val first = vm.competitions.value.firstOrNull()
        assertNotNull(first)
        assertEquals("初始赛事", first?.name)
    }

    @Test
    fun addCompetitionAddsItem() {
        val vm = CompetitionManagementViewModel()
        val before = vm.competitions.value.size
        vm.addCompetition("TestComp", "2026-02-10")
        val after = vm.competitions.value.size
        assertEquals(before + 1, after)
        assertTrue(vm.competitions.value.any { it.name == "TestComp" && it.date == "2026-02-10" })
    }

    @Test
    fun deleteCompetitionRemovesItem() {
        val vm = CompetitionManagementViewModel()
        vm.addCompetition("Temp", "2026-02-11")
        val id = vm.competitions.value.last().id
        val before = vm.competitions.value.size
        vm.deleteCompetition(id)
        val after = vm.competitions.value.size
        assertEquals(before - 1, after)
        assertFalse(vm.competitions.value.any { it.id == id })
    }
}
