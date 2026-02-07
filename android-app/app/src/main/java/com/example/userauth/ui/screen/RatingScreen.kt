package com.example.userauth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.viewmodel.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    competitionId: Long,
    onBack: () -> Unit,
    viewModel: RatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val entryRatings by viewModel.entryRatings.collectAsState()
    
    LaunchedEffect(competitionId) {
        viewModel.loadCompetitionForRating(competitionId)
    }
    
    // Show error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            snackbarHostState.showSnackbar("评分提交成功！")
            viewModel.clearSubmissionSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (uiState.competitionName.isNotEmpty()) 
                            "评分: ${uiState.competitionName}" 
                        else 
                            "作品评分"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // Progress indicator
                if (uiState.entries.isNotEmpty()) {
                    val ratedCount = entryRatings.count { it.value.isSubmitted }
                    Text(
                        text = "评分进度: $ratedCount/${uiState.entries.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    LinearProgressIndicator(
                        progress = ratedCount.toFloat() / uiState.entries.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                
                // Entry list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.entries) { index, entry ->
                        val entryRating = entryRatings[entry.id]
                        val isRated = entryRating?.isSubmitted ?: false
                        
                        EntryRatingCard(
                            entryNumber = index + 1,
                            entryName = entry.entryName,
                            contestantName = entry.contestantName,
                            description = entry.description,
                            isRated = isRated,
                            onRateClick = {
                                viewModel.setCurrentEntry(index)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Show rating dialog if an entry is selected
    val currentEntryIndex = uiState.currentEntryIndex
    if (currentEntryIndex >= 0 && currentEntryIndex < uiState.entries.size) {
        val currentEntry = uiState.entries[currentEntryIndex]
        val entryRating = entryRatings[currentEntry.id]
        
        RatingDialog(
            entryName = currentEntry.entryName,
            parameters = uiState.evaluationModel?.parameters ?: emptyList(),
            currentScores = entryRating?.scores ?: emptyMap(),
            currentNote = entryRating?.note ?: "",
            onDismiss = { viewModel.setCurrentEntry(-1) },
            onScoreChange = { parameterId, score ->
                viewModel.updateScore(currentEntry.id, parameterId, score)
            },
            onNoteChange = { note ->
                viewModel.updateNote(currentEntry.id, note)
            },
            onSubmit = {
                viewModel.submitRating(currentEntry.id)
                viewModel.setCurrentEntry(-1)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryRatingCard(
    entryNumber: Int,
    entryName: String,
    contestantName: String,
    description: String?,
    isRated: Boolean,
    onRateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "#$entryNumber: $entryName",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "参赛者: $contestantName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (!description.isNullOrBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                if (isRated) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "已评分",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onRateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isRated) Icons.Default.CheckCircle else Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isRated) "修改评分" else "开始评分")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RatingDialog(
    entryName: String,
    parameters: List<com.example.userauth.data.api.dto.EvaluationParameterDto>,
    currentScores: Map<Long, Double>,
    currentNote: String,
    onDismiss: () -> Unit,
    onScoreChange: (Long, Double) -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var note by remember { mutableStateOf(currentNote) }
    val totalScore = currentScores.values.sum()
    val maxPossibleScore = parameters.sumOf { it.weight }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("评分: $entryName") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Total score display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "当前总分",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${totalScore.toInt()}/$maxPossibleScore",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Parameter sliders
                LazyColumn(
                    modifier = Modifier.heightIn(max = 250.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(parameters) { _, param ->
                        val currentValue = currentScores[param.id] ?: 0.0
                        
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = param.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${currentValue.toInt()}/${param.weight}分",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Slider(
                                value = currentValue.toFloat(),
                                onValueChange = { onScoreChange(param.id, it.toDouble()) },
                                valueRange = 0f..param.weight.toFloat(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Note input
                OutlinedTextField(
                    value = note,
                    onValueChange = { 
                        note = it
                        onNoteChange(it)
                    },
                    label = { Text("评语 (可选)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = currentScores.size == parameters.size
            ) {
                Text("提交评分")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
