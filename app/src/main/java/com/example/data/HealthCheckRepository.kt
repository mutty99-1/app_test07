package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class MedicationSlot {
    MORNING,
    LUNCH,
    EVENING
}

interface DateProvider {
    fun currentDateKey(): String
    fun formatKoreanYear(): String
    fun formatKoreanMonthDayWeekday(): String
    fun millisUntilNextMidnight(): Long
}

class SystemDateProvider : DateProvider {
    private val keyFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val yearFormatter = SimpleDateFormat("yyyy년", Locale.KOREA)
    private val dateFormatter = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA)

    override fun currentDateKey(): String = keyFormatter.format(Calendar.getInstance().time)

    override fun formatKoreanYear(): String = yearFormatter.format(Calendar.getInstance().time)

    override fun formatKoreanMonthDayWeekday(): String =
        dateFormatter.format(Calendar.getInstance().time)

    override fun millisUntilNextMidnight(): Long {
        val now = Calendar.getInstance()
        val nextMidnight = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return (nextMidnight.timeInMillis - now.timeInMillis).coerceAtLeast(1_000L)
    }
}

class HealthCheckRepository(
    private val dao: DailyRecordDao
) {
    private val writeMutex = Mutex()

    fun observeRecord(dateKey: String): Flow<DailyRecordEntity> {
        return dao.observeRecord(dateKey).map { existing ->
            existing ?: DailyRecordEntity(dateKey = dateKey)
        }
    }

    suspend fun clearStaleRecords(todayKey: String) {
        writeMutex.withLock {
            dao.deleteRecordsOtherThan(todayKey)
        }
    }

    suspend fun toggleMedication(dateKey: String, slot: MedicationSlot) {
        writeMutex.withLock {
            val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
            val updated = when (slot) {
                MedicationSlot.MORNING -> current.copy(morningTaken = !current.morningTaken)
                MedicationSlot.LUNCH -> current.copy(lunchTaken = !current.lunchTaken)
                MedicationSlot.EVENING -> current.copy(eveningTaken = !current.eveningTaken)
            }
            dao.upsertRecord(updated)
        }
    }

    suspend fun incrementWater(dateKey: String) {
        writeMutex.withLock {
            val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
            val updated = current.copy(waterCups = current.waterCups + 1)
            dao.upsertRecord(updated)
        }
    }

    suspend fun decrementWater(dateKey: String) {
        writeMutex.withLock {
            val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
            val updated = current.copy(waterCups = (current.waterCups - 1).coerceAtLeast(0))
            dao.upsertRecord(updated)
        }
    }
}
