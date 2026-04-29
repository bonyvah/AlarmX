package com.example.alarmx.domain.repository

import com.example.alarmx.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun observeAlarms(): Flow<List<Alarm>>

    suspend fun list(): List<Alarm>

    suspend fun findById(id: Long): Alarm?

    suspend fun save(alarm: Alarm)

    suspend fun dismiss(alarmId: Long)

    suspend fun delete(alarmId: Long)

    suspend fun setEnabled(alarmId: Long, enabled: Boolean)
}