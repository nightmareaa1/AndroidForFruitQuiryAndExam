package com.example.userauth.data.model

data class Fruit(
    val id: Long,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val nutritionData: List<FruitDataItem>,
    val flavorData: List<FruitDataItem>
)

data class FruitDataItem(
    val componentName: String,
    val value: Double
)
