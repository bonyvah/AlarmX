package com.example.alarmx.domain.ringing

/**
 * Domain-level contract for stopping a currently-ringing alarm.
 */
interface RingingController {
    /**
     * Stop any ringtone playback / WakeLock / foreground service tied to a
     * currently-firing alarm. No-op if nothing is ringing.
     */
    fun stop()
}
