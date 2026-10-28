package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = TdBlueLight,
    onPrimary = Color.White,
    primaryContainer = TdBlueDark,
    onPrimaryContainer = Color.White,
    secondary = TdPurpleLight,
    onSecondary = Color.White,
    secondaryContainer = TdPurpleDark,
    onSecondaryContainer = Color.White,
    tertiary = TdYellowBright,
    onTertiary = Color.Black,
    tertiaryContainer = TdYellowDark,
    onTertiaryContainer = Color.White,
    background = TdBackgroundDark,
    onBackground = TdTextPrimaryDark,
    surface = TdSurfaceDark,
    onSurface = TdTextPrimaryDark,
    surfaceVariant = TdSurfaceVariantDark,
    onSurfaceVariant = TdTextSecondaryDark,
    error = TdErrorRed,
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TdBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = TdBlueDark,
    secondary = TdPurpleSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = TdPurpleDark,
    tertiary = TdYellowDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    error = TdErrorRed,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to rich vibrant dark theme for cartoon video studio
  dynamicColor: Boolean = false, // Keep brand blue, purple, yellow vibrant colors
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
