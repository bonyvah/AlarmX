package com.example.alarmx.domain.usecase

import com.example.alarmx.domain.challenge.ChallengeProvider
import com.example.alarmx.domain.model.AlarmTrigger
import com.example.alarmx.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Produces an [AlarmTrigger] (alarm + freshly-generated challenge) for the
 * given id.
 */
class TriggerAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val challengeProvider: ChallengeProvider,
) {
    suspend operator fun invoke(alarmId: Long): AlarmTrigger? {
        val alarm = alarmRepository.findById(alarmId) ?: return null
        if (!alarm.enabled) return null
        return AlarmTrigger(
            alarmId = alarm.id,
            challenge = challengeProvider.generate(alarm.difficulty),
        )
    }
}
