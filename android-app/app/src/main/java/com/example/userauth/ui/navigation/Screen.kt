package com.example.userauth.ui.navigation

/**
 * Sealed class representing different screens in the app
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Admin : Screen("admin")
    object Competition : Screen("competition")
    object FruitNutrition : Screen("fruit_nutrition")
    
    // TODO: Add more screen destinations as needed
}