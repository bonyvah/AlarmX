package com.example.alarmx.data.db

import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.model.DifficultyLevel

/**
 * `Alarm` ↔ `AlarmEntity` mapping.
 */
object AlarmMapper {

    fun AlarmEntity.toDomain(): Alarm = Alarm(
        id = id,
        triggerAtEpochMillis = triggerAtEpochMillis,
        label = label,
        enabled = enabled,
        difficulty = parseDifficulty(difficulty),
        snoozeMinutes = snoozeMinutes,
        sound = sound,
        repeatDays = RepeatDaysConverters.fromBitmask(repeatDaysBitmask),
    )

    fun Alarm.toEntity(
        existing: AlarmEntity? = null,
        now: Long = System.currentTimeMillis(),
    ): AlarmEntity = AlarmEntity(
        id = id,
        triggerAtEpochMillis = triggerAtEpochMillis,
        label = label,
        enabled = enabled,
        difficulty = difficulty.name,
        snoozeMinutes = snoozeMinutes,
        sound = sound,
        repeatDaysBitmask = RepeatDaysConverters.toBitmask(repeatDays),
        createdAt = existing?.createdAt ?: now,
        updatedAt = now,
    )

    private fun parseDifficulty(raw: String): DifficultyLevel =
        runCatching { DifficultyLevel.valueOf(raw) }
            .getOrDefault(DifficultyLevel.MEDIUM)
}
