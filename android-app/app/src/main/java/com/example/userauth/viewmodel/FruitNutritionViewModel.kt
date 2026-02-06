package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.userauth.data.model.FruitNutrition
import com.example.userauth.data.model.QueryDataItem

class FruitNutritionViewModel(
    initialFruits: List<FruitNutrition> = listOf(
        FruitNutrition("芒果", 60, 14),
        FruitNutrition("香蕉", 89, 12)
    )
) : ViewModel() {
    private val _fruits = MutableStateFlow<List<FruitNutrition>>(initialFruits)
    val fruits: StateFlow<List<FruitNutrition>> = _fruits.asStateFlow()

    // Query results placeholder for UI testing
    private val _queryResults = MutableStateFlow<List<QueryDataItem>>(emptyList())
    val queryResults: StateFlow<List<QueryDataItem>> = _queryResults.asStateFlow()

    fun setFruits(list: List<FruitNutrition>) { _fruits.value = list }

    // Query results: nutrition vs flavor data for a given fruit
    fun query(type: String, fruit: String) {
        _queryResults.value = when (fruit) {
            "芒果" -> if (type == "营养成分") listOf(
                QueryDataItem("热量", 60.0),
                QueryDataItem("糖分", 14.0)
            ) else listOf(
                QueryDataItem("甜度", 7.0),
                QueryDataItem("香气", 8.0)
            )
            "香蕉" -> if (type == "营养成分") listOf(
                QueryDataItem("热量", 89.0),
                QueryDataItem("糖分", 12.0)
            ) else listOf(
                QueryDataItem("甜度", 6.5),
                QueryDataItem("香气", 6.0)
            )
            else -> emptyList()
        }
    }
}
