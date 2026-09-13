package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
  primary = JarvisCyan,
  onPrimary = JarvisDarkBackground,
  primaryContainer = JarvisSurfaceVariant,
  onPrimaryContainer = JarvisCyan,
  secondary = JarvisElectricBlue,
  onSecondary = JarvisDarkBackground,
  secondaryContainer = JarvisSurfaceContainerHigh,
  onSecondaryContainer = JarvisElectricBlue,
  tertiary = JarvisStarkGold,
  onTertiary = JarvisDarkBackground,
  background = JarvisDarkBackground,
  onBackground = JarvisTextPrimary,
  surface = JarvisDarkSurface,
  onSurface = JarvisTextPrimary,
  surfaceVariant = JarvisSurfaceVariant,
  onSurfaceVariant = JarvisTextSecondary,
  outline = JarvisHoloBorder,
  error = JarvisAlertRed
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false, // Keep high-tech JARVIS HUD aesthetic consistent
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = JarvisColorScheme,
    typography = Typography,
    content = content
  )
}

