package com.example.alarmx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

private val MaterialLightScheme = lightColorScheme(
    primary = AccentBlueLight,
    onPrimary = SurfaceBaseLight,
    primaryContainer = AccentBlueSoftLight,
    onPrimaryContainer = AccentBlueLight,
    background = SurfaceBaseLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceBaseLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceRaisedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceStrokeLight,
    outlineVariant = SurfaceStrokeLight,
    error = StateDangerLight,
    onError = SurfaceBaseLight,
)

private val MaterialDarkScheme = darkColorScheme(
    primary = AccentBlueDark,
    onPrimary = SurfaceBaseLight,
    primaryContainer = AccentBlueSoftDark,
    onPrimaryContainer = AccentBlueDark,
    background = SurfaceBaseDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceBaseDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceRaisedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceStrokeDark,
    outlineVariant = SurfaceStrokeDark,
    error = StateDangerDark,
    onError = SurfaceBaseDark,
)

@Composable
fun AlarmXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkAlarmXColors else LightAlarmXColors
    val materialScheme = if (darkTheme) MaterialDarkScheme else MaterialLightScheme

    CompositionLocalProvider(
        LocalAlarmXColors provides colors,
        LocalAlarmXTypography provides AlarmXTypographyDefault,
        LocalAbsoluteTonalElevation provides 0.dp,
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = MaterialTypography,
            content = content,
        )
    }
}

object AlarmXTheme {
    val colors: AlarmXColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAlarmXColors.current

    val typography: AlarmXTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAlarmXTypography.current
}