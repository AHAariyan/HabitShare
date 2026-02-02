package com.habit.habitshare.ui.theme

import androidx.compose.ui.graphics.Color

// HabitShare color palette - matching reference design
object HabitShareColors {
    // Primary colors - Teal
    val Primary = Color(0xFF009688)              // Teal - Top bar, primary actions
    val PrimaryDark = Color(0xFF00796B)          // Dark teal
    val PrimaryLight = Color(0xFF4DB6AC)         // Light teal
    val OnPrimary = Color(0xFFFFFFFF)            // White text on primary

    // Status colors
    val Success = Color(0xFF8BC34A)              // Lime green - Habit completed
    val SuccessLight = Color(0xFFDCEDC8)         // Light lime green
    val Fail = Color(0xFFF44336)                 // Red - Habit missed
    val FailLight = Color(0xFFFFCDD2)            // Light red
    val Skip = Color(0xFF9E9E9E)                 // Medium grey - Day skipped
    val SkipLight = Color(0xFFE0E0E0)            // Light grey
    val Empty = Color(0xFFE0E0E0)                // Light grey - No data/Future
    val EmptyLight = Color(0xFFF5F5F5)           // Very light grey

    // Surface colors
    val Surface = Color(0xFFFFFFFF)              // White background
    val SurfaceVariant = Color(0xFFF5F5F5)       // Slightly grey background
    val CardBackground = Color(0xFFFFFFFF)       // Card background
    val Divider = Color(0xFFE0E0E0)              // Divider lines

    // Text colors
    val TextPrimary = Color(0xFF212121)          // Dark text
    val TextSecondary = Color(0xFF757575)        // Secondary/muted text
    val TextOnPrimary = Color(0xFFFFFFFF)        // White text on teal
    val TextHint = Color(0xFFBDBDBD)             // Hint/placeholder text

    // Selection colors
    val SelectionBorder = Color(0xFF80CBC4)      // Light teal border for selection

    // Day circle colors
    val DayUnselected = Color(0xFFE0E0E0)        // Unselected day circle
    val DaySelected = Color(0xFF8BC34A)          // Selected day circle (same as success)
    val DayText = Color(0xFFFFFFFF)              // White text on day circles
    val DayTextUnselected = Color(0xFF9E9E9E)    // Grey text on unselected days
}

// For Material Theme compatibility
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
