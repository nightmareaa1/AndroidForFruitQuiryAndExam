package com.example.userauth.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.userauth.ui.screen.*
import com.example.userauth.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Admin : Screen("admin")
    object Competition : Screen("competition")
    object FruitNutrition : Screen("fruit_nutrition")
    object ModelManagement : Screen("model_management")
    object CompetitionManagement : Screen("competition_management")
    object CompetitionEdit : Screen("competition_edit/{competitionId}")
    object CompetitionAdd : Screen("competition_add")
    object Score : Screen("score/{competitionId}")
    object Rating : Screen("rating/{competitionId}/{entryId}/{entryName}/{modelId}")
    object RadarDetail : Screen("radar_detail/{submissionId}")
    object DataDisplayDetail : Screen("data-display-detail/{competitionId}/{submissionId}")
    object DataDisplay : Screen("data-display/{competitionId}")
    object EntryAdd : Screen("entry-add/{competitionId}/{competitionName}")
    object EntryReview : Screen("entry-review/{competitionId}/{competitionName}")

    companion object {
        fun competitionEdit(competitionId: Long): String =
            "competition_edit/$competitionId"
        
        fun rating(competitionId: Long, entryId: Long, entryName: String, modelId: Long): String =
            "rating/$competitionId/$entryId/${java.net.URLEncoder.encode(entryName, "UTF-8")}/$modelId"
        
        fun dataDisplay(competitionId: Long): String =
            "data-display/$competitionId"
        
        fun dataDisplayDetail(competitionId: Long, submissionId: String): String =
            "data-display-detail/$competitionId/$submissionId"
        
        fun entryAdd(competitionId: Long, competitionName: String): String =
            "entry-add/$competitionId/$competitionName"

        fun entryReview(competitionId: Long, competitionName: String): String =
            "entry-review/$competitionId/${java.net.URLEncoder.encode(competitionName, "UTF-8")}"
    }
}
