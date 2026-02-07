package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import com.example.userauth.data.repository.EvaluationModelRepository
import javax.inject.Inject

@HiltViewModel
class ModelViewModel @Inject constructor(
    private val repository: EvaluationModelRepository
) : ViewModel() {

    private val _models = MutableStateFlow<List<EvaluationModel>>(emptyList())
    val models: StateFlow<List<EvaluationModel>> = _models.asStateFlow()

    private val _selectedModel = MutableStateFlow<EvaluationModel?>(null)
    val selectedModel: StateFlow<EvaluationModel?> = _selectedModel.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadModels()
    }

    fun loadModels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getAllModels()
                .onSuccess { models ->
                    _models.value = models
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }

    fun selectModel(model: EvaluationModel?) {
        _selectedModel.value = model
    }

    fun addModel(name: String, parameters: List<EvaluationParameter>) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.createModel(name, parameters)
                .onSuccess {
                    loadModels()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun updateModel(updatedModel: EvaluationModel) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.updateModel(
                id = updatedModel.id.toLongOrNull() ?: return@launch,
                name = updatedModel.name,
                parameters = updatedModel.parameters
            )
                .onSuccess {
                    loadModels()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun deleteModel(id: String) {
        viewModelScope.launch {
            repository.deleteModel(id.toLongOrNull() ?: return@launch)
                .onSuccess {
                    loadModels()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun getModelById(id: String): EvaluationModel? {
        return _models.value.find { it.id == id }
    }
}
