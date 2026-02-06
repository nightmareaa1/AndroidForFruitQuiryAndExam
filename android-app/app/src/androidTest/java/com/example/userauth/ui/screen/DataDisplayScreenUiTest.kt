package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.userauth.ui.navigation.Screen
import org.junit.runner.RunWith
import com.example.userauth.viewmodel.DataDisplayViewModel
import org.junit.Assert.assertEquals

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

    @Test
    fun avgIsDisplayed() {
        val vm = DataDisplayViewModel()
        composeTestRule.setContent {
            DataDisplayScreen(onBack = {}, onCardClick = { /* no-op */ }, viewModel = vm)
        }
        composeTestRule.waitForIdle()
        val submissions = vm.submissions.value
        if (submissions.isNotEmpty()) {
            val first = submissions.first()
            val avg = first.scores.map { it.score }.average()
            val expected = "Avg ${"%.1f".format(avg)}"
            composeTestRule.onNodeWithText(expected).assertIsDisplayed()
        }
    }

    @Test
    fun onCardClickInvokesCallback() {
        val vm = DataDisplayViewModel()
        var clickedId: String? = null
        composeTestRule.setContent {
            DataDisplayScreen(onBack = {}, onCardClick = { id -> clickedId = id }, viewModel = vm)
        }
        val first = vm.submissions.value.firstOrNull()
        if (first != null) {
            composeTestRule.onNodeWithText(first.contestant).performClick()
            assertEquals(first.id, clickedId)
        }
    }
}
