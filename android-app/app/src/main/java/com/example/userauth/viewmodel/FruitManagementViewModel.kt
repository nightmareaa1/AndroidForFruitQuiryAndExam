package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.model.Fruit
import com.example.userauth.data.repository.FruitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FruitManagementUiState(
    val fruits: List<Fruit> = emptyList(),
    val selectedFruit: Fruit? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

@HiltViewModel
class FruitManagementViewModel @Inject constructor(
    private val repository: FruitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FruitManagementUiState())
    val uiState: StateFlow<FruitManagementUiState> = _uiState.asStateFlow()

    init {
        loadFruits()
    }

    fun loadFruits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAllFruits()
                .onSuccess { fruits ->
                    _uiState.value = _uiState.value.copy(
                        fruits = fruits,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "加载失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun selectFruit(fruit: Fruit) {
        _uiState.value = _uiState.value.copy(selectedFruit = fruit)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedFruit = null)
    }

    fun createFruit(name: String, description: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.createFruit(name, description)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "创建成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "创建失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun updateFruit(id: Long, name: String, description: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.updateFruit(id, name, description)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "更新成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "更新失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun deleteFruit(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.deleteFruit(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "删除成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "删除失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun addNutritionData(fruitId: Long, componentName: String, value: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.addNutritionData(fruitId, componentName, value)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "添加成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "添加失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun addFlavorData(fruitId: Long, componentName: String, value: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.addFlavorData(fruitId, componentName, value)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "添加成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "添加失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun deleteNutritionData(fruitId: Long, dataId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.deleteNutritionData(fruitId, dataId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "删除成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "删除失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun deleteFlavorData(fruitId: Long, dataId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.deleteFlavorData(fruitId, dataId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        success = "删除成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = "删除失败: ${e.message}",
                        isLoading = false
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = null)
    }
}
