package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.example.userauth.viewmodel.FruitNutritionViewModel
import com.example.userauth.ui.screen.FruitNutritionScreen
import com.example.userauth.data.model.FruitNutrition

// Test suite for FruitNutritionScreen (路线A: 数据本地化初始实现)
@RunWith(AndroidJUnit4::class)
class FruitNutritionScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsUIElementsAndInitialData() {
        val vm = FruitNutritionViewModel()
        composeTestRule.setContent {
            FruitNutritionScreen(onBack = {}, onFruitClick = { _ -> }, viewModel = vm)
        }
        composeTestRule.waitForIdle()

        // UI visibility checks
        composeTestRule.onNodeWithText("水果营养查询").assertIsDisplayed()
        composeTestRule.onNodeWithTag("type-dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("fruit-dropdown").assertIsDisplayed()
        // Default selections
        composeTestRule.onNodeWithText("营养成分").assertIsDisplayed()
        composeTestRule.onNodeWithText("芒果").assertIsDisplayed()
        composeTestRule.onNodeWithTag("query-button").assertIsDisplayed()
    }

    @Test
    fun queryShowsResults() {
        val vm = FruitNutritionViewModel()
        composeTestRule.setContent {
            FruitNutritionScreen(onBack = {}, onFruitClick = { _ -> }, viewModel = vm)
        }
        // Trigger a query via the ViewModel to simulate user action
        vm.query("营养成分", "芒果")
        composeTestRule.waitForIdle()
        // Results table should be shown with headers and data
        composeTestRule.onNodeWithTag("results-header").assertIsDisplayed()
        composeTestRule.onNodeWithText("热量").assertIsDisplayed()
        composeTestRule.onNodeWithText("60.0").assertIsDisplayed()
    }
}
