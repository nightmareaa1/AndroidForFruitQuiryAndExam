package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.ui.components.RadarChartView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataDisplayDetailScreen(submissionId: String, onBack: () -> Unit, viewModel: DataDisplayViewModel) {
    val submissions = viewModel.submissions.collectAsState()
    val sub = submissions.value.find { it.id == submissionId } ?: return
    val dims = sub.scores.map { it.name }
    val values = sub.scores.map { it.score.toFloat() }
    val colors = listOf(0xFFE57373.toInt(), 0xFF64B5F6.toInt(), 0xFF81C784.toInt(), 0xFFFFC107.toInt(), 0xFFBA68C8.toInt(), 0xFF4DD0E1.toInt())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            RadarChartView(
                dimensions = dims, 
                values = values, 
                colors = colors.take(dims.size), 
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            )
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(
                    text = "Submission: ${sub.title}\nContestant: ${sub.contestant}", 
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
