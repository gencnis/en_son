package com.gencnis.enson.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class EnSonColors(
    val plum: Color,
    // Text and icons placed on plum.
    val onPlum: Color,
    val plumDeep: Color,
    val background: Color,
    val surface: Color,
    val pink: Color,
    val sage: Color,
    val yellow: Color,
    val lavender: Color,
    val sky: Color,
    val peach: Color,
    val text: Color,
    val mutedText: Color,
    val border: Color,
    val warning: Color,
    val amber: Color,
    val isDark: Boolean
)

val LightEnSonColors = EnSonColors(
    plum = Color(0xFF69465C),
    onPlum = Color(0xFFFFFFFF),
    plumDeep = Color(0xFF493640),
    background = Color(0xFFFBF7F2),
    surface = Color(0xFFFFFDFC),
    pink = Color(0xFFF2E5E8),
    sage = Color(0xFFE8EEE4),
    yellow = Color(0xFFF4EBCF),
    lavender = Color(0xFFECE6F2),
    sky = Color(0xFFE3ECF2),
    peach = Color(0xFFF6E6DC),
    text = Color(0xFF2C2528),
    mutedText = Color(0xFF7D7277),
    border = Color(0xFFE8DED8),
    warning = Color(0xFF985F5B),
    amber = Color(0xFF9A6C3E),
    isDark = false
)

// Same mood at night: warm plum-charcoal instead of black,
// muted card tones and a softened plum accent.
val DarkEnSonColors = EnSonColors(
    plum = Color(0xFFD9B8CB),
    onPlum = Color(0xFF2B1D25),
    plumDeep = Color(0xFFEDE3E8),
    background = Color(0xFF1B1618),
    surface = Color(0xFF251F22),
    pink = Color(0xFF3E2D36),
    sage = Color(0xFF2C3630),
    yellow = Color(0xFF3A3426),
    lavender = Color(0xFF332D3F),
    sky = Color(0xFF29333B),
    peach = Color(0xFF3D3029),
    text = Color(0xFFF3ECEF),
    mutedText = Color(0xFFA89CA2),
    border = Color(0xFF3A3135),
    warning = Color(0xFFE4A49C),
    amber = Color(0xFFE0B98A),
    isDark = true
)

val LocalEnSonColors =
    staticCompositionLocalOf { LightEnSonColors }

// The names below follow the active palette, so screens use them
// directly and switch with light/dark mode.

val Plum: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.plum

val OnPlum: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.onPlum

val WarmCream: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.background

val SurfacePaper: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.surface

val CardPink: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.pink

val CardSage: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.sage

val CardYellow: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.yellow

val CardLavender: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.lavender

val CardSky: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.sky

val CardPeach: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.peach

val DarkText: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.text

val MutedText: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.mutedText

val SoftBorder: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.border

val OverdueTerracotta: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.warning

val TodayAmber: Color
    @Composable @ReadOnlyComposable
    get() = LocalEnSonColors.current.amber
