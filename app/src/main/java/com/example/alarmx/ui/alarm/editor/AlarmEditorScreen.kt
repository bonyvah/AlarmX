package com.example.alarmx.ui.alarm.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.model.DifficultyLevel
import com.example.alarmx.domain.util.AlarmScheduling
import com.example.alarmx.ui.alarm.AlarmViewModel
import com.example.alarmx.ui.common.AxChip
import com.example.alarmx.ui.common.AxPrimaryButton
import com.example.alarmx.ui.common.AxSecondaryButton
import com.example.alarmx.ui.common.AxSegmented
import com.example.alarmx.ui.common.AxToggle
import com.example.alarmx.ui.theme.AlarmXTheme
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditorScreen(
    viewModel: AlarmViewModel,
    alarmId: Long?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = AlarmXTheme.colors
    val typography = AlarmXTheme.typography

    val isEdit = alarmId != null

    var initialised by remember { mutableStateOf(!isEdit) }
    var label by remember { mutableStateOf("") }
    var repeatDays by remember { mutableStateOf<Set<DayOfWeek>>(emptySet()) }
    var difficulty by remember { mutableStateOf(state.preferences.difficulty) }
    var snoozeEnabled by remember { mutableStateOf(state.preferences.snoozeEnabled) }
    var snoozeMinutes by remember { mutableStateOf(state.preferences.defaultSnoozeMinutes) }
    var sound by remember { mutableStateOf(state.preferences.sound) }
    var existingId by remember { mutableStateOf<Long?>(null) }
    var enabled by remember { mutableStateOf(true) }

    val defaultTime = remember {
        val now = LocalTime.now()
        LocalTime.of((now.hour + 1) % 24, 0)
    }
    val timePickerState = rememberTimePickerState(
        initialHour = defaultTime.hour,
        initialMinute = defaultTime.minute,
        is24Hour = true,
    )

    LaunchedEffect(alarmId) {
        if (alarmId == null) {
            initialised = true
            return@LaunchedEffect
        }
        val loaded = viewModel.loadAlarm(alarmId)
        if (loaded == null) {
            onClose()
            return@LaunchedEffect
        }
        existingId = loaded.id
        label = loaded.label
        repeatDays = loaded.repeatDays
        difficulty = loaded.difficulty
        snoozeMinutes = loaded.snoozeMinutes
        snoozeEnabled = loaded.snoozeMinutes > 0
        sound = loaded.sound
        enabled = loaded.enabled
        val local = java.time.Instant.ofEpochMilli(loaded.triggerAtEpochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        timePickerState.hour = local.hour
        timePickerState.minute = local.minute
        initialised = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.surfaceBase,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Edit alarm" else "New alarm",
                        style = typography.titleLg,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Text(text = "✕", style = typography.titleLg, color = colors.textPrimary)
                    }
                },
                actions = {
                    TextButton(
                        enabled = initialised,
                        onClick = {
                            val alarm = buildAlarm(
                                existingId = existingId,
                                hour = timePickerState.hour,
                                minute = timePickerState.minute,
                                label = label,
                                repeatDays = repeatDays,
                                difficulty = difficulty,
                                snoozeMinutes = if (snoozeEnabled) snoozeMinutes else 0,
                                sound = sound,
                                enabled = enabled,
                            )
                            viewModel.saveAlarm(alarm)
                            onClose()
                        },
                    ) {
                        Text(
                            text = "Save",
                            style = typography.titleMd,
                            color = colors.accentBlue,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surfaceBase,
                    titleContentColor = colors.textPrimary,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = colors.surfaceRaised,
                        clockDialSelectedContentColor = colors.surfaceBase,
                        clockDialUnselectedContentColor = colors.textPrimary,
                        selectorColor = colors.accentBlue,
                        containerColor = colors.surfaceBase,
                        periodSelectorBorderColor = colors.surfaceStroke,
                        periodSelectorSelectedContainerColor = colors.accentBlueSoft,
                        periodSelectorUnselectedContainerColor = colors.surfaceBase,
                        periodSelectorSelectedContentColor = colors.accentBlue,
                        periodSelectorUnselectedContentColor = colors.textSecondary,
                        timeSelectorSelectedContainerColor = colors.accentBlueSoft,
                        timeSelectorUnselectedContainerColor = colors.surfaceRaised,
                        timeSelectorSelectedContentColor = colors.accentBlue,
                        timeSelectorUnselectedContentColor = colors.textPrimary,
                    ),
                )
            }

            Section(title = "Label") {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Alarm",
                            style = typography.bodyMd,
                            color = colors.textTertiary,
                        )
                    },
                    singleLine = true,
                    textStyle = typography.bodyMd.copy(color = colors.textPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentBlue,
                        unfocusedBorderColor = colors.surfaceStroke,
                        focusedContainerColor = colors.surfaceBase,
                        unfocusedContainerColor = colors.surfaceBase,
                        cursorColor = colors.accentBlue,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                    ),
                )
            }

            Section(title = "Repeat") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    WeekOrder.forEach { day ->
                        AxChip(
                            label = shortDay(day),
                            selected = day in repeatDays,
                            onClick = {
                                repeatDays = if (day in repeatDays) {
                                    repeatDays - day
                                } else {
                                    repeatDays + day
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Text(
                    text = if (repeatDays.isEmpty()) "One-time alarm" else "Repeats weekly",
                    style = typography.labelSm,
                    color = colors.textTertiary,
                )
            }

            Section(title = "Difficulty") {
                AxSegmented(
                    options = DifficultyLevel.values().toList(),
                    selected = difficulty,
                    onSelect = { difficulty = it },
                    label = { it.name.lowercase().replaceFirstChar { c -> c.titlecase() } },
                )
            }

            Section(title = "Snooze") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Allow snooze",
                            style = typography.bodyMd,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = if (snoozeEnabled) "$snoozeMinutes minutes" else "Off",
                            style = typography.labelSm,
                            color = colors.textSecondary,
                        )
                    }
                    AxToggle(
                        checked = snoozeEnabled,
                        onCheckedChange = { snoozeEnabled = it },
                    )
                }
                if (snoozeEnabled) {
                    AxSegmented(
                        options = SnoozeChoices,
                        selected = snoozeMinutes.takeIf { it in SnoozeChoices } ?: 5,
                        onSelect = { snoozeMinutes = it },
                        label = { "$it m" },
                    )
                }
            }

            if (isEdit && existingId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.surfaceStroke),
                )
                AxSecondaryButton(
                    text = "Delete alarm",
                    onClick = {
                        viewModel.deleteAlarm(existingId!!)
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            AxPrimaryButton(
                text = if (isEdit) "Save changes" else "Create alarm",
                onClick = {
                    val alarm = buildAlarm(
                        existingId = existingId,
                        hour = timePickerState.hour,
                        minute = timePickerState.minute,
                        label = label,
                        repeatDays = repeatDays,
                        difficulty = difficulty,
                        snoozeMinutes = if (snoozeEnabled) snoozeMinutes else 0,
                        sound = sound,
                        enabled = enabled,
                    )
                    viewModel.saveAlarm(alarm)
                    onClose()
                },
                enabled = initialised,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    val colors = AlarmXTheme.colors
    val typography = AlarmXTheme.typography
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title.uppercase(),
            style = typography.labelSm,
            color = colors.textSecondary,
        )
        content()
    }
}

private val WeekOrder = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY,
)

private val SnoozeChoices = listOf(1, 5, 10, 15)

private fun shortDay(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "M"
    DayOfWeek.TUESDAY -> "T"
    DayOfWeek.WEDNESDAY -> "W"
    DayOfWeek.THURSDAY -> "T"
    DayOfWeek.FRIDAY -> "F"
    DayOfWeek.SATURDAY -> "S"
    DayOfWeek.SUNDAY -> "S"
}

private fun buildAlarm(
    existingId: Long?,
    hour: Int,
    minute: Int,
    label: String,
    repeatDays: Set<DayOfWeek>,
    difficulty: DifficultyLevel,
    snoozeMinutes: Int,
    sound: String,
    enabled: Boolean,
): Alarm {
    val triggerAtEpochMillis = AlarmScheduling.nextTriggerEpochMillis(
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
    )

    val id = existingId ?: System.currentTimeMillis()

    return Alarm(
        id = id,
        triggerAtEpochMillis = triggerAtEpochMillis,
        label = label.trim(),
        enabled = enabled,
        difficulty = difficulty,
        snoozeMinutes = snoozeMinutes,
        sound = sound,
        repeatDays = repeatDays,
    )
}
