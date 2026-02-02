package com.habit.habitshare.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = HabitShareColors.Primary,
    onPrimary = HabitShareColors.OnPrimary,
    primaryContainer = HabitShareColors.PrimaryLight,
    onPrimaryContainer = HabitShareColors.TextPrimary,
    secondary = HabitShareColors.Success,
    onSecondary = Color.White,
    secondaryContainer = HabitShareColors.SuccessLight,
    onSecondaryContainer = HabitShareColors.TextPrimary,
    tertiary = HabitShareColors.Primary,
    onTertiary = Color.White,
    background = HabitShareColors.Surface,
    onBackground = HabitShareColors.TextPrimary,
    surface = HabitShareColors.Surface,
    onSurface = HabitShareColors.TextPrimary,
    surfaceVariant = HabitShareColors.SurfaceVariant,
    onSurfaceVariant = HabitShareColors.TextSecondary,
    outline = HabitShareColors.Divider
)

private val DarkColorScheme = darkColorScheme(
    primary = HabitShareColors.PrimaryLight,
    onPrimary = HabitShareColors.TextPrimary,
    primaryContainer = HabitShareColors.PrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = HabitShareColors.Success,
    onSecondary = Color.White,
    secondaryContainer = HabitShareColors.SuccessLight,
    onSecondaryContainer = HabitShareColors.TextPrimary,
    tertiary = HabitShareColors.PrimaryLight,
    onTertiary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF404040)
)

@Composable
fun HabitShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = HabitShareColors.Primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
