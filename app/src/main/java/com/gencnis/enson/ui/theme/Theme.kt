package com.gencnis.enson.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private fun EnSonColors.toColorScheme(): ColorScheme {
    val base = if (isDark) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    return base.copy(
        primary = plum,
        onPrimary = onPlum,

        primaryContainer = pink,
        onPrimaryContainer = text,

        secondary = plumDeep,
        onSecondary = background,

        background = background,
        onBackground = text,

        surface = background,
        onSurface = text,

        surfaceVariant = pink,
        onSurfaceVariant = mutedText,

        surfaceContainerHigh = surface,
        surfaceContainerHighest = surface,

        outline = border,

        // Snackbars: deep plum with a soft pink action.
        inverseSurface = plumDeep,
        inverseOnSurface = background,
        inversePrimary = if (isDark) {
            LightEnSonColors.plum
        } else {
            pink
        }
    )
}

@Composable
fun EnSonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkEnSonColors
    } else {
        LightEnSonColors
    }

    CompositionLocalProvider(
        LocalEnSonColors provides colors
    ) {
        MaterialTheme(
            colorScheme = colors.toColorScheme(),
            typography = Typography,
            content = content
        )
    }
}
