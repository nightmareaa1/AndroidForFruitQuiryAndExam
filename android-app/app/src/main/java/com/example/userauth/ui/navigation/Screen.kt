package com.example.userauth.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Admin : Screen("admin")
    object Competition : Screen("competition")
    object FruitNutrition : Screen("fruit_nutrition")
    object ModelManagement : Screen("model_management")
    object CompetitionManagement : Screen("competition_management")
    object Score : Screen("score")
    object RadarDetail : Screen("radar_detail/{submissionId}")
    object DataDisplayDetail : Screen("data-display-detail/{submissionId}")
    object DataDisplay : Screen("data-display")
}
