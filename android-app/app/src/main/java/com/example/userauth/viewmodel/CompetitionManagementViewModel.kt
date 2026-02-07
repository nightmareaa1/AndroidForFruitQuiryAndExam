package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.userauth.data.model.Competition

class CompetitionManagementViewModel : ViewModel() {
    private var idCounter = 0L
    
    private fun generateId(): Long = ++idCounter

    private val _competitions = MutableStateFlow<List<Competition>>(listOf(
        Competition(
            id = generateId(),
            name = "初始赛事",
            modelId = 1L,
            creatorId = 1L,
            deadline = "2026-02-01",
            status = "ACTIVE"
        )
    ))
    val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()

    fun addCompetition(name: String, date: String) {
        val c = Competition(
            id = generateId(),
            name = name,
            modelId = 1L,
            creatorId = 1L,
            deadline = date,
            status = "ACTIVE"
        )
        _competitions.value = _competitions.value + c
    }

    fun deleteCompetition(id: Long) {
        _competitions.value = _competitions.value.filter { it.id != id }
    }
}
