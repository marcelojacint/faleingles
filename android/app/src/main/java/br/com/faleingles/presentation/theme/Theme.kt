package br.com.faleingles.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF1A6BFF)
private val PrimaryDark = Color(0xFF5E9BFF)
private val Secondary = Color(0xFF00B87A)
private val SecondaryDark = Color(0xFF00D48C)
private val Background = Color(0xFFFFFFFF)
private val BackgroundDark = Color(0xFF0D0D0D)
private val Surface = Color(0xFFF5F5F5)
private val SurfaceDark = Color(0xFF1A1A1A)
private val Error = Color(0xFFD32F2F)
private val OnPrimary = Color(0xFFFFFFFF)
private val OnBackground = Color(0xFF111111)
private val OnBackgroundDark = Color(0xFFF5F5F5)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = OnPrimary,
    onBackground = OnBackground,
    onSurface = OnBackground,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = Error,
    onPrimary = OnPrimary,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
)

@Composable
fun FaleInglesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FaleInglesTypography,
        content = content
    )
}
