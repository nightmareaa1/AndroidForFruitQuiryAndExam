package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userauth.viewmodel.ScoreViewModel
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    competitionId: Long,
    onBack: () -> Unit,
    viewModel: ScoreViewModel = hiltViewModel()
) {
    val submissions by viewModel.submissions.collectAsState()
    val competitionName by viewModel.competitionName.collectAsState()
    
    // Load competition data when screen opens
    LaunchedEffect(competitionId) {
        viewModel.loadCompetition(competitionId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(competitionName.ifEmpty { "评分界面（评委）" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(submissions) { sub: SubmissionScore ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = sub.contestant, style = MaterialTheme.typography.titleMedium)
                            Text(text = sub.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            sub.scores.forEach { p ->
                                ScoreParameterRow(submission = sub, param = p, onScoreChange = { name, value ->
                                    // Update score in ViewModel
                                    viewModel.updateScore(sub.id, name, value)
                                })
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { viewModel.submitScores(sub.id) }) {
                                    Text("提交评分")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreParameterRow(
    submission: SubmissionScore,
    param: ScoreParameter,
    onScoreChange: (String, Int) -> Unit
) {
    val value = param.score
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(text = param.name, modifier = Modifier.weight(1f))
            Text(text = "[${param.score}]", modifier = Modifier.padding(start = 8.dp))
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onScoreChange(param.name, it.toInt()) },
            valueRange = 0f..param.max.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
    }
}
