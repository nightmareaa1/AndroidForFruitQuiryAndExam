package com.example.userauth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.userauth.ui.screen.LoginScreen
import com.example.userauth.ui.screen.AdminScreen
import com.example.userauth.ui.screen.ModelManagementScreen
import com.example.userauth.ui.screen.CompetitionManagementScreen
import com.example.userauth.ui.screen.ScoreScreen
import com.example.userauth.ui.screen.MainScreen
import com.example.userauth.ui.screen.DataDisplayScreen
import com.example.userauth.ui.screen.DataDisplayDetailScreen
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.ui.screen.RegisterScreen
import com.example.userauth.viewmodel.AuthViewModel

/**
 * Navigation graph for the application
 * Handles navigation between login, register, and main screens
 * Automatically navigates to main screen if user is already logged in
 */
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
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
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
                }
            )
        }
        // Data display list and detail routes
        composable(Screen.DataDisplay.route) {
            DataDisplayScreen(onBack = { navController.popBackStack() }, onCardClick = { id -> navController.navigate(Screen.DataDisplayDetail.route.replace("{submissionId}", id)) }, viewModel = DataDisplayViewModel())
        }
        composable(Screen.DataDisplayDetail.route) { backStack ->
            val id = backStack.arguments?.getString("submissionId") ?: return@composable
            DataDisplayDetailScreen(submissionId = id, onBack = { navController.popBackStack() })
        }
        // Admin route
        composable(Screen.Admin.route) {
            AdminScreen(
                onBack = { navController.popBackStack() },
                onNavigateToModelManagement = { navController.navigate(Screen.ModelManagement.route) },
                onNavigateToCompetitionManagement = { navController.navigate(Screen.CompetitionManagement.route) }
            )
        }
        // Model management route (admin only)
        composable(Screen.ModelManagement.route) {
            ModelManagementScreen(onBack = { navController.popBackStack() })
        }
        // Score screen (评委评分)
        composable(Screen.Score.route) {
            ScoreScreen(onBack = { navController.popBackStack() })
        }
        // Competition management route (admin only)
        composable(Screen.CompetitionManagement.route) {
            CompetitionManagementScreen(onBack = { navController.popBackStack() })
        }
        
        // TODO: Add more navigation destinations for other features
    }
}
