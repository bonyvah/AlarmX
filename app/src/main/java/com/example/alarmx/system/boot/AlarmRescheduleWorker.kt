package com.example.alarmx.system.boot

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.alarmx.domain.repository.AlarmRepository
import com.example.alarmx.domain.scheduler.AlarmScheduler
import com.example.alarmx.domain.util.AlarmScheduling
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker that re-arms every enabled alarm.
 */
@HiltWorker
class AlarmRescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val alarms = alarmRepository.list()
            val now = System.currentTimeMillis()
            for (alarm in alarms) {
                if (!alarm.enabled) continue
                if (alarm.triggerAtEpochMillis > now) {
                    alarmScheduler.schedule(alarm)
                    continue
                }
                if (alarm.repeatDays.isEmpty()) {
                    alarmRepository.setEnabled(alarm.id, enabled = false)
                    continue
                }
                val next = AlarmScheduling.advanceRepeating(alarm) ?: continue
                alarmRepository.save(next)
            }
            Result.success()
        } catch (t: Throwable) {
            Log.e("AlarmRescheduleWorker", "Reschedule worker failed", t)
            Result.retry()
        }
    }
}
