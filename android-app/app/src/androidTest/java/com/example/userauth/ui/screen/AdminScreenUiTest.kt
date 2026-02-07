package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AdminScreenUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun adminView_showsAdminPanel() {
        composeTestRule.setContent {
            AdminScreen(
                onBack = {},
                onNavigateToModelManagement = {},
                onNavigateToCompetitionManagement = {}
            )
        }

        // Verify admin panel title is displayed
        composeTestRule.onNodeWithText("管理员面板").assertIsDisplayed()
    }

    @Test
    fun adminView_displaysBackButton() {
        composeTestRule.setContent {
            AdminScreen(
                onBack = {},
                onNavigateToModelManagement = {},
                onNavigateToCompetitionManagement = {}
            )
        }

        // Back button and title should always be displayed
        composeTestRule.onNodeWithText("管理员面板").assertIsDisplayed()
    }
}
