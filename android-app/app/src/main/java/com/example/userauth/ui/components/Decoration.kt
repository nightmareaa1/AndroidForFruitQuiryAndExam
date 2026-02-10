package com.example.userauth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.userauth.ui.theme.BrandGradients
import com.example.userauth.ui.theme.CornerRadius
import com.example.userauth.ui.theme.DecorationSize

// ============================================
// Card Decoration Types
// ============================================
sealed class CardDecoration {
    object None : CardDecoration()

    data class GradientTop(
        val brush: Brush,
        val height: Dp = DecorationSize.stripHeight
    ) : CardDecoration()

    data class AccentCorner(
        val color: Color,
        val size: Dp = DecorationSize.cornerDot,
        val position: CornerPosition = CornerPosition.TopRight
    ) : CardDecoration()

    data class LeftAccentBar(
        val color: Color,
        val width: Dp = 4.dp
    ) : CardDecoration()

    enum class CornerPosition {
        TopLeft, TopRight, BottomLeft, BottomRight
    }
}

// ============================================
// Decorative Background Components
// ============================================

@Composable
fun DecorativeCircleBackground(
    size: Dp = DecorationSize.backgroundCircle,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    alpha: Float = 0.1f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .offset(x = offsetX, y = offsetY)
            .graphicsLayer { this.alpha = alpha }
            .background(
                brush = BrandGradients.DecorationRadial,
                shape = CircleShape
            )
    )
}

@Composable
fun DecorativeTiltedBlock(
    size: Dp = DecorationSize.tiltedBlock,
    offsetX: Dp = 100.dp,
    offsetY: Dp = (-20).dp,
    rotation: Float = 15f,
    alpha: Float = 0.1f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .offset(x = offsetX, y = offsetY)
            .graphicsLayer {
                this.rotationZ = rotation
                this.alpha = alpha
            }
            .background(
                color = Color.White,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
    )
}

// ============================================
// Card Decoration Renderers
// ============================================

@Composable
fun CardTopGradientStrip(
    brush: Brush,
    height: Dp = DecorationSize.stripHeight
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(brush)
    )
}

@Composable
fun CardCornerAccent(
    color: Color,
    size: Dp = DecorationSize.cornerDot,
    position: CardDecoration.CornerPosition = CardDecoration.CornerPosition.TopRight
) {
    val alignment = when (position) {
        CardDecoration.CornerPosition.TopLeft -> Alignment.TopStart
        CardDecoration.CornerPosition.TopRight -> Alignment.TopEnd
        CardDecoration.CornerPosition.BottomLeft -> Alignment.BottomStart
        CardDecoration.CornerPosition.BottomRight -> Alignment.BottomEnd
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = if (position.name.contains("Right")) size else -size,
                    y = -size / 2
                )
                .size(size)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun CardLeftAccentBar(
    color: Color,
    width: Dp = 4.dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .background(color)
    )
}
