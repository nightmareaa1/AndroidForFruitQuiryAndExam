package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.example.userauth.viewmodel.CompetitionManagementViewModel
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.performClick

@RunWith(AndroidJUnit4::class)
class CompetitionManagementScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsInitialCompetitionInList() {
        // Render with a default ViewModel value
        composeTestRule.setContent {
            CompetitionManagementScreen(onBack = {})
        }
        composeTestRule.onNode(hasText("初始赛事", substring = true)).assertIsDisplayed()
    }

    @Test
    fun exportButtonInvokesCallback() {
        var exported = false
        composeTestRule.setContent {
            CompetitionManagementScreen(onBack = {}, onExport = { exported = true }, viewModel = com.example.userauth.viewmodel.CompetitionManagementViewModel())
        }
        composeTestRule.onNode(hasTestTag("CompetitionExportBtn")).performClick()
        assert(exported)
    }
}
