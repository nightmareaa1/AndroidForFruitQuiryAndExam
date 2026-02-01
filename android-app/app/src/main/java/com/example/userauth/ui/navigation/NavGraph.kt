package com.example.userauth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.userauth.ui.screen.LoginScreen
import com.example.userauth.ui.screen.MainScreen
import com.example.userauth.ui.screen.RegisterScreen
import com.example.userauth.viewmodel.AuthViewModel

/**
 * Navigation graph for the application
 * Handles navigation between login, register, and main screens
 * Automatically navigates to main screen if user is already logged in
 */
@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    
    // Determine start destination based on login state
    val startDestination = if (authViewModel.isLoggedIn()) {
        Screen.Main.route
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        // Clear back stack to prevent going back to login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        // Clear back stack to prevent going back to main
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCompetition = {
                    navController.navigate(Screen.Competition.route)
                },
                onNavigateToFruitNutrition = {
                    navController.navigate(Screen.FruitNutrition.route)
                }
            )
        }
        
        // TODO: Add more navigation destinations for other features
    }
}