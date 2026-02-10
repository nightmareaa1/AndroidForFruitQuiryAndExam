package com.example.userauth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.userauth.ui.theme.CornerRadius
import com.example.userauth.ui.theme.Dimensions
import com.example.userauth.ui.theme.GradientPrimary
import com.example.userauth.ui.theme.Primary

// ============================================
// Button Types
// ============================================
enum class AppButtonType {
    Primary,
    Secondary,
    Outlined,
    Text,
    Gradient
}

// ============================================
// Primary Brand Button
// ============================================
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    buttonType: AppButtonType = AppButtonType.Primary
) {
    val backgroundColor = when (buttonType) {
        AppButtonType.Primary -> Primary
        AppButtonType.Secondary -> MaterialTheme.colorScheme.secondary
        AppButtonType.Outlined, AppButtonType.Text -> Color.Transparent
        AppButtonType.Gradient -> Color.Transparent
    }

    val contentColor = when (buttonType) {
        AppButtonType.Primary, AppButtonType.Secondary, AppButtonType.Gradient -> Color.White
        AppButtonType.Outlined, AppButtonType.Text -> Primary
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(Dimensions.buttonHeight),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(CornerRadius.large),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.38f),
            disabledContentColor = contentColor.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============================================
// Gradient Button
// ============================================
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = GradientPrimary,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(Dimensions.buttonHeight)
            .clip(RoundedCornerShape(CornerRadius.large))
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(gradientColors)
                } else {
                    Brush.horizontalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.38f) }
                    )
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
