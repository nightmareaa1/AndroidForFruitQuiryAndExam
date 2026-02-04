package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.userauth.ui.navigation.Screen
import org.junit.runner.RunWith
import com.example.userauth.viewmodel.DataDisplayViewModel

@RunWith(AndroidJUnit4::class)
class DataDisplayScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsBasicDataDisplayElements() {
        val vm = DataDisplayViewModel()
        composeTestRule.setContent {
            DataDisplayScreen(
                onBack = {}, 
                onCardClick = { id -> 
                    // Mock navigation for testing
                }, 
                viewModel = vm
            )
        }
        
        // Wait for the content to be composed
        composeTestRule.waitForIdle()
        
        // Check if submissions are displayed (if any exist)
        val submissions = vm.submissions.value
        if (submissions.isNotEmpty()) {
            val firstSubmission = submissions.first()
            // Check if the first submission's contestant name is displayed
            composeTestRule.onNode(hasText(firstSubmission.contestant)).assertIsDisplayed()
            // Check if the first submission's title is displayed
            composeTestRule.onNode(hasText(firstSubmission.title)).assertIsDisplayed()
        }
    }
}
