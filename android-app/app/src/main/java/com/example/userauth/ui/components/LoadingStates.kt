package com.example.userauth.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.userauth.ui.theme.CornerRadius
import com.example.userauth.ui.theme.IconSize
import com.example.userauth.ui.theme.OnSurface
import com.example.userauth.ui.theme.OnSurfaceVariant
import com.example.userauth.ui.theme.Padding
import com.example.userauth.ui.theme.Primary
import com.example.userauth.ui.theme.Secondary
import com.example.userauth.ui.theme.Spacing
import com.example.userauth.ui.theme.SurfaceVariant

// ============================================
// Animated Loading Indicator
// ============================================
@Composable
fun AnimatedLoadingIndicator(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = Primary,
            strokeWidth = 4.dp
        )
        Box(
            modifier = Modifier
                .size(size * 0.4f * scale)
                .background(Secondary, shape = CircleShape)
        )
    }
}

// ============================================
// Shimmer Types
// ============================================
enum class ShimmerType {
    Card, ListItem, Button
}

// ============================================
// Shimmer Card with Diagonal Effect
// ============================================
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    type: ShimmerType = ShimmerType.Card
) {
    val shimmerColors = listOf(
        SurfaceVariant.copy(alpha = 0.3f),
        SurfaceVariant.copy(alpha = 0.5f),
        SurfaceVariant.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (type) {
                    ShimmerType.Card -> 1500
                    ShimmerType.ListItem -> 1000
                    ShimmerType.Button -> 800
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200, 0f),
        end = Offset(translateAnim, 400f)
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CornerRadius.large)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}

// ============================================
// Shimmer List Item
// ============================================
@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.screen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerCard(
            modifier = Modifier.size(56.dp),
            type = ShimmerType.ListItem
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ShimmerCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                type = ShimmerType.ListItem
            )
            ShimmerCard(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp),
                type = ShimmerType.ListItem
            )
        }
    }
}

// ============================================
// Empty State Component
// ============================================
@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

// ============================================
// Convenience Empty State for Competitions
// ============================================
@Composable
fun CompetitionEmptyState(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(IconSize.xlarge),
                tint = OnSurfaceVariant
            )
        },
        title = "暂无赛事",
        description = "当前没有进行中的评价赛事，请稍后再试或联系管理员",
        modifier = modifier,
        action = {
            AppButton(
                text = "刷新",
                onClick = onRefresh,
                buttonType = AppButtonType.Outlined
            )
        }
    )
}
