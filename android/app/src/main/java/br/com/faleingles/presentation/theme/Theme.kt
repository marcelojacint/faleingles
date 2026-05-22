package br.com.faleingles.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF1A6BFF)
private val PrimaryDark = Color(0xFF4D8FFF)
private val Secondary = Color(0xFF00C896)
private val SecondaryDark = Color(0xFF00E6AB)
private val Background = Color(0xFFF8F9FF)
private val BackgroundDark = Color(0xFF0F1117)
private val Surface = Color(0xFFFFFFFF)
private val SurfaceDark = Color(0xFF1A1D27)
private val Error = Color(0xFFFF4D4D)
private val OnPrimary = Color(0xFFFFFFFF)
private val OnBackground = Color(0xFF0F1117)
private val OnBackgroundDark = Color(0xFFF0F2FF)

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
