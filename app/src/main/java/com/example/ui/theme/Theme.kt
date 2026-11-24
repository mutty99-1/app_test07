package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IvorySeniorColorScheme = lightColorScheme(
    primary = MedDoneContainer,
    onPrimary = MedDoneContent,
    secondary = WaterButtonBlue,
    onSecondary = MedDoneContent,
    background = IvoryBackground,
    onBackground = InkPrimary,
    surface = IvorySurface,
    onSurface = InkPrimary,
    surfaceVariant = IvorySurfaceWarm,
    onSurfaceVariant = InkSecondary,
    outline = IvoryBorder
)

@Composable
fun SeniorCareTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IvorySeniorColorScheme,
        typography = SeniorTypography,
        content = content
    )
}
