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
    primary = DeepGoldAccent,
    secondary = EmeraldSecondary,
    tertiary = PaleGold,
    background = DarkEmeraldBg,
    surface = DarkEmeraldSurface,
    onPrimary = Color.Black,
    onSecondary = SoftWhiteText,
    onBackground = SoftWhiteText,
    onSurface = SoftWhiteText
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimary,
    secondary = EmeraldSecondary,
    tertiary = DeepGoldAccent,
    background = Color(0xFFF9F6F0),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF031A13),
    onSurface = Color(0xFF031A13)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
