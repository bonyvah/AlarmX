package com.example.alarmx.domain.model

enum class ArithmeticOperation {
    ADD,
    SUBTRACT,
    MULTIPLY
}

data class ArithmeticTask(
    val left: Int,
    val right: Int,
    val operation: ArithmeticOperation,
) {
    val prompt: String
        get() = when (operation) {
            ArithmeticOperation.ADD -> "$left + $right"
            ArithmeticOperation.SUBTRACT -> "$left - $right"
            ArithmeticOperation.MULTIPLY -> "$left × $right"
        }

    fun answer(): Int = when (operation) {
        ArithmeticOperation.ADD -> left + right
        ArithmeticOperation.SUBTRACT -> left - right
        ArithmeticOperation.MULTIPLY -> left * right
    }
}

data class AlarmTrigger(
    val alarmId: Long,
    val challenge: ArithmeticTask,
)