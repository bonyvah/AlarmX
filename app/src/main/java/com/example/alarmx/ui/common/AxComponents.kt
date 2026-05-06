package com.example.alarmx.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.alarmx.ui.theme.AlarmXTheme

@Composable
fun AxToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AlarmXTheme.colors
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colors.surfaceBase,
            checkedTrackColor = colors.accentBlue,
            checkedBorderColor = colors.accentBlue,
            checkedIconColor = colors.accentBlue,
            uncheckedThumbColor = colors.textPrimary,
            uncheckedTrackColor = colors.surfaceRaised,
            uncheckedBorderColor = colors.surfaceStroke,
            uncheckedIconColor = colors.surfaceRaised,
            disabledCheckedThumbColor = colors.surfaceBase,
            disabledCheckedTrackColor = colors.textTertiary,
            disabledCheckedBorderColor = colors.textTertiary,
            disabledUncheckedThumbColor = colors.textTertiary,
            disabledUncheckedTrackColor = colors.surfaceRaised,
            disabledUncheckedBorderColor = colors.surfaceStroke,
        ),
    )
}

@Composable
fun AxPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AlarmXTheme.colors
    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .heightIn(min = 56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.accentBlue,
            contentColor = colors.surfaceBase,
            disabledContainerColor = colors.surfaceRaised,
            disabledContentColor = colors.textTertiary,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, style = AlarmXTheme.typography.titleMd)
        }
    }
}

@Composable
fun AxSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AlarmXTheme.colors
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .heightIn(min = 56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 1.dp, color = colors.surfaceStroke),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colors.surfaceBase,
            contentColor = colors.textPrimary,
            disabledContainerColor = colors.surfaceBase,
            disabledContentColor = colors.textTertiary,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, style = AlarmXTheme.typography.titleMd)
        }
    }
}

@Composable
fun AxChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AlarmXTheme.colors
    val bg = when {
        !enabled -> colors.surfaceRaised
        selected -> colors.accentBlueSoft
        else -> colors.surfaceRaised
    }
    val fg = when {
        !enabled -> colors.textTertiary
        selected -> colors.accentBlue
        else -> colors.textSecondary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = AlarmXTheme.typography.labelSm, color = fg)
    }
}

@Composable
fun <T> AxSegmented(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            AxChip(
                label = label(option),
                selected = option == selected,
                onClick = { onSelect(option) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
