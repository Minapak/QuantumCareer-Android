package com.swiftquantum.quantumcareer.presentation.ui.theme

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
    primary = CareerGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFECB3),
    onPrimaryContainer = Color(0xFF3D2E00),
    secondary = QuantumTeal,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFCEFAF8),
    onSecondaryContainer = Color(0xFF00201F),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

private val DarkColorScheme = darkColorScheme(
    primary = CareerGold,
    onPrimary = Color.Black,
    primaryContainer = CareerGoldContainer,
    onPrimaryContainer = OnCareerGoldContainer,
    secondary = Color(0xFFA8F5F0),
    onSecondary = Color(0xFF003735),
    secondaryContainer = Color(0xFF004F4D),
    onSecondaryContainer = Color(0xFFCEFAF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = BackgroundDark,  // #0A0A0A
    onBackground = TextPrimary,
    surface = SurfaceDark,        // #121212
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextDisabled,
    outlineVariant = SurfaceDarkVariant
)

@Composable
fun QuantumCareerTheme(
    darkTheme: Boolean = true,  // Dark mode dominant
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
