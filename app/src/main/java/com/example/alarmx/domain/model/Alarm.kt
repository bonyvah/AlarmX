package com.example.alarmx.domain.model

import java.time.DayOfWeek

data class Alarm(
    val id: Long,
    val triggerAtEpochMillis: Long,
    val label: String = "",
    val enabled: Boolean = true,
    val difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val snoozeMinutes: Int = 5,
    val sound: String = "default",
    val repeatDays: Set<DayOfWeek> = emptySet(),
)