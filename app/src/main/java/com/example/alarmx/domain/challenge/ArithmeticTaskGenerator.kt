package com.example.alarmx.domain.challenge

import com.example.alarmx.domain.model.ArithmeticOperation
import com.example.alarmx.domain.model.ArithmeticTask
import com.example.alarmx.domain.model.DifficultyLevel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Default [ChallengeProvider] producing arithmetic tasks.
 */
@Singleton
class ArithmeticTaskGenerator @Inject constructor(
    private val random: Random,
) : ChallengeProvider {

    override fun generate(difficulty: DifficultyLevel): ArithmeticTask {
        val operation = pickOperation(difficulty)
        val (left, right) = pickOperands(difficulty, operation)
        return ArithmeticTask(left = left, right = right, operation = operation)
    }

    private fun pickOperation(difficulty: DifficultyLevel): ArithmeticOperation =
        when (difficulty) {
            DifficultyLevel.EASY -> ArithmeticOperation.ADD
            DifficultyLevel.MEDIUM -> MEDIUM_OPERATIONS.random()
            DifficultyLevel.HARD -> HARD_OPERATIONS.random()
        }

    private fun pickOperands(
        difficulty: DifficultyLevel,
        operation: ArithmeticOperation,
    ): Pair<Int, Int> {
        val range = when (operation) {
            ArithmeticOperation.MULTIPLY -> MULTIPLY_RANGE
            else -> when (difficulty) {
                DifficultyLevel.EASY -> EASY_RANGE
                DifficultyLevel.MEDIUM -> MEDIUM_RANGE
                DifficultyLevel.HARD -> HARD_RANGE
            }
        }

        val a = randomInRange(range)
        val b = randomInRange(range)

        return when (operation) {
            ArithmeticOperation.SUBTRACT -> if (a >= b) a to b else b to a
            else -> a to b
        }
    }

    private fun randomInRange(range: IntRange): Int =
        random.nextInt(range.first, range.last + 1)

    private fun <T> Array<T>.random(): T = this[random.nextInt(size)]

    private companion object {
        val EASY_RANGE = 1..9
        val MEDIUM_RANGE = 10..49
        val HARD_RANGE = 20..99
        val MULTIPLY_RANGE = 2..12

        val MEDIUM_OPERATIONS = arrayOf(
            ArithmeticOperation.ADD,
            ArithmeticOperation.SUBTRACT,
        )
        val HARD_OPERATIONS = arrayOf(
            ArithmeticOperation.ADD,
            ArithmeticOperation.SUBTRACT,
            ArithmeticOperation.MULTIPLY,
        )
    }
}
