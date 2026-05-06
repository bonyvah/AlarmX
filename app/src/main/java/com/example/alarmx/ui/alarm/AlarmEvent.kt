package com.example.alarmx.ui.alarm

sealed interface AlarmEvent {
    data class NavigateToDismiss(val alarmId: Long) : AlarmEvent
    data object NavigateBackToList : AlarmEvent
    data object WrongAnswerFeedback : AlarmEvent
    data object CorrectAnswerFeedback : AlarmEvent
    data class Snoozed(val nextTriggerEpochMillis: Long) : AlarmEvent
    data class ShowError(val message: String) : AlarmEvent
}
