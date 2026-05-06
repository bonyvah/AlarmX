package com.example.alarmx.domain.util

import com.example.alarmx.domain.model.Alarm
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Pure-Kotlin scheduling helpers.
 */
object AlarmScheduling {

    fun nextTriggerEpochMillis(
        hour: Int,
        minute: Int,
        repeatDays: Set<DayOfWeek>,
        zone: ZoneId = ZoneId.systemDefault(),
        now: LocalDateTime = LocalDateTime.now(zone),
    ): Long {
        val today = now.toLocalDate()
        val targetTime = LocalTime.of(hour, minute)

        val nextDate: LocalDate = if (repeatDays.isEmpty()) {
            if (targetTime.isAfter(now.toLocalTime())) today else today.plusDays(1)
        } else {
            val todayIsEligible =
                today.dayOfWeek in repeatDays && targetTime.isAfter(now.toLocalTime())
            if (todayIsEligible) {
                today
            } else {
                (1..7).asSequence()
                    .map { today.plusDays(it.toLong()) }
                    .first { it.dayOfWeek in repeatDays }
            }
        }
        return LocalDateTime.of(nextDate, targetTime)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    }

    fun advanceRepeating(
        alarm: Alarm,
        after: Instant = Instant.now(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): Alarm? {
        if (alarm.repeatDays.isEmpty()) return null
        val originalTime = Instant.ofEpochMilli(alarm.triggerAtEpochMillis)
            .atZone(zone)
            .toLocalTime()
        val reference = LocalDateTime.ofInstant(after, zone)
        val next = nextTriggerEpochMillis(
            hour = originalTime.hour,
            minute = originalTime.minute,
            repeatDays = alarm.repeatDays,
            zone = zone,
            now = reference,
        )
        return alarm.copy(triggerAtEpochMillis = next)
    }
}
