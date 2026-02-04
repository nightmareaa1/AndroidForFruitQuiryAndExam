package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import com.example.userauth.data.model.Competition

class CompetitionManagementViewModel : ViewModel() {
    private val _competitions = MutableStateFlow<List<Competition>>(listOf(
        Competition(id = UUID.randomUUID().toString(), name = "初始赛事", date = "2026-02-01")
    ))
    val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()

    fun addCompetition(name: String, date: String) {
        val c = Competition(UUID.randomUUID().toString(), name, date)
        _competitions.value = _competitions.value + c
    }

    fun deleteCompetition(id: String) {
        _competitions.value = _competitions.value.filter { it.id != id }
    }
}
