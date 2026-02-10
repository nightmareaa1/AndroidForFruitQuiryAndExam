package com.example.userauth.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================
// Primary Brand Colors - Natural Green Theme
// ============================================
val Primary = Color(0xFF4CAF50)           // Natural Green - Main brand color
val PrimaryLight = Color(0xFF81C784)      // Light Green - Hover state
val PrimaryDark = Color(0xFF2E7D32)       // Dark Green - Pressed/Disabled state
val OnPrimary = Color.White               // White text on primary

// ============================================
// Secondary Colors - Warm Orange
// ============================================
val Secondary = Color(0xFFFF9800)         // Warm Orange
val SecondaryLight = Color(0xFFFFB74D)    // Light Orange
val SecondaryDark = Color(0xFFF57C00)     // Dark Orange
val OnSecondary = Color.White

// ============================================
// Accent Colors - Brand Decoration
// ============================================
val AccentCyan = Color(0xFF26A69A)        // Cyan Green - Tech decoration
val AccentYellow = Color(0xFFFFEB3B)      // Fresh Yellow - Decorative accent
val AccentPink = Color(0xFFE91E63)        // Berry Pink - Highlight actions

// ============================================
// Semantic Colors - Status Indicators
// ============================================
val Success = Color(0xFF66BB6A)           // Success - Deep green
val SuccessLight = Color(0xFFE8F5E9)      // Success background
val Warning = Color(0xFFFFA726)           // Warning - Amber
val WarningLight = Color(0xFFFFF3E0)      // Warning background
val Error = Color(0xFFEF5350)             // Error - Red
val ErrorLight = Color(0xFFFFEBEE)        // Error background
val Info = Color(0xFF42A5F5)              // Info - Blue
val InfoLight = Color(0xFFE3F2FD)         // Info background

// ============================================
// Neutral Colors - UI Foundation
// ============================================
val Background = Color(0xFFF8FAF8)        // Warm white with green tint
val Surface = Color.White                  // Pure white for cards
val SurfaceVariant = Color(0xFFF5F5F5)    // Light gray surface
val Outline = Color(0xFFE0E0E0)           // Border lines
val OnBackground = Color(0xFF212121)      // Dark gray text
val OnSurface = Color(0xFF212121)         // Dark gray text
val OnSurfaceVariant = Color(0xFF757575)  // Medium gray secondary text

// ============================================
// Legacy Material Colors (for backward compatibility)
// ============================================
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ============================================
// Brand Gradient Colors
// ============================================
val GradientPrimary = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
val GradientSecondary = listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
val GradientAccent = listOf(Color(0xFF26A69A), Color(0xFF4CAF50))

// ============================================
// Brand Brushes (Pre-defined Gradients)
// ============================================
object BrandGradients {
    val PrimaryGradient = Brush.linearGradient(colors = GradientPrimary)
    val SecondaryGradient = Brush.linearGradient(colors = GradientSecondary)
    val AccentGradient = Brush.linearGradient(colors = GradientAccent)
    
    // Login background decoration gradient
    val LoginBackground = Brush.verticalGradient(
        colors = listOf(
            Primary,
            Primary.copy(alpha = 0.8f),
            AccentCyan.copy(alpha = 0.3f),
            Background
        )
    )
    
    // Radial gradient for decorative circles
    val DecorationRadial = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.Transparent
        )
    )
}

// ============================================
// Decoration Colors
// ============================================
val DecorationLight = Primary.copy(alpha = 0.08f)
val DecorationMedium = Primary.copy(alpha = 0.15f)