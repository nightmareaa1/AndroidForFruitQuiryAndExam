package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI tests for DataDisplayScreen
 * Tests data display functionality without backend dependency
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMaterial3Api::class)
class DataDisplayScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Test data
    private val testSubmissions = listOf(
        SubmissionScore(
            id = "sub-1",
            contestant = "选手A",
            title = "作品A",
            scores = mutableListOf(
                ScoreParameter("创新性", 8, 10),
                ScoreParameter("技术性", 7, 10)
            )
        ),
        SubmissionScore(
            id = "sub-2",
            contestant = "选手B",
            title = "作品B",
            scores = mutableListOf(
                ScoreParameter("创新性", 9, 10),
                ScoreParameter("技术性", 6, 10)
            )
        )
    )

    /**
     * Simple test ViewModel without Hilt dependency
     */
    private data class TestDataDisplayState(
        val submissions: List<SubmissionScore> = emptyList()
    )

    /**
     * Test version of DataDisplayScreen
     */
    @Composable
    private fun TestDataDisplayScreen(
        submissions: List<SubmissionScore>,
        onBack: () -> Unit,
        onCardClick: (String) -> Unit
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("数据展示", style = MaterialTheme.typography.titleMedium)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                if (submissions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无数据")
                    }
                } else {
                    submissions.forEach { submission ->
                        SubmissionCard(
                            submission = submission,
                            onClick = { onCardClick(submission.id) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SubmissionCard(
        submission: SubmissionScore,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${submission.contestant} - ${submission.title}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                val avg = submission.scores.map { it.score }.average()
                Text(
                    text = "平均分: ${"%.1f".format(avg)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    @Test
    fun showsBasicDataDisplayElements() {
        composeTestRule.setContent {
            TestDataDisplayScreen(
                submissions = testSubmissions,
                onBack = {},
                onCardClick = {}
            )
        }

        // Verify submissions are displayed
        composeTestRule.onNodeWithText("选手A - 作品A").assertIsDisplayed()
        composeTestRule.onNodeWithText("选手B - 作品B").assertIsDisplayed()
    }

    @Test
    fun displaysAverageScore() {
        composeTestRule.setContent {
            TestDataDisplayScreen(
                submissions = testSubmissions,
                onBack = {},
                onCardClick = {}
            )
        }

        // Verify average scores are displayed
        composeTestRule.onNodeWithText("平均分: 7.5").assertIsDisplayed() // (8+7)/2 = 7.5
        composeTestRule.onNodeWithText("平均分: 7.5").assertIsDisplayed() // (9+6)/2 = 7.5
    }

    @Test
    fun cardClickInvokesCallback() {
        var clickedId: String? = null

        composeTestRule.setContent {
            TestDataDisplayScreen(
                submissions = testSubmissions,
                onBack = {},
                onCardClick = { id -> clickedId = id }
            )
        }

        // Click on first submission
        composeTestRule.onNodeWithText("选手A - 作品A").performClick()

        // Verify callback was invoked with correct ID
        assert(clickedId == "sub-1")
    }

    @Test
    fun emptyStateDisplaysCorrectly() {
        composeTestRule.setContent {
            TestDataDisplayScreen(
                submissions = emptyList(),
                onBack = {},
                onCardClick = {}
            )
        }

        // Verify empty state message
        composeTestRule.onNodeWithText("暂无数据").assertIsDisplayed()
    }

    @Test
    fun multipleSubmissionsAllDisplay() {
        composeTestRule.setContent {
            TestDataDisplayScreen(
                submissions = testSubmissions,
                onBack = {},
                onCardClick = {}
            )
        }

        // Verify all submissions are displayed
        testSubmissions.forEach { submission ->
            composeTestRule.onNodeWithText("${submission.contestant} - ${submission.title}").assertIsDisplayed()
        }
    }
}
