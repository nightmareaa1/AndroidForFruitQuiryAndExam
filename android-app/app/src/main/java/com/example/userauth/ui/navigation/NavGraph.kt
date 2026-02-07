package com.example.userauth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.userauth.ui.screen.LoginScreen
import com.example.userauth.ui.screen.AdminScreen
import com.example.userauth.ui.screen.ModelManagementScreen
import com.example.userauth.ui.screen.CompetitionManagementScreen
import com.example.userauth.ui.screen.CompetitionEditScreen
import com.example.userauth.ui.screen.CompetitionAddScreen
import com.example.userauth.ui.screen.ScoreScreen
import com.example.userauth.ui.screen.FruitNutritionScreen
import com.example.userauth.ui.screen.MainScreen
import com.example.userauth.ui.screen.DataDisplayScreen
import com.example.userauth.ui.screen.DataDisplayDetailScreen
import com.example.userauth.ui.screen.EntryAddScreen
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.ui.screen.RegisterScreen
import com.example.userauth.viewmodel.AuthViewModel
import com.example.userauth.ui.screen.CompetitionScreen
import com.example.userauth.ui.screen.RatingScreen

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
        // Data display list route
        composable(Screen.DataDisplay.route) { backStack ->
            val competitionId = backStack.arguments?.getString("competitionId")?.toLongOrNull()
            if (competitionId == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }
            val dataViewModel: DataDisplayViewModel = hiltViewModel()
            
            // Load data for the competition
            LaunchedEffect(competitionId) {
                dataViewModel.loadSubmissions(competitionId)
            }
            
            DataDisplayScreen(
                onBack = { navController.popBackStack() },
                onCardClick = { id ->
                    navController.navigate(Screen.dataDisplayDetail(competitionId, id))
                },
                viewModel = dataViewModel
            )
        }
        // Data display detail route
        composable(
            route = Screen.DataDisplayDetail.route,
            arguments = listOf(
                navArgument("competitionId") { type = NavType.LongType },
                navArgument("submissionId") { type = NavType.StringType }
            )
        ) { backStack ->
            val competitionId = backStack.arguments?.getLong("competitionId") ?: 0L
            val submissionId = backStack.arguments?.getString("submissionId") ?: ""
            
            if (submissionId.isBlank()) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }
            
            val dataViewModel: DataDisplayViewModel = hiltViewModel()
            
            // Load data if not loaded
            LaunchedEffect(competitionId) {
                dataViewModel.loadSubmissions(competitionId)
            }
            
            DataDisplayDetailScreen(
                submissionId = submissionId,
                onBack = { navController.popBackStack() },
                viewModel = dataViewModel
            )
        }
        // Admin route - requires admin privileges
        composable(Screen.Admin.route) {
            // Check admin permission at navigation level
            if (!authViewModel.isCurrentUserAdmin()) {
                // Non-admin users are redirected to main screen
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Admin.route) { inclusive = true }
                    }
                }
                return@composable
            }
            AdminScreen(
                onBack = { navController.popBackStack() },
                onNavigateToModelManagement = { navController.navigate(Screen.ModelManagement.route) },
                onNavigateToCompetitionManagement = { navController.navigate(Screen.CompetitionManagement.route) }
            )
        }
        // Model management route - requires admin privileges
        composable(Screen.ModelManagement.route) {
            // Check admin permission at navigation level
            if (!authViewModel.isCurrentUserAdmin()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.ModelManagement.route) { inclusive = true }
                    }
                }
                return@composable
            }
            ModelManagementScreen(onBack = { navController.popBackStack() })
        }
        // Score screen (评委评分)
        composable(Screen.Score.route) { backStack ->
            val competitionId = backStack.arguments?.getString("competitionId")?.toLongOrNull()
            if (competitionId == null) {
                // Invalid competition ID - navigate back
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }
            ScoreScreen(
                competitionId = competitionId,
                onBack = { navController.popBackStack() }
            )
        }
        // Competition screen (赛事评价列表)
        composable(Screen.Competition.route) {
            CompetitionScreen(
                onBack = { navController.popBackStack() },
                onCompetitionClick = { competitionId ->
                    navController.navigate(Screen.Score.route.replace("{competitionId}", competitionId.toString()))
                },
                onNavigateToDataDisplay = { competitionId ->
                    navController.navigate(Screen.DataDisplay.route.replace("{competitionId}", competitionId.toString()))
                },
                onNavigateToEntryAdd = { competitionId, competitionName ->
                    navController.navigate(Screen.EntryAdd.route
                        .replace("{competitionId}", competitionId.toString())
                        .replace("{competitionName}", java.net.URLEncoder.encode(competitionName, "UTF-8")))
                }
            )
        }
        // Fruit Nutrition screen
        composable(Screen.FruitNutrition.route) {
            FruitNutritionScreen(
                onBack = { navController.popBackStack() },
                onFruitClick = { /* optional: navigate to detail in future */ },
                viewModel = hiltViewModel()
            )
        }
        // Competition management route - requires admin privileges
        composable(Screen.CompetitionManagement.route) {
            if (!authViewModel.isCurrentUserAdmin()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.CompetitionManagement.route) { inclusive = true }
                    }
                }
                return@composable
            }
            CompetitionManagementScreen(
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { competitionId ->
                    navController.navigate(Screen.competitionEdit(competitionId))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.CompetitionAdd.route)
                }
            )
        }

        // Competition edit route - requires admin privileges
        composable(Screen.CompetitionEdit.route) { backStack ->
            if (!authViewModel.isCurrentUserAdmin()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.CompetitionEdit.route) { inclusive = true }
                    }
                }
                return@composable
            }
            val competitionId = backStack.arguments?.getString("competitionId")?.toLongOrNull()
            if (competitionId == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }
            CompetitionEditScreen(
                competitionId = competitionId,
                onBack = { navController.popBackStack() }
            )
        }

        // Competition add route - requires admin privileges
        composable(Screen.CompetitionAdd.route) {
            if (!authViewModel.isCurrentUserAdmin()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.CompetitionAdd.route) { inclusive = true }
                    }
                }
                return@composable
            }
            CompetitionAddScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Rating screen for judges
        composable(Screen.Rating.route) { backStack ->
            val competitionId = backStack.arguments?.getString("competitionId")?.toLongOrNull()
            if (competitionId == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }
            RatingScreen(
                competitionId = competitionId,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Entry add screen - for users to submit their entries
        composable(
            route = Screen.EntryAdd.route,
            arguments = listOf(
                navArgument("competitionId") { type = NavType.LongType },
                navArgument("competitionName") { type = NavType.StringType }
            )
        ) { backStack ->
            val competitionId = backStack.arguments?.getLong("competitionId") ?: 0L
            val competitionName = backStack.arguments?.getString("competitionName") ?: ""
            
            EntryAddScreen(
                competitionId = competitionId,
                competitionName = competitionName,
                onBack = { navController.popBackStack() },
                onSubmissionSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}
