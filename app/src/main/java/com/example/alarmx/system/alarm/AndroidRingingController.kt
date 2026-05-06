package com.example.alarmx.system.alarm

import android.content.Context
import android.content.Intent
import com.example.alarmx.domain.ringing.RingingController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidRingingController @Inject constructor(
    @ApplicationContext private val context: Context,
) : RingingController {

    override fun stop() {
        val intent = Intent(context, AlarmRingtoneService::class.java).apply {
            action = AlarmIntents.ACTION_STOP_RINGING
        }
        try {
            context.startService(intent)
        } catch (_: IllegalStateException) {
        }
    }
}
