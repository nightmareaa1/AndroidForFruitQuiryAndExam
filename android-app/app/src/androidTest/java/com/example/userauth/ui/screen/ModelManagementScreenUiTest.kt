package com.example.userauth.ui.screen

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.testTag
import org.junit.Rule
import org.junit.Test

import androidx.test.ext.junit.runners.AndroidJUnit4

@org.junit.runner.RunWith(AndroidJUnit4::class)
@ExperimentalMaterial3Api
class ModelManagementScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsInitialModelInList() {
        composeTestRule.setContent {
            ModelManagementScreen(onBack = {})
        }
        composeTestRule.onNode(hasText("Mango Model", substring = true)).assertIsDisplayed()
    }
}
