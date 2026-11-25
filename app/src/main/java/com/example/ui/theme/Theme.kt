package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EastBluePixelColorScheme = darkColorScheme(
    primary = StrawGold,
    onPrimary = PixelInk,
    primaryContainer = LuffyRed,
    onPrimaryContainer = HudTextLight,
    secondary = SurfCyan,
    onSecondary = PixelInk,
    secondaryContainer = OceanDeep,
    onSecondaryContainer = HudTextLight,
    tertiary = ReadyGreen,
    onTertiary = PixelInk,
    background = PixelInk,
    onBackground = HudTextLight,
    surface = PixelPanelBg,
    onSurface = HudTextLight,
    surfaceVariant = PixelPanelDark,
    onSurfaceVariant = HudTextMuted,
    outline = PixelBorderLight
)

@Composable
fun GomuArenaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EastBluePixelColorScheme,
        typography = Typography,
        content = content
    )
}
