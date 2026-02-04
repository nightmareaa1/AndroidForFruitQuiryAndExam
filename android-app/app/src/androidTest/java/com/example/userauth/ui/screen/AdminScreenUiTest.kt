package com.example.userauth.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.userauth.ui.screen.AdminScreen

@RunWith(AndroidJUnit4::class)
class AdminScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun adminView_showsOptionsWhenAdmin() {
        composeTestRule.setContent {
            AdminScreen(onBack = {}, isAdminProvider = { true })
        }
        composeTestRule.onNodeWithText("模型管理入口").assertIsDisplayed()
        composeTestRule.onNodeWithText("赛事管理入口").assertIsDisplayed()
    }

    @Test
    fun adminView_hidesOptionsWhenNotAdmin() {
        composeTestRule.setContent {
            AdminScreen(onBack = {}, isAdminProvider = { false })
        }
        composeTestRule.onNodeWithText("模型管理入口").assertDoesNotExist()
        composeTestRule.onNodeWithText("赛事管理入口").assertDoesNotExist()
    }
}
