package com.example.alarmx.domain.challenge

import com.example.alarmx.domain.model.ArithmeticTask
import com.example.alarmx.domain.model.DifficultyLevel

/**
 * Generates a single dismissal challenge for a given [DifficultyLevel].
 */
fun interface ChallengeProvider {
    fun generate(difficulty: DifficultyLevel): ArithmeticTask
}
