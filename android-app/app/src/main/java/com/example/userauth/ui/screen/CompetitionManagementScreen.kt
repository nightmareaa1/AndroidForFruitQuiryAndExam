package com.example.userauth.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.data.model.Competition
import com.example.userauth.viewmodel.CompetitionManagementViewModel
import com.example.userauth.viewmodel.ModelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionManagementScreen(
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToReview: (Long, String) -> Unit,
    onNavigateToEditEntries: (Long, String) -> Unit,
    viewModel: CompetitionManagementViewModel = hiltViewModel(),
    modelViewModel: ModelViewModel = hiltViewModel()
) {
    val competitions by viewModel.competitions.collectAsState()
    val models by modelViewModel.models.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var selectedCompetition by remember { mutableStateOf<Competition?>(null) }

    // Force refresh when entering the screen
    LaunchedEffect(Unit) {
        viewModel.loadCompetitions()
        modelViewModel.loadModels()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("赛事管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAdd) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "添加赛事")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (competitions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无赛事",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(competitions) { competition ->
                    val modelName = models.find { it.id == competition.modelId.toString() }?.name ?: "未知模型"
                    CompetitionCard(
                        competition = competition,
                        modelName = modelName,
                        onNavigateToEdit = { onNavigateToEdit(competition.id) },
                        onNavigateToReview = { onNavigateToReview(competition.id, competition.name) },
                        onNavigateToEditEntries = { onNavigateToEditEntries(competition.id, competition.name) },
                        onDelete = {
                            selectedCompetition = competition
                            showDeleteConfirm = true
                        }
                    )
                }
                }
            }
        }
    }

    if (showDeleteConfirm && selectedCompetition != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
                selectedCompetition = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除赛事 ${selectedCompetition?.name} 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedCompetition?.let { viewModel.deleteCompetition(it.id) }
                        showDeleteConfirm = false
                        selectedCompetition = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    selectedCompetition = null
                }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompetitionCard(
    competition: Competition,
    modelName: String,
    onNavigateToEdit: () -> Unit,
    onNavigateToReview: () -> Unit,
    onNavigateToEditEntries: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = if (expanded) 4.dp else 2.dp)
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
                        text = competition.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "截止日期: ${competition.deadline}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "评价模型: $modelName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusChip(status = competition.status)
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconActionButton(
                            icon = Icons.Default.Edit,
                            label = "编辑赛事",
                            onClick = onNavigateToEdit
                        )
                        IconActionButton(
                            icon = Icons.Default.RateReview,
                            label = "审核作品",
                            onClick = onNavigateToReview
                        )
                        IconActionButton(
                            icon = Icons.Default.Collections,
                            label = "编辑作品",
                            onClick = onNavigateToEditEntries
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, text) = when (status.uppercase()) {
        "ACTIVE" -> MaterialTheme.colorScheme.primary to "进行中"
        "ENDED" -> MaterialTheme.colorScheme.error to "已结束"
        "PENDING" -> MaterialTheme.colorScheme.secondary to "待开始"
        else -> MaterialTheme.colorScheme.outline to status
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
