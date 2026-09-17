package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = GreenButton,
  onPrimary = PureWhite,
  primaryContainer = GreenContainer,
  onPrimaryContainer = OnGreenContainer,
  secondary = AmberAccent,
  onSecondary = PureWhite,
  secondaryContainer = AmberContainer,
  onSecondaryContainer = OnAmberContainer,
  tertiary = OrangeAccent,
  onTertiary = PureWhite,
  background = OffWhiteBg,
  onBackground = TextDarkPrimary,
  surface = CardSurface,
  onSurface = TextDarkPrimary,
  surfaceVariant = Color(0xFFF1F6F2),
  onSurfaceVariant = TextDarkSecondary,
  outline = CardBorder,
  outlineVariant = DividerColor,
  error = RedError,
  errorContainer = RedErrorContainer,
  onError = PureWhite,
  onErrorContainer = Color(0xFF5C0003)
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  // Always use bright, modern, high-contrast light theme as requested
  MaterialTheme(
    colorScheme = LightColorScheme,
    typography = Typography,
    content = content
  )
}
