package com.example.pocketledger.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PocketLedgerDarkPrimary,
    onPrimary = PocketLedgerDarkOnPrimary,
    primaryContainer = PocketLedgerDarkPrimaryContainer,
    onPrimaryContainer = PocketLedgerDarkOnPrimaryContainer,
    secondary = PocketLedgerDarkSecondary,
    onSecondary = PocketLedgerDarkOnSecondary,
    secondaryContainer = PocketLedgerDarkSecondaryContainer,
    onSecondaryContainer = PocketLedgerDarkOnSecondaryContainer,
    tertiary = PocketLedgerDarkTertiary,
    onTertiary = PocketLedgerDarkOnTertiary,
    tertiaryContainer = PocketLedgerDarkTertiaryContainer,
    onTertiaryContainer = PocketLedgerDarkOnTertiaryContainer,
    error = PocketLedgerDarkError,
    onError = PocketLedgerDarkOnError,
    errorContainer = PocketLedgerDarkErrorContainer,
    onErrorContainer = PocketLedgerDarkOnErrorContainer,
    background = PocketLedgerDarkBackground,
    onBackground = PocketLedgerDarkOnBackground,
    surface = PocketLedgerDarkSurface,
    onSurface = PocketLedgerDarkOnSurface,
    surfaceVariant = PocketLedgerDarkSurfaceVariant,
    onSurfaceVariant = PocketLedgerDarkOnSurfaceVariant,
    outline = PocketLedgerDarkOutline,
    outlineVariant = PocketLedgerDarkOutlineVariant,
    scrim = PocketLedgerDarkScrim,
    inverseSurface = PocketLedgerDarkInverseSurface,
    inverseOnSurface = PocketLedgerDarkInverseOnSurface,
    inversePrimary = PocketLedgerDarkInversePrimary
)

private val LightColorScheme = lightColorScheme(
    primary = PocketLedgerPrimary,
    onPrimary = PocketLedgerOnPrimary,
    primaryContainer = PocketLedgerPrimaryContainer,
    onPrimaryContainer = PocketLedgerOnPrimaryContainer,
    secondary = PocketLedgerSecondary,
    onSecondary = PocketLedgerOnSecondary,
    secondaryContainer = PocketLedgerSecondaryContainer,
    onSecondaryContainer = PocketLedgerOnSecondaryContainer,
    tertiary = PocketLedgerTertiary,
    onTertiary = PocketLedgerOnTertiary,
    tertiaryContainer = PocketLedgerTertiaryContainer,
    onTertiaryContainer = PocketLedgerOnTertiaryContainer,
    error = PocketLedgerError,
    onError = PocketLedgerOnError,
    errorContainer = PocketLedgerErrorContainer,
    onErrorContainer = PocketLedgerOnErrorContainer,
    background = PocketLedgerBackground,
    onBackground = PocketLedgerOnBackground,
    surface = PocketLedgerSurface,
    onSurface = PocketLedgerOnSurface,
    surfaceVariant = PocketLedgerSurfaceVariant,
    onSurfaceVariant = PocketLedgerOnSurfaceVariant,
    outline = PocketLedgerOutline,
    outlineVariant = PocketLedgerOutlineVariant,
    scrim = PocketLedgerScrim,
    inverseSurface = PocketLedgerInverseSurface,
    inverseOnSurface = PocketLedgerInverseOnSurface,
    inversePrimary = PocketLedgerInversePrimary
)

@Composable
fun PocketLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
