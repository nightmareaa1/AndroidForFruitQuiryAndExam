package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.FruitDataResponse
import com.example.userauth.data.api.FruitOption
import com.example.userauth.data.repository.FruitDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FruitDataUiState(
    val dataTypes: List<String> = emptyList(),
    val fruits: List<FruitOption> = emptyList(),
    val queryResults: List<FruitDataResponse> = emptyList(),
    val selectedFruit: FruitOption? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FruitDataViewModel @Inject constructor(
    private val repository: FruitDataRepository,
    private val adminApi: com.example.userauth.data.api.FruitDataAdminApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(FruitDataUiState())
    val uiState: StateFlow<FruitDataUiState> = _uiState.asStateFlow()

    init {
        loadDataTypes()
        loadFruits()
    }

    fun loadDataTypes() {
        viewModelScope.launch {
            adminApi.getAllDataTypes()
                .takeIf { it.isSuccessful }
                ?.body()
                ?.let { dataTypes ->
                    _uiState.value = _uiState.value.copy(
                        dataTypes = dataTypes.map { it.dataType }
                    )
                }
        }
    }

    fun loadFruits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getFruits()
                .onSuccess { fruits ->
                    _uiState.value = _uiState.value.copy(fruits = fruits, isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "加载水果列表失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun selectFruit(fruit: FruitOption?) {
        _uiState.value = _uiState.value.copy(selectedFruit = fruit)
    }

    fun query(fruitName: String, dataType: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.query(fruitName, dataType)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        queryResults = listOf(result),
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "查询失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
