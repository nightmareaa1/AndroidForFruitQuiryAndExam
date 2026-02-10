package com.example.userauth.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.dp
import com.example.userauth.ui.theme.CornerRadius
import com.example.userauth.ui.theme.DecorationSize
import com.example.userauth.ui.theme.Dimensions
import com.example.userauth.ui.theme.GradientPrimary
import com.example.userauth.ui.theme.Outline
import com.example.userauth.ui.theme.Padding
import com.example.userauth.ui.theme.Primary
import com.example.userauth.ui.theme.Surface

// ============================================
// Brand Card with Decoration Support
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: androidx.compose.ui.unit.Dp = Dimensions.cardElevation,
    shape: Shape = RoundedCornerShape(CornerRadius.large),
    backgroundColor: Color = Surface,
    decoration: CardDecoration = CardDecoration.None,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = Dimensions.cardElevationRaised
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            1.dp,
            Outline.copy(alpha = 0.3f)
        )
    ) {
        Column {
            when (decoration) {
                is CardDecoration.GradientTop -> {
                    CardTopGradientStrip(decoration.brush, decoration.height)
                }
                is CardDecoration.AccentCorner -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CardCornerAccent(
                            color = decoration.color,
                            size = decoration.size,
                            position = decoration.position
                        )
                    }
                }
                is CardDecoration.LeftAccentBar -> {
                    // Handled in row layout
                }
                CardDecoration.None -> {}
            }

            if (decoration is CardDecoration.LeftAccentBar) {
                Row {
                    CardLeftAccentBar(decoration.color, decoration.width)
                    Column(
                        modifier = Modifier.padding(Padding.card),
                        content = content
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(Padding.card),
                    content = content
                )
            }
        }
    }
}

// ============================================
// Gradient Card with Tilted Decoration
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showTiltedDecoration: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {},
        shape = RoundedCornerShape(CornerRadius.xlarge),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(CornerRadius.xlarge)
                )
        ) {
            if (showTiltedDecoration) {
                DecorativeTiltedBlock(
                    size = DecorationSize.tiltedBlock,
                    offsetX = 100.dp,
                    offsetY = (-20).dp,
                    rotation = 15f,
                    alpha = 0.1f
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(DecorationSize.cornerDotLarge)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50.dp)
                    )
            )

            Column(
                modifier = Modifier.padding(Padding.card),
                content = content
            )
        }
    }
}

// ============================================
// Expandable Card with State Indicator
// ============================================
@Composable
fun ExpandableCard(
    title: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Primary
) {
    var scale by remember { mutableStateOf(1f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scale = 0.98f
                        tryAwaitRelease()
                        scale = 1f
                    },
                    onTap = { onExpandToggle() }
                )
            },
        shape = RoundedCornerShape(CornerRadius.large),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 4.dp else 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DecorationSize.stripHeight)
                    .background(accentColor)
            )

            Column(modifier = Modifier.padding(Padding.card)) {
                title()

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))
                        expandedContent()
                    }
                }
            }
        }
    }
}
