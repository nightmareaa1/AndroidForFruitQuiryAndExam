package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import com.example.userauth.data.model.EvaluationModel

class ModelViewModel : ViewModel() {
    private val _models = MutableStateFlow<List<EvaluationModel>>(listOf(
        EvaluationModel(id = UUID.randomUUID().toString(), name = "Mango Model", weight = 50)
    ))
    val models: StateFlow<List<EvaluationModel>> = _models.asStateFlow()

    fun addModel(name: String, weight: Int) {
        val newModel = EvaluationModel(UUID.randomUUID().toString(), name, weight)
        _models.value = _models.value + newModel
    }

    fun deleteModel(id: String) {
        _models.value = _models.value.filter { it.id != id }
    }
}
