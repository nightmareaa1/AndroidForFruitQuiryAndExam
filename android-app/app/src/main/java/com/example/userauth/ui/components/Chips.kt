package com.example.userauth.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.userauth.ui.theme.CornerRadius
import com.example.userauth.ui.theme.Error
import com.example.userauth.ui.theme.ErrorLight
import com.example.userauth.ui.theme.Info
import com.example.userauth.ui.theme.InfoLight
import com.example.userauth.ui.theme.SecondaryDark
import com.example.userauth.ui.theme.Success
import com.example.userauth.ui.theme.SuccessLight
import com.example.userauth.ui.theme.SurfaceVariant
import com.example.userauth.ui.theme.Warning
import com.example.userauth.ui.theme.WarningLight

// ============================================
// Status Types
// ============================================
enum class StatusType {
    ACTIVE,
    PENDING,
    ENDED,
    APPROVED,
    REJECTED,
    INFO
}

// ============================================
// Status Chip Component
// ============================================
@Composable
fun StatusChip(
    text: String,
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = when (status) {
        StatusType.ACTIVE -> Pair(
            SuccessLight,
            Success
        )
        StatusType.PENDING -> Pair(
            WarningLight,
            SecondaryDark
        )
        StatusType.ENDED -> Pair(
            SurfaceVariant,
            Color.Gray
        )
        StatusType.APPROVED -> Pair(
            SuccessLight,
            Success
        )
        StatusType.REJECTED -> Pair(
            ErrorLight,
            Error
        )
        StatusType.INFO -> Pair(
            InfoLight,
            Info
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CornerRadius.medium),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}
