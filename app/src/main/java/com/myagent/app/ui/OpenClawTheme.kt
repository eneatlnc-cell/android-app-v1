package com.myagent.app.ui

import com.myagent.app.AppearanceThemeMode
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalOpenClawDarkTheme = staticCompositionLocalOf { true }

/**
 * App theme wrapper — 使用固定深色/浅色主题，不依赖系统 Wallpaper 动态色。
 */
@Composable
fun OpenClawTheme(
  themeMode: AppearanceThemeMode = AppearanceThemeMode.Dark,
  content: @Composable () -> Unit,
) {
  val isDark = themeMode.isDark(systemDark = isSystemInDarkTheme())
  val colorScheme = if (isDark) {
    darkColorScheme(
      primary = Color(0xFF6C5CE7),
      onPrimary = Color.White,
      background = Color(0xFF0A0A0F),
      onBackground = Color(0xFFE8EAF0),
      surface = Color(0xFF14141F),
      onSurface = Color(0xFFE8EAF0),
      surfaceVariant = Color(0xFF1C1C2E),
      onSurfaceVariant = Color(0xFF9B9FB8),
      outline = Color(0xFF2A2A3E),
      error = Color(0xFFFF7675),
      onError = Color.White,
    )
  } else {
    lightColorScheme(
      primary = Color(0xFF6C5CE7),
      onPrimary = Color.White,
      background = Color(0xFFFAFBFC),
      onBackground = Color(0xFF16181D),
      surface = Color.White,
      onSurface = Color(0xFF16181D),
      surfaceVariant = Color(0xFFFFFFFF),
      onSurfaceVariant = Color(0xFF5A6072),
      outline = Color(0xFFE0E4EC),
      error = Color(0xFFE87070),
      onError = Color.White,
    )
  }
  val mobileColors = if (isDark) darkMobileColors() else lightMobileColors()

  OpenClawSystemBarAppearance(lightAppearance = !isDark)

  CompositionLocalProvider(
    LocalMobileColors provides mobileColors,
  ) {
    MaterialTheme(colorScheme = colorScheme, content = content)
  }
}

@Composable
internal fun OpenClawSystemBarAppearance(lightAppearance: Boolean) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window ?: return@SideEffect
      WindowCompat
        .getInsetsController(window, window.decorView)
        .isAppearanceLightStatusBars = lightAppearance
      WindowCompat
        .getInsetsController(window, window.decorView)
        .isAppearanceLightNavigationBars = lightAppearance
    }
  }
}

/**
 * Overlay background token tuned for panels floating over the mobile canvas.
 */
@Composable
fun overlayContainerColor(): Color {
  val scheme = MaterialTheme.colorScheme
  val isDark = LocalOpenClawDarkTheme.current
  val base = if (isDark) scheme.surfaceContainerLow else scheme.surfaceContainerHigh
  // Light mode keeps overlays away from pure-white glare on the app canvas.
  return if (isDark) base else base.copy(alpha = 0.88f)
}

/**
 * Overlay icon token kept next to overlayContainerColor for callers outside the design package.
 */
@Composable
fun overlayIconColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant
