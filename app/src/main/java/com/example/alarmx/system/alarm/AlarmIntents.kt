package com.example.alarmx.system.alarm

/**
 * String constants shared between system components.
 */
object AlarmIntents {
    const val ACTION_ALARM_FIRED = "com.example.alarmx.action.ALARM_FIRED"
    const val ACTION_STOP_RINGING = "com.example.alarmx.action.STOP_RINGING"
    const val ACTION_SHOW_DISMISS = "com.example.alarmx.action.SHOW_DISMISS"

    const val EXTRA_ALARM_ID = "com.example.alarmx.extra.ALARM_ID"
    const val EXTRA_ALARM_SOUND = "com.example.alarmx.extra.ALARM_SOUND"

    const val CHANNEL_ID_ALARM = "alarmx.alarm"
    const val NOTIFICATION_ID_ALARM = 0xA1A2
}
