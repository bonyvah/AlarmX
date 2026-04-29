package com.example.alarmx.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserPreferences(
    val difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val snoozeEnabled: Boolean = true,
    val defaultSnoozeMinutes: Int = 5,
    val sound: String = "default",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val maxSnoozeCount: Int = 3,
    val hapticsOnWrongAnswer: Boolean = true,
)