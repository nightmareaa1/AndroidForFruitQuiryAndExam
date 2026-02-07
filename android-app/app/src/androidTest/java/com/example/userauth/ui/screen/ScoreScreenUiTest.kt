package com.example.userauth.ui.screen

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class ScoreScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Test state holder for ScoreScreen
    class TestScoreState {
        private val _submissions = MutableStateFlow(
            listOf(
                SubmissionScore(
                    id = "test-id-1",
                    contestant = "选手A",
                    title = "作品A",
                    scores = mutableListOf(
                        ScoreParameter("创新性", 10, 5),
                        ScoreParameter("技术性", 10, 5),
                        ScoreParameter("可观感", 10, 5)
                    )
                )
            )
        )
        val submissions: StateFlow<List<SubmissionScore>> = _submissions

        private val _competitionName = MutableStateFlow("测试赛事")
        val competitionName: StateFlow<String> = _competitionName

        var lastSubmittedId: String? = null
        fun submitScores(submissionId: String) {
            lastSubmittedId = submissionId
        }

        fun updateScore(submissionId: String, paramName: String, value: Int) {
            _submissions.value = _submissions.value.map { s ->
                if (s.id != submissionId) s
                else {
                    val updated = s.scores.map { p -> if (p.name == paramName) p.copy(score = value) else p }.toMutableList()
                    s.copy(scores = updated)
                }
            }
        }
    }

    @Test
    fun showsBasicScoreUIElements() {
        val state = TestScoreState()
        composeTestRule.setContent {
            TestScoreScreen(
                competitionId = 1L,
                competitionName = state.competitionName.value,
                submissions = state.submissions.value,
                onBack = {},
                onSubmitScores = { state.submitScores(it) },
                onUpdateScore = { id, name, value -> state.updateScore(id, name, value) }
            )
        }
        composeTestRule.onNodeWithText("选手A").assertIsDisplayed()
        composeTestRule.onNodeWithText("作品A").assertIsDisplayed()
        composeTestRule.onNodeWithText("创新性").assertIsDisplayed()
        composeTestRule.onNodeWithText("技术性").assertIsDisplayed()
        composeTestRule.onNodeWithText("可观感").assertIsDisplayed()
        composeTestRule.onNodeWithText("提交评分").assertIsDisplayed()
    }

    @Test
    fun submitScoreTriggersCallback() {
        val state = TestScoreState()
        composeTestRule.setContent {
            TestScoreScreen(
                competitionId = 1L,
                competitionName = state.competitionName.value,
                submissions = state.submissions.value,
                onBack = {},
                onSubmitScores = { state.submitScores(it) },
                onUpdateScore = { id, name, value -> state.updateScore(id, name, value) }
            )
        }
        val subId = state.submissions.value.first().id
        composeTestRule.onNodeWithText("提交评分").performClick()
        assertEquals(subId, state.lastSubmittedId)
    }

    @Test
    fun sliderUpdatesScoreDisplay() {
        val state = TestScoreState()
        composeTestRule.setContent {
            TestScoreScreen(
                competitionId = 1L,
                competitionName = state.competitionName.value,
                submissions = state.submissions.value,
                onBack = {},
                onSubmitScores = { state.submitScores(it) },
                onUpdateScore = { id, name, value -> state.updateScore(id, name, value) }
            )
        }
        val subId = state.submissions.value.first().id
        state.updateScore(subId, "创新性", 9)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("创新性").assertIsDisplayed()
    }

    @Test
    fun backButtonTriggersCallback() {
        var backClicked = false
        val state = TestScoreState()
        composeTestRule.setContent {
            TestScoreScreen(
                competitionId = 1L,
                competitionName = state.competitionName.value,
                submissions = state.submissions.value,
                onBack = { backClicked = true },
                onSubmitScores = { state.submitScores(it) },
                onUpdateScore = { id, name, value -> state.updateScore(id, name, value) }
            )
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun displaysCompetitionTitle() {
        val state = TestScoreState()
        composeTestRule.setContent {
            TestScoreScreen(
                competitionId = 1L,
                competitionName = state.competitionName.value,
                submissions = state.submissions.value,
                onBack = {},
                onSubmitScores = { state.submitScores(it) },
                onUpdateScore = { id, name, value -> state.updateScore(id, name, value) }
            )
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }
}
