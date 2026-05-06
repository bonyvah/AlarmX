package com.example.alarmx.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DefaultFontFamily = FontFamily.Default
private const val TabularNumerics = "tnum"

@Immutable
data class AlarmXTypography(
    val displayXl: TextStyle,
    val displayLg: TextStyle,
    val titleLg: TextStyle,
    val titleMd: TextStyle,
    val bodyMd: TextStyle,
    val labelSm: TextStyle,
    val monoTask: TextStyle,
)

val AlarmXTypographyDefault = AlarmXTypography(
    displayXl = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = TabularNumerics,
    ),
    displayLg = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.25).sp,
        fontFeatureSettings = TabularNumerics,
    ),
    titleLg = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMd = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    bodyMd = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    labelSm = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
    ),
    monoTask = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        fontFeatureSettings = TabularNumerics,
    ),
)

val LocalAlarmXTypography = compositionLocalOf<AlarmXTypography> {
    error("AlarmXTypography not provided")
}

internal val MaterialTypography: Typography = Typography(
    displayLarge = AlarmXTypographyDefault.displayXl,
    displayMedium = AlarmXTypographyDefault.displayLg,
    titleLarge = AlarmXTypographyDefault.titleLg,
    titleMedium = AlarmXTypographyDefault.titleMd,
    bodyMedium = AlarmXTypographyDefault.bodyMd,
    labelSmall = AlarmXTypographyDefault.labelSm,
)