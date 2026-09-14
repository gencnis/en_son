package com.gencnis.enson.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EnSonColorScheme = lightColorScheme(
    primary = Plum,
    onPrimary = White,

    primaryContainer = CardPink,
    onPrimaryContainer = DarkText,

    secondary = PlumDark,
    onSecondary = White,

    background = WarmCream,
    onBackground = DarkText,

    surface = WarmCream,
    onSurface = DarkText,

    surfaceVariant = CardPink,
    onSurfaceVariant = MutedText,

    outline = SoftBorder
)

@Composable
fun EnSonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnSonColorScheme,
        typography = Typography,
        content = content
    )
}