package com.example.alarmx.system.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.alarmx.MainActivity
import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.scheduler.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManagerScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmScheduler {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: Alarm) {
        if (!alarm.enabled) {
            cancel(alarm.id)
            return
        }

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = AlarmIntents.ACTION_ALARM_FIRED
            putExtra(AlarmIntents.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmIntents.EXTRA_ALARM_SOUND, alarm.sound)
            data = android.net.Uri.parse("alarmx://alarm/${alarm.id}")
        }
        val operation = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            pendingIntentFlags(mutable = false)
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            action = AlarmIntents.ACTION_SHOW_DISMISS
            putExtra(AlarmIntents.EXTRA_ALARM_ID, alarm.id)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val showPi = PendingIntent.getActivity(
            context,
            alarm.id.toInt(),
            showIntent,
            pendingIntentFlags(mutable = false)
        )

        try {
            val info = AlarmManager.AlarmClockInfo(alarm.triggerAtEpochMillis, showPi)
            alarmManager.setAlarmClock(info, operation)
        } catch (e: SecurityException) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarm.triggerAtEpochMillis,
                operation
            )
        }
    }

    override fun cancel(alarmId: Long) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = AlarmIntents.ACTION_ALARM_FIRED
            data = android.net.Uri.parse("alarmx://alarm/$alarmId")
        }
        val operation = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            pendingIntentFlags(mutable = false)
        )
        alarmManager.cancel(operation)
        operation.cancel()
    }

    private fun pendingIntentFlags(mutable: Boolean): Int {
        val base = PendingIntent.FLAG_UPDATE_CURRENT
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            base or if (mutable) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE
        } else {
            base
        }
    }
}
