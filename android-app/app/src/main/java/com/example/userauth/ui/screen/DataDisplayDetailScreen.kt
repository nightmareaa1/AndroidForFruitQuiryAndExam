package com.example.userauth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.userauth.viewmodel.DataDisplayViewModel
import com.example.userauth.ui.components.RadarChartView
import com.example.userauth.ui.components.PieChart

// 图表颜色列表
private val chartColors = listOf(
    0xFFE57373.toInt(), // 红
    0xFF64B5F6.toInt(), // 蓝
    0xFF81C784.toInt(), // 绿
    0xFFFFC107.toInt(), // 黄
    0xFFBA68C8.toInt(), // 紫
    0xFF4DD0E1.toInt()  // 青
)

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
    var chartHeight by remember { mutableFloatStateOf(280f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub?.title ?: "作品详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val modelId = viewModel.modelId.collectAsState().value
                    val entryId = sub?.id?.toLongOrNull() ?: 0L
                    Button(
                        onClick = {
                            onRateClick(entryId, sub?.title ?: "", modelId ?: 0L)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("开始评分")
                    }
                    OutlinedButton(
                        onClick = { /* TODO: 查看全部评分 */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("评分详情")
                    }
                }
            }
        }
    ) { padding ->
        if (sub == null) {
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
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ========== 区域1: 作品大图 ==========
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                    contentDescription = null,
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

                // ========== 区域2: 雷达图+饼图并排 ==========
                val dims = sub.scores.map { it.name }
                val values = sub.scores.map { it.score.toFloat() }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 雷达图 (50%)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "评分雷达图",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (dims.isNotEmpty()) {
                                RadarChartView(
                                    dimensions = dims,
                                    values = values,
                                    colors = chartColors.take(dims.size),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("暂无评分数据", color = Color.Gray)
                                }
                            }
                        }
                    }

                    // 饼图 (50%)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "权重占比",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (sub.scores.isNotEmpty()) {
                                val pieData = sub.scores.map { it.name to it.max.toFloat() }
                                PieChart(
                                    data = pieData,
                                    colors = chartColors.take(pieData.size),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("暂无数据", color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // 图表高度调节滑块
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "图表大小:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = chartHeight,
                        onValueChange = { chartHeight = it },
                        valueRange = 150f..400f,
                        modifier = Modifier.weight(1f),
                        steps = 4
                    )
                    Text(
                        text = "${chartHeight.toInt()}dp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ========== 区域3: 统计卡片 ==========
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 使用API返回的数据
                    val avgScore = sub.averageTotalScore
                    val maxScore = sub.highestScore
                    val ratingCount = sub.numberOfRatings
                    val totalJudges = sub.totalJudges
                    val totalWeight = sub.scores.sumOf { it.max }

                    StatCard(
                        title = "平均分",
                        value = "%.1f".format(avgScore),
                        subtitle = "/ ${totalWeight}分",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "最高分",
                        value = "%.0f".format(maxScore),
                        subtitle = "分",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "评分人数",
                        value = "$ratingCount/$totalJudges",
                        subtitle = "人",
                        modifier = Modifier.weight(1f)
                    )
                }

                // ========== 区域4: 评分详情（进度条） ==========
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
                            text = "评分详情",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        sub.scores.forEachIndexed { index, scoreParam ->
                            ScoreProgressBar(
                                name = scoreParam.name,
                                score = scoreParam.score,
                                maxScore = scoreParam.max,
                                color = chartColors[index % chartColors.size]
                            )
                            if (index < sub.scores.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp)) // 为底部栏留空间
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ScoreProgressBar(
    name: String,
    score: Int,
    maxScore: Int,
    color: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$score/$maxScore 分",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(color)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = (score.toFloat() / maxScore).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = Color(color),
            trackColor = Color.LightGray
        )
    }
}
