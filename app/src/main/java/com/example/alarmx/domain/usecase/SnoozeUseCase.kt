package com.example.alarmx.domain.usecase

import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.repository.AlarmRepository
import com.example.alarmx.domain.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Pushes an alarm forward by its configured snooze interval.
 */
class SnoozeUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(alarmId: Long, nowEpochMillis: Long): Alarm? {
        val prefs = preferencesRepository.get()
        if (!prefs.snoozeEnabled) return null

        val alarm = alarmRepository.findById(alarmId) ?: return null
        if (!alarm.enabled) return null

        val snoozed = alarm.copy(
            triggerAtEpochMillis = nowEpochMillis + alarm.snoozeMinutes * MILLIS_PER_MINUTE,
            enabled = true,
        )
        alarmRepository.save(snoozed)
        return snoozed
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}
