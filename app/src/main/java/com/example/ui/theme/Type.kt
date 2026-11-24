package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoSansKRFontFamily = FontFamily(
    Font(R.font.noto_sans_kr, FontWeight.Normal),
    Font(R.font.noto_sans_kr, FontWeight.Medium),
    Font(R.font.noto_sans_kr, FontWeight.SemiBold),
    Font(R.font.noto_sans_kr, FontWeight.Bold),
    Font(R.font.noto_sans_kr, FontWeight.ExtraBold)
)

// Every text style is strictly >= 22sp for senior legibility
val SeniorTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    )
)
