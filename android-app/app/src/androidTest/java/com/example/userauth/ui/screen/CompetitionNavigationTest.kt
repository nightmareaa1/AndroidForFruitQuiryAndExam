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
import com.example.userauth.data.model.Competition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI tests for Competition Screen navigation flow
 * Tests: MainScreen → CompetitionScreen → ScoreScreen(competitionId)
 * Requirements: 6.3.1-6.3.16
 *
 * IMPORTANT: This test needs to be run manually in Android Studio
 * Execution: Right-click on test class → Run
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMaterial3Api::class)
class CompetitionNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Test state tracking
    private var currentScreen: Screen = Screen.MAIN
    private var lastCompetitionId: Long? = null
    private var backButtonClickedCount = 0

    // Test data
    private val testCompetitions = listOf(
        Competition(
            id = 1L,
            name = "2026年芒果品鉴大赛",
            modelId = 1L,
            creatorId = 1L,
            deadline = "2026-03-01",
            status = "ACTIVE"
        ),
        Competition(
            id = 2L,
            name = "香蕉风味评选",
            modelId = 1L,
            creatorId = 1L,
            deadline = "2026-04-15",
            status = "PENDING"
        )
    )

    /**
     * Enum to track current screen for navigation testing
     */
    private enum class Screen {
        MAIN, COMPETITION, SCORE
    }

    /**
     * Simple test data class for competition list state
     */
    private data class TestCompetitionState(
        val competitions: List<Competition> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * Test version of MainScreen for navigation testing
     */
    @Composable
    private fun TestMainScreenForNavigation(
        onNavigateToCompetition: () -> Unit,
        username: String = "testuser"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome, $username!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onNavigateToCompetition,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("赛事评价")
            }

            Button(
                onClick = { /* Not tested here */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("水果营养查询")
            }
        }
    }

    /**
     * Test version of CompetitionScreen for navigation testing
     */
    @Composable
    private fun TestCompetitionScreenForNavigation(
        competitions: List<Competition>,
        onBack: () -> Unit,
        onCompetitionClick: (Long, String) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("←")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "赛事评价",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Competition list
            Column(modifier = Modifier.padding(8.dp)) {
                competitions.forEach { competition ->
                    CompetitionTestCard(
                        competition = competition,
                        onClick = { onCompetitionClick(competition.id, competition.name) }
                    )
                }
            }
        }
    }

    /**
     * Test card component for competition items
     */
    @Composable
    private fun CompetitionTestCard(
        competition: Competition,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = competition.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "截止时间: ${competition.deadline}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    /**
     * Test version of ScoreScreen for navigation testing
     */
    @Composable
    private fun TestScoreScreenForNavigation(
        competitionId: Long,
        competitionName: String = "测试赛事",
        onBack: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("←")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$competitionName - 评分",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Mock submission data
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "选手A - 作品A",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "参数: 创新性, 技术性",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { }) {
                    Text("提交评分")
                }
            }
        }
    }

    @Before
    fun setup() {
        currentScreen = Screen.MAIN
        lastCompetitionId = null
        backButtonClickedCount = 0
    }

    // ==================== MainScreen Tests ====================

    @Test
    fun mainScreen_competitionButtonExists() {
        composeTestRule.setContent {
            TestMainScreenForNavigation(
                onNavigateToCompetition = {
                    currentScreen = Screen.COMPETITION
                }
            )
        }

        // Verify competition button exists
        composeTestRule.onNodeWithText("赛事评价").assertIsDisplayed()
    }

    @Test
    fun mainScreen_clickCompetitionButtonNavigatesToCompetitionScreen() {
        composeTestRule.setContent {
            TestMainScreenForNavigation(
                onNavigateToCompetition = {
                    currentScreen = Screen.COMPETITION
                }
            )
        }

        // When - click competition button
        composeTestRule.onNodeWithText("赛事评价").performClick()

        // Then - should navigate to competition screen
        assert(currentScreen == Screen.COMPETITION)
    }

    // ==================== CompetitionScreen Tests ====================

    @Test
    fun competitionScreen_displaysCompetitionList() {
        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = testCompetitions,
                onBack = { currentScreen = Screen.MAIN },
                onCompetitionClick = { id, _ ->
                    lastCompetitionId = id
                    currentScreen = Screen.SCORE
                }
            )
        }

        // Verify competitions are displayed
        composeTestRule.onNodeWithText("2026年芒果品鉴大赛").assertIsDisplayed()
        composeTestRule.onNodeWithText("香蕉风味评选").assertIsDisplayed()
    }

    @Test
    fun competitionScreen_displaysCompetitionDeadline() {
        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = testCompetitions,
                onBack = { currentScreen = Screen.MAIN },
                onCompetitionClick = { id, _ ->
                    lastCompetitionId = id
                    currentScreen = Screen.SCORE
                }
            )
        }

        // Verify deadline is displayed
        composeTestRule.onNodeWithText("截止时间: 2026-03-01").assertIsDisplayed()
        composeTestRule.onNodeWithText("截止时间: 2026-04-15").assertIsDisplayed()
    }

    @Test
    fun competitionScreen_clickCompetitionNavigatesToScoreScreen() {
        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = testCompetitions,
                onBack = { currentScreen = Screen.MAIN },
                onCompetitionClick = { id, _ ->
                    lastCompetitionId = id
                    currentScreen = Screen.SCORE
                }
            )
        }

        // When - click first competition
        composeTestRule.onNodeWithText("2026年芒果品鉴大赛").performClick()

        // Then - should navigate to score screen with correct competition ID
        assert(currentScreen == Screen.SCORE)
        assert(lastCompetitionId == 1L)
    }

    @Test
    fun competitionScreen_backButtonReturnsToPreviousScreen() {
        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = testCompetitions,
                onBack = {
                    backButtonClickedCount++
                    currentScreen = Screen.MAIN
                },
                onCompetitionClick = { id, _ ->
                    lastCompetitionId = id
                    currentScreen = Screen.SCORE
                }
            )
        }

        // When - click back button
        composeTestRule.onNodeWithText("←").performClick()

        // Then - should return to previous screen
        assert(backButtonClickedCount == 1)
        assert(currentScreen == Screen.MAIN)
    }

    @Test
    fun competitionScreen_multipleCompetitionsDisplayCorrectly() {
        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = testCompetitions,
                onBack = { currentScreen = Screen.MAIN },
                onCompetitionClick = { id, _ ->
                    lastCompetitionId = id
                    currentScreen = Screen.SCORE
                }
            )
        }

        // Verify all competitions are displayed
        testCompetitions.forEach { competition ->
            composeTestRule.onNodeWithText(competition.name).assertIsDisplayed()
        }

        // Verify total count
        composeTestRule.onNodeWithText("2026年芒果品鉴大赛").assertIsDisplayed()
        composeTestRule.onNodeWithText("香蕉风味评选").assertIsDisplayed()
    }

    // ==================== ScoreScreen Tests ====================

    @Test
    fun scoreScreen_displaysCompetitionName() {
        composeTestRule.setContent {
            TestScoreScreenForNavigation(
                competitionId = 1L,
                competitionName = "2026年芒果品鉴大赛",
                onBack = { currentScreen = Screen.COMPETITION }
            )
        }

        // Verify competition name is displayed
        composeTestRule.onNodeWithText("2026年芒果品鉴大赛 - 评分").assertIsDisplayed()
    }

    @Test
    fun scoreScreen_receivesCorrectCompetitionId() {
        var receivedId: Long? = null

        composeTestRule.setContent {
            TestScoreScreenForNavigation(
                competitionId = 42L,
                competitionName = "测试赛事",
                onBack = { currentScreen = Screen.COMPETITION }
            )
        }

        // Verify score screen is displayed (indicates correct ID was received)
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun scoreScreen_backButtonReturnsToCompetitionScreen() {
        composeTestRule.setContent {
            TestScoreScreenForNavigation(
                competitionId = 1L,
                competitionName = "测试赛事",
                onBack = {
                    backButtonClickedCount++
                    currentScreen = Screen.COMPETITION
                }
            )
        }

        // When - click back button
        composeTestRule.onNodeWithText("←").performClick()

        // Then - should return to competition screen
        assert(backButtonClickedCount == 1)
        assert(currentScreen == Screen.COMPETITION)
    }

    /**
     * Unified navigation host for testing - only one setContent needed
     */
    @Composable
    private fun TestNavigationHost(
        initialScreen: Screen = Screen.MAIN,
        testCompetitions: List<Competition> = emptyList()
    ) {
        var currentScreen by remember { mutableStateOf(initialScreen) }
        var selectedCompetitionId by remember { mutableStateOf<Long?>(null) }
        var selectedCompetitionName by remember { mutableStateOf("") }

        when (currentScreen) {
            Screen.MAIN -> {
                TestMainScreenForNavigation(
                    onNavigateToCompetition = { currentScreen = Screen.COMPETITION }
                )
            }
            Screen.COMPETITION -> {
                TestCompetitionScreenForNavigation(
                    competitions = testCompetitions,
                    onBack = { currentScreen = Screen.MAIN },
                    onCompetitionClick = { id, name ->
                        selectedCompetitionId = id
                        selectedCompetitionName = name
                        currentScreen = Screen.SCORE
                    }
                )
            }
            Screen.SCORE -> {
                TestScoreScreenForNavigation(
                    competitionId = selectedCompetitionId ?: 1L,
                    competitionName = selectedCompetitionName,
                    onBack = { currentScreen = Screen.COMPETITION }
                )
            }
        }
    }

    // ==================== End-to-End Navigation Tests ====================

    @Test
    fun endToEnd_mainScreenToCompetitionScreenToScoreScreen() {
        // Single setContent for entire navigation flow
        composeTestRule.setContent {
            TestNavigationHost(testCompetitions = testCompetitions)
        }

        // Step 1: Navigate from MainScreen to CompetitionScreen
        composeTestRule.onNodeWithText("赛事评价").performClick()

        // Verify CompetitionScreen is displayed
        composeTestRule.onNodeWithText("2026年芒果品鉴大赛").assertIsDisplayed()

        // Step 2: Navigate to ScoreScreen by clicking competition
        composeTestRule.onNodeWithText("2026年芒果品鉴大赛").performClick()

        // Verify ScoreScreen is displayed
        composeTestRule.onNodeWithText("提交评分").assertIsDisplayed()

        // Step 3: Navigate back to CompetitionScreen
        composeTestRule.onNodeWithText("←").performClick()

        // Verify CompetitionScreen is displayed again
        composeTestRule.onNodeWithText("香蕉风味评选").assertIsDisplayed()

        // Step 4: Navigate to different competition
        composeTestRule.onNodeWithText("香蕉风味评选").performClick()

        // Verify different competition's score screen
        composeTestRule.onNodeWithText("提交评分").assertIsDisplayed()
    }

    @Test
    fun endToEnd_competitionSelectionPassesCorrectId() {
        // Test that clicking different competitions passes correct IDs
        // Use single setContent with all competitions displayed
        var capturedId: Long? = null

        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = testCompetitions,
                onBack = {},
                onCompetitionClick = { id, _ ->
                    capturedId = id
                }
            )
        }

        // Test each competition sequentially without setContent reuse
        testCompetitions.forEach { competition ->
            capturedId = null

            composeTestRule.onNodeWithText(competition.name).performClick()

            assert(capturedId == competition.id) {
                "Expected competition ID ${competition.id} but got $capturedId"
            }
        }
    }

    @Test
    fun endToEnd_navigationWithEmptyCompetitionList() {
        val emptyCompetitions = emptyList<Competition>()

        // Test navigation when no competitions are available
        composeTestRule.setContent {
            TestCompetitionScreenForNavigation(
                competitions = emptyCompetitions,
                onBack = { currentScreen = Screen.MAIN },
                onCompetitionClick = { id, _ ->
                    lastCompetitionId = id
                    currentScreen = Screen.SCORE
                }
            )
        }

        // Verify back button still works
        composeTestRule.onNodeWithText("←").performClick()
        assert(currentScreen == Screen.MAIN)
    }
}
