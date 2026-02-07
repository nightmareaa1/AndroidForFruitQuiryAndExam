package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter

import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Test version of ScoreScreen that accepts a simple contract
 * This allows testing without extending the real ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScoreScreen(
    competitionId: Long,
    competitionName: String,
    submissions: List<SubmissionScore>,
    onBack: () -> Unit,
    onSubmitScores: (String) -> Unit,
    onUpdateScore: (String, String, Int) -> Unit
) {
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
                                TestScoreParameterRow(
                                    param = p,
                                    onScoreChange = { name, value ->
                                        onUpdateScore(sub.id, name, value)
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { onSubmitScores(sub.id) }) {
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
private fun TestScoreParameterRow(
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
