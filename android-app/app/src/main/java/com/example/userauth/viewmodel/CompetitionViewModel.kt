package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.EvaluationApiService
import com.example.userauth.data.api.dto.CompetitionDto
import com.example.userauth.data.model.Competition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompetitionViewModel @Inject constructor(
    private val evaluationApiService: EvaluationApiService
) : ViewModel() {

    private val _competitions = MutableStateFlow<List<Competition>>(emptyList())
    val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCompetitions()
    }

    fun loadCompetitions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = evaluationApiService.getCompetitions()
                if (response.isSuccessful) {
                    _competitions.value = response.body()?.map { it.toCompetition() } ?: emptyList()
                } else {
                    _errorMessage.value = "加载赛事列表失败: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络错误: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

fun CompetitionDto.toCompetition(): Competition {
    return Competition(
        id = id,
        name = name,
        modelId = modelId ?: 0,
        creatorId = creatorId ?: 0,
        deadline = deadline?.substring(0, 10) ?: "",
        status = status ?: "ACTIVE"
    )
}
