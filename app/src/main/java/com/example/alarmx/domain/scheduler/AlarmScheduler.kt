package com.example.alarmx.domain.scheduler

import com.example.alarmx.domain.model.Alarm

/**
 * Domain-level contract for arming/cancelling system alarms.
 */
interface AlarmScheduler {

    /**
     * Arm [alarm] in the OS so that it fires at [Alarm.triggerAtEpochMillis].
     * If [alarm] is already scheduled, the implementation replaces the
     * existing schedule.
     */
    fun schedule(alarm: Alarm)

    /** Cancel any pending OS-level alarm with [alarmId], if one exists. */
    fun cancel(alarmId: Long)
}
