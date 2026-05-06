package com.example.alarmx.ui.alarm

import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.model.ArithmeticTask
import com.example.alarmx.domain.model.UserPreferences

data class AlarmUiState(
    val alarms: List<Alarm> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val activeAlarmId: Long? = null,
    val activeChallenge: ArithmeticTask? = null,
    val fullScreenVisible: Boolean = false,
    val incorrectAttempts: Int = 0,
    val loading: Boolean = false,
    val error: String? = null,
)
