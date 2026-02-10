package com.example.userauth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.data.model.SubmissionScore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataDisplayScreen(
    onBack: () -> Unit,
    onCardClick: (String) -> Unit,
    competitionName: String = "评分详情",
    viewModel: DataDisplayViewModel = hiltViewModel()
) {
    val submissions by viewModel.submissions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(competitionName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            items(submissions) { sub: SubmissionScore ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onCardClick(sub.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.LightGray, MaterialTheme.shapes.small)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sub.contestant, style = MaterialTheme.typography.titleMedium)
                            Text(text = sub.title, style = MaterialTheme.typography.bodyMedium)
                        }
                        val avg = sub.scores.map { it.score }.average()
                        Text(
                            text = "Avg ${"%.1f".format(avg)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
