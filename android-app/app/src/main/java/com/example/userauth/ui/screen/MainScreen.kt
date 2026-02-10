package com.example.userauth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.userauth.ui.components.GradientCard
import com.example.userauth.ui.components.StatusChip
import com.example.userauth.ui.components.StatusType
import com.example.userauth.ui.theme.*
import com.example.userauth.viewmodel.AuthViewModel

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToCompetition: () -> Unit = {},
    onNavigateToFruitNutrition: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val username = viewModel.getCurrentUsername() ?: "用户"
    val isAdmin = viewModel.isCurrentUserAdmin()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        GradientCard(
            gradientColors = GradientPrimary,
            modifier = Modifier.padding(Spacing.md),
            showTiltedDecoration = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(AvatarSize.medium),
                    shape = RoundedCornerShape(CornerRadius.circular),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(Spacing.sm),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "欢迎，$username！",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    if (isAdmin) {
                        StatusChip(
                            text = "管理员",
                            status = StatusType.INFO
                        )
                    }
                }

                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "退出登录",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "功能入口",
            style = MaterialTheme.typography.titleMedium,
            color = OnBackground,
            modifier = Modifier.padding(
                horizontal = Spacing.md,
                vertical = Spacing.sm
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                BrandFeatureCard(
                    title = "赛事评价",
                    description = "浏览和参与水果评价赛事",
                    icon = Icons.Default.EmojiEvents,
                    gradientColors = GradientPrimary,
                    onClick = onNavigateToCompetition
                )
            }

            item {
                BrandFeatureCard(
                    title = "水果营养查询",
                    description = "了解各类水果的营养成分",
                    icon = Icons.Default.Restaurant,
                    gradientColors = GradientSecondary,
                    onClick = onNavigateToFruitNutrition
                )
            }

            if (isAdmin) {
                item {
                    BrandFeatureCard(
                        title = "管理员面板",
                        description = "管理赛事、模型和参赛作品",
                        icon = Icons.Default.AdminPanelSettings,
                        gradientColors = listOf(
                            AccentPink,
                            AccentPink.copy(alpha = 0.7f)
                        ),
                        onClick = onNavigateToAdmin
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    GradientCard(
        gradientColors = gradientColors,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(IconSize.xlarge),
                shape = RoundedCornerShape(CornerRadius.medium),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(Spacing.sm),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
