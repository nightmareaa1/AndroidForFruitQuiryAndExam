package com.example.userauth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.data.model.SubmissionScore

@Composable
fun DataDisplayScreen(onBack: () -> Unit, onCardClick: (String) -> Unit, viewModel: DataDisplayViewModel = hiltViewModel()) {
    val submissions by viewModel.submissions.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(submissions) { sub: SubmissionScore ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onCardClick(sub.id) },
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.LightGray)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = sub.contestant, style = MaterialTheme.typography.titleMedium)
                        Text(text = sub.title, style = MaterialTheme.typography.bodyMedium)
                    }
                    val avg = sub.scores.map { it.score }.average()
                    Text(text = "Avg ${"%.1f".format(avg)}", modifier = Modifier.padding(end = 8.dp))
                }
            }
        }
    }
}
