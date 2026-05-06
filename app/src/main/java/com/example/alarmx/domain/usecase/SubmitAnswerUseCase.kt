package com.example.alarmx.domain.usecase

import com.example.alarmx.domain.model.ArithmeticTask
import com.example.alarmx.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Compares the user's input against the active challenge's expected answer.
 */
class SubmitAnswerUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(
        alarmId: Long?,
        challenge: ArithmeticTask?,
        answer: Int,
    ): SubmitAnswerResult {
        if (alarmId == null || challenge == null) return SubmitAnswerResult.NoActiveChallenge
        return if (answer == challenge.answer()) {
            alarmRepository.dismiss(alarmId)
            SubmitAnswerResult.Correct
        } else {
            SubmitAnswerResult.Incorrect
        }
    }
}

sealed class SubmitAnswerResult {
    data object Correct : SubmitAnswerResult()
    data object Incorrect : SubmitAnswerResult()
    data object NoActiveChallenge : SubmitAnswerResult()
}
