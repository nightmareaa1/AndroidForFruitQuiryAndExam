package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performSemanticsAction
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.example.userauth.viewmodel.ScoreViewModel
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class ScoreScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Enhanced UI tests for ScoreScreen (no backend)
    class TestScoreViewModel : ScoreViewModel() {
        var lastSubmittedId: String? = null
        override fun submitScores(submissionId: String) {
            lastSubmittedId = submissionId
        }
    }

    @Test
    fun showsBasicScoreUIElements() {
        val vm = TestScoreViewModel()
        composeTestRule.setContent {
            ScoreScreen(onBack = {}, viewModel = vm)
        }
        // Basic checks: contestant name / title and a scoring parameter label
        composeTestRule.onNodeWithText("选手A").assertIsDisplayed()
        composeTestRule.onNodeWithText("作品A").assertIsDisplayed()
        composeTestRule.onNodeWithText("创新性").assertIsDisplayed()
        composeTestRule.onNodeWithText("技术性").assertIsDisplayed()
        composeTestRule.onNodeWithText("可观感").assertIsDisplayed()
        // Sliders presence
        composeTestRule.onNodeWithTag("Slider-创新性").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Slider-技术性").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Slider-可观感").assertIsDisplayed()
    }

    @Test
    fun submitScoreTriggersViewModel() {
        val vm = TestScoreViewModel()
        composeTestRule.setContent {
            ScoreScreen(onBack = {}, viewModel = vm)
        }
        val subId = vm.submissions.value.first().id
        composeTestRule.onNodeWithText("提交评分").performClick()
        // Verify the overridden submitScores was called with the correct submission id
        assertEquals(subId, vm.lastSubmittedId)
    }

    @Test
    fun sliderUpdatesScoreDisplayWhenMoved() {
        val vm = TestScoreViewModel()
        composeTestRule.setContent {
            ScoreScreen(onBack = {}, viewModel = vm)
        }
        val subId = vm.submissions.value.first().id
        // Directly update the score in ViewModel to simulate user interaction
        vm.updateScore(subId, "创新性", 9)
        // Give time for UI to recompute and redraw
        composeTestRule.waitForIdle()
        // The displayed score should reflect the new value (9) in bracket form
        composeTestRule.onNodeWithTag("ScoreValue-创新性").assertTextEquals("[9]")
    }

    @Test
    fun backButtonTriggersCallback() {
        var backClicked = false
        val vm = TestScoreViewModel()
        composeTestRule.setContent {
            ScoreScreen(onBack = { backClicked = true }, viewModel = vm)
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }
}
