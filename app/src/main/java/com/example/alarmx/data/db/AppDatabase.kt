package com.example.alarmx.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database root.
 */
@Database(
    entities = [AlarmEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {
        const val DATABASE_NAME = "alarmx.db"
    }
}
