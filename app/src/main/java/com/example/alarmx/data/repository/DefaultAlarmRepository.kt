package com.example.alarmx.data.repository

import com.example.alarmx.data.db.AlarmDao
import com.example.alarmx.data.db.AlarmMapper.toDomain
import com.example.alarmx.data.db.AlarmMapper.toEntity
import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.repository.AlarmRepository
import com.example.alarmx.domain.scheduler.AlarmScheduler
import com.example.alarmx.domain.util.AlarmScheduling
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Production [AlarmRepository] implementation.
 */
@Singleton
class DefaultAlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao,
    private val scheduler: AlarmScheduler,
) : AlarmRepository {

    override fun observeAlarms(): Flow<List<Alarm>> =
        alarmDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun list(): List<Alarm> =
        alarmDao.getAll().map { it.toDomain() }

    override suspend fun findById(id: Long): Alarm? =
        alarmDao.findById(id)?.toDomain()

    override suspend fun save(alarm: Alarm) {
        val existing = alarmDao.findById(alarm.id)
        alarmDao.upsert(alarm.toEntity(existing = existing))
        if (alarm.enabled) {
            scheduler.schedule(alarm)
        } else {
            scheduler.cancel(alarm.id)
        }
    }

    override suspend fun dismiss(alarmId: Long) {
        val existing = alarmDao.findById(alarmId)?.toDomain()
        if (existing != null && existing.repeatDays.isNotEmpty()) {
            val advanced = AlarmScheduling.advanceRepeating(existing)
            if (advanced != null) {
                save(advanced)
                return
            }
        }
        alarmDao.setEnabled(alarmId, enabled = false, updatedAt = System.currentTimeMillis())
        scheduler.cancel(alarmId)
    }

    override suspend fun delete(alarmId: Long) {
        alarmDao.deleteById(alarmId)
        scheduler.cancel(alarmId)
    }

    override suspend fun setEnabled(alarmId: Long, enabled: Boolean) {
        alarmDao.setEnabled(alarmId, enabled, updatedAt = System.currentTimeMillis())
        if (enabled) {
            val alarm = alarmDao.findById(alarmId)?.toDomain() ?: return
            scheduler.schedule(alarm)
        } else {
            scheduler.cancel(alarmId)
        }
    }
}
