package com.example.userauth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.ui.components.RadarChartView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataDisplayDetailScreen(
    submissionId: String,
    competitionId: Long,
    onBack: () -> Unit,
    onRateClick: (Long, String, Long) -> Unit,
    viewModel: DataDisplayViewModel
) {
    val submissions = viewModel.submissions.collectAsState()
    val sub = submissions.value.find { it.id == submissionId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub?.title ?: "作品详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (sub == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("加载中...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        if (!sub.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = sub.imageUrl,
                                contentDescription = "作品图片",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RateReview,
                                        contentDescription = "作品图片",
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "暂无图片",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    val dims = sub.scores.map { it.name }
                    val values = sub.scores.map { it.score.toFloat() }
                    val colors = listOf(
                        0xFFE57373.toInt(),
                        0xFF64B5F6.toInt(),
                        0xFF81C784.toInt(),
                        0xFFFFC107.toInt(),
                        0xFFBA68C8.toInt(),
                        0xFF4DD0E1.toInt()
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        RadarChartView(
                            dimensions = dims,
                            values = values,
                            colors = colors.take(dims.size),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "作品信息",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(label = "作品名称", value = sub.title)

                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow(label = "参赛选手", value = sub.contestant)

                        Spacer(modifier = Modifier.height(8.dp))

                        val avgScore = if (sub.scores.isNotEmpty()) {
                            sub.scores.map { it.score }.average()
                        } else 0.0

                        InfoRow(
                            label = "平均得分",
                            value = "%.1f".format(avgScore),
                            valueColor = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "评分详情:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        sub.scores.forEach { scoreParam ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = scoreParam.name,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "${scoreParam.score}/${scoreParam.max}分",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "评委操作",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val modelId = viewModel.modelId.collectAsState().value

                        Button(
                            onClick = {
                                val entryId = sub.id.toLongOrNull() ?: 0L
                                onRateClick(entryId, sub.title, modelId ?: 0L)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = "评分",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "开始评分",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "点击按钮为该作品进行评分",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}
