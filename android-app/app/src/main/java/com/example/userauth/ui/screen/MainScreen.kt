package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.AuthViewModel

/**
 * Main navigation screen UI
 * Displays user information and navigation buttons to different features
 * Requirements: 5.1-5.6
 */
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToCompetition: () -> Unit = {},
    onNavigateToFruitNutrition: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val username = viewModel.getCurrentUsername() ?: "User"
    val isAdmin = viewModel.isCurrentUserAdmin()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display current logged-in user's username (Requirement 5.6)
        Text(
            text = "Welcome, $username!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isAdmin) {
            Text(
                text = "Administrator",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        
        // Button to navigate to Event_Screen (Requirement 5.2, 5.4)
        Button(
            onClick = onNavigateToCompetition,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("赛事评价")
        }
        
        // Button to navigate to Fruit_Nutrition_Screen (Requirement 5.3, 5.5)
        Button(
            onClick = onNavigateToFruitNutrition,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text("水果营养查询")
        }
        
        OutlinedButton(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}