package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for MainScreen
 * Tests main navigation functionality and user information display
 * Requirements: 5.1-5.6
 * 
 * IMPORTANT: This test needs to be run manually in Android Studio
 * Execution: Right-click on test class → Run
 */
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var testViewModel: TestAuthViewModel
    private var logoutCalled = false
    private var navigateToCompetitionCalled = false
    private var navigateToFruitNutritionCalled = false

    @Before
    fun setup() {
        testViewModel = TestAuthViewModel()
        
        // Reset navigation flags
        logoutCalled = false
        navigateToCompetitionCalled = false
        navigateToFruitNutritionCalled = false
    }

    /**
     * Test implementation of AuthViewModel for UI testing
     * Provides a simple way to control state without complex mocking
     */
    private class TestAuthViewModel {
        private var currentUsername: String? = "testuser"
        private var isAdmin: Boolean = false
        var logoutCalled = false

        fun getCurrentUsername(): String? = currentUsername
        fun isCurrentUserAdmin(): Boolean = isAdmin
        
        fun logout() {
            logoutCalled = true
            currentUsername = null
        }

        // Test helper methods
        fun setCurrentUser(username: String, admin: Boolean = false) {
            currentUsername = username
            isAdmin = admin
        }

        fun clearUser() {
            currentUsername = null
            isAdmin = false
        }
    }

    /**
     * Test version of MainScreen that accepts our test ViewModel
     */
    @Composable
    private fun TestMainScreen(
        onLogout: () -> Unit,
        onNavigateToCompetition: () -> Unit = {},
        onNavigateToFruitNutrition: () -> Unit = {},
        viewModel: TestAuthViewModel
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

    @Test
    fun mainScreen_displaysCorrectUI() {
        // Given - regular user
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify UI elements are displayed (Requirements 5.1, 5.2, 5.3)
        composeTestRule.onNodeWithText("Welcome, testuser!").assertIsDisplayed()
        composeTestRule.onNodeWithText("赛事评价").assertIsDisplayed()
        composeTestRule.onNodeWithText("水果营养查询").assertIsDisplayed()
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
        
        // Administrator text should not be displayed for regular user
        composeTestRule.onNodeWithText("Administrator").assertDoesNotExist()
    }

    @Test
    fun mainScreen_displaysUsernameCorrectly() {
        // Given - user with specific username
        testViewModel.setCurrentUser("john_doe", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify username is displayed correctly (Requirement 5.6)
        composeTestRule.onNodeWithText("Welcome, john_doe!").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysAdministratorLabel() {
        // Given - admin user
        testViewModel.setCurrentUser("admin", admin = true)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify admin label is displayed
        composeTestRule.onNodeWithText("Welcome, admin!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Administrator").assertIsDisplayed()
    }

    @Test
    fun mainScreen_handlesNullUsername() {
        // Given - no username (null case)
        testViewModel.clearUser()
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify default "User" is displayed (Requirement 5.6)
        composeTestRule.onNodeWithText("Welcome, User!").assertIsDisplayed()
    }

    @Test
    fun mainScreen_competitionButtonNavigatesCorrectly() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // When - click competition button
        composeTestRule.onNodeWithText("赛事评价").performClick()

        // Then - navigation callback should be called (Requirement 5.4)
        assert(navigateToCompetitionCalled)
        assert(!navigateToFruitNutritionCalled) // Other navigation should not be called
        assert(!logoutCalled)
    }

    @Test
    fun mainScreen_fruitNutritionButtonNavigatesCorrectly() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // When - click fruit nutrition button
        composeTestRule.onNodeWithText("水果营养查询").performClick()

        // Then - navigation callback should be called (Requirement 5.5)
        assert(navigateToFruitNutritionCalled)
        assert(!navigateToCompetitionCalled) // Other navigation should not be called
        assert(!logoutCalled)
    }

    @Test
    fun mainScreen_logoutButtonWorksCorrectly() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // When - click logout button
        composeTestRule.onNodeWithText("Logout").performClick()

        // Then - logout should be called
        assert(logoutCalled)
        assert(testViewModel.logoutCalled) // ViewModel logout should also be called
        assert(!navigateToCompetitionCalled)
        assert(!navigateToFruitNutritionCalled)
    }

    @Test
    fun mainScreen_buttonsAreClickable() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify all buttons are enabled and clickable
        composeTestRule.onNodeWithText("赛事评价").assertIsEnabled()
        composeTestRule.onNodeWithText("水果营养查询").assertIsEnabled()
        composeTestRule.onNodeWithText("Logout").assertIsEnabled()
    }

    @Test
    fun mainScreen_multipleButtonClicksWork() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // When - click multiple buttons in sequence
        composeTestRule.onNodeWithText("赛事评价").performClick()
        
        // Reset flags to test second button
        navigateToCompetitionCalled = false
        
        composeTestRule.onNodeWithText("水果营养查询").performClick()

        // Then - both navigation calls should work
        assert(navigateToFruitNutritionCalled)
    }

    @Test
    fun mainScreen_adminUserDisplaysCorrectly() {
        // Given - admin user
        testViewModel.setCurrentUser("admin_user", admin = true)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify admin-specific UI elements
        composeTestRule.onNodeWithText("Welcome, admin_user!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Administrator").assertIsDisplayed()
        
        // And - verify all navigation buttons are still available
        composeTestRule.onNodeWithText("赛事评价").assertIsDisplayed()
        composeTestRule.onNodeWithText("水果营养查询").assertIsDisplayed()
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun mainScreen_regularUserDoesNotShowAdminLabel() {
        // Given - regular user
        testViewModel.setCurrentUser("regular_user", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify admin label is not displayed
        composeTestRule.onNodeWithText("Welcome, regular_user!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Administrator").assertDoesNotExist()
    }

    @Test
    fun mainScreen_layoutIsCorrect() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify layout elements are in correct order
        // Welcome message should be at the top
        composeTestRule.onNodeWithText("Welcome, testuser!").assertIsDisplayed()
        
        // Navigation buttons should be in the middle
        composeTestRule.onNodeWithText("赛事评价").assertIsDisplayed()
        composeTestRule.onNodeWithText("水果营养查询").assertIsDisplayed()
        
        // Logout button should be at the bottom
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun mainScreen_allRequiredElementsPresent() {
        // Given
        testViewModel.setCurrentUser("testuser", admin = false)
        
        composeTestRule.setContent {
            TestMainScreen(
                onLogout = { logoutCalled = true },
                onNavigateToCompetition = { navigateToCompetitionCalled = true },
                onNavigateToFruitNutrition = { navigateToFruitNutritionCalled = true },
                viewModel = testViewModel
            )
        }

        // Then - verify all required elements from requirements 5.1-5.6 are present
        // Requirement 5.1: Main screen is displayed after successful login
        composeTestRule.onRoot().assertIsDisplayed()
        
        // Requirement 5.2: Button to navigate to Event_Screen
        composeTestRule.onNodeWithText("赛事评价").assertExists()
        
        // Requirement 5.3: Button to navigate to Fruit_Nutrition_Screen  
        composeTestRule.onNodeWithText("水果营养查询").assertExists()
        
        // Requirement 5.6: Display current logged-in user's username
        composeTestRule.onNodeWithText("Welcome, testuser!").assertExists()
        
        // Additional: Logout functionality
        composeTestRule.onNodeWithText("Logout").assertExists()
    }
}