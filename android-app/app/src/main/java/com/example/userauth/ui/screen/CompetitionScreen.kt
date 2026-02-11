package com.example.userauth.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.data.model.Competition
import com.example.userauth.ui.components.CompetitionEmptyState
import com.example.userauth.ui.components.ShimmerCard
import com.example.userauth.ui.components.StatusChip
import com.example.userauth.ui.components.StatusType
import com.example.userauth.ui.theme.*
import com.example.userauth.viewmodel.CompetitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionScreen(
    onBack: () -> Unit,
    onNavigateToDataDisplay: (Long) -> Unit = {},
    onNavigateToEntryAdd: (Long, String) -> Unit = { _, _ -> },
    onNavigateToMyEntries: (Long, String) -> Unit = { _, _ -> },
    viewModel: CompetitionViewModel = hiltViewModel()
) {
    val competitions by viewModel.competitions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "赛事评价",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(3) {
                            ShimmerCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )
                        }
                    }
                }

                errorMessage != null -> {
                    CompetitionEmptyState(
                        onRefresh = { viewModel.loadCompetitions() }
                    )
                }

                competitions.isEmpty() -> {
                    CompetitionEmptyState(
                        onRefresh = { viewModel.loadCompetitions() }
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(competitions) { competition ->
                    BrandCompetitionCard(
                        competition = competition,
                        onNavigateToDataDisplay = {
                            onNavigateToDataDisplay(competition.id)
                        },
                        onNavigateToEntryAdd = {
                            onNavigateToEntryAdd(competition.id, competition.name)
                        },
                        onNavigateToMyEntries = {
                            onNavigateToMyEntries(competition.id, competition.name)
                        }
                    )
                }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrandCompetitionCard(
    competition: Competition,
    onNavigateToDataDisplay: () -> Unit,
    onNavigateToEntryAdd: () -> Unit,
    onNavigateToMyEntries: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val accentColor = when (competition.status.uppercase()) {
        "ACTIVE" -> Success
        "PENDING" -> Warning
        "ENDED" -> OnSurfaceVariant
        else -> Primary
    }

    val statusType = when (competition.status.uppercase()) {
        "ACTIVE" -> StatusType.ACTIVE
        "PENDING" -> StatusType.PENDING
        "ENDED" -> StatusType.ENDED
        else -> StatusType.INFO
    }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.large),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (expanded) 4.dp else 2.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DecorationSize.stripHeight)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier.padding(Padding.card)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(CornerRadius.medium),
                        color = accentColor.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.md))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = competition.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "截止: ${competition.deadline}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    StatusChip(
                        text = when (competition.status.uppercase()) {
                            "ACTIVE" -> "进行中"
                            "PENDING" -> "待开始"
                            "ENDED" -> "已结束"
                            else -> competition.status
                        },
                        status = statusType
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Divider(color = Outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(Spacing.md))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconActionButton(
                                icon = Icons.Default.Assessment,
                                label = "数据",
                                onClick = onNavigateToDataDisplay
                            )
                            IconActionButton(
                                icon = Icons.Default.Collections,
                                label = "我的作品",
                                onClick = onNavigateToMyEntries
                            )
                            if (competition.status.uppercase() == "ACTIVE") {
                                IconActionButton(
                                    icon = Icons.Default.Add,
                                    label = "提交",
                                    onClick = onNavigateToEntryAdd
                                )
                            }
                        }
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
            shape = RoundedCornerShape(CornerRadius.medium),
            color = Primary.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(12.dp),
                tint = Primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}
