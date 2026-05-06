package com.example.alarmx.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room row for an alarm.
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "trigger_at_epoch_millis")
    val triggerAtEpochMillis: Long,

    val label: String,

    val enabled: Boolean,

    val difficulty: String,

    @ColumnInfo(name = "snooze_minutes")
    val snoozeMinutes: Int,

    val sound: String,

    @ColumnInfo(name = "repeat_days_bitmask")
    val repeatDaysBitmask: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
