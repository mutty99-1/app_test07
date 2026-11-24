package com.example.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DateProvider
import com.example.data.HealthCheckRepository
import com.example.data.MedicationSlot
import com.example.data.SystemDateProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HealthCheckUiState(
    val dateKey: String = "",
    val yearLabel: String = "",
    val monthDayWeekdayLabel: String = "",
    val morningTaken: Boolean = false,
    val lunchTaken: Boolean = false,
    val eveningTaken: Boolean = false,
    val waterCups: Int = 0,
    val waterGoal: Int = 8
) {
    val completedMedCount: Int
        get() = listOf(morningTaken, lunchTaken, eveningTaken).count { it }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HealthCheckViewModel(
    private val repository: HealthCheckRepository,
    private val dateProvider: DateProvider = SystemDateProvider(),
    enableMidnightWatcher: Boolean = true
) : ViewModel() {

    private val currentDateKeyFlow = MutableStateFlow(dateProvider.currentDateKey())
    private var midnightWatcherJob: Job? = null

    val uiState: StateFlow<HealthCheckUiState> = currentDateKeyFlow
        .flatMapLatest { dateKey ->
            repository.observeRecord(dateKey)
        }
        .combine(currentDateKeyFlow) { record, dateKey ->
            HealthCheckUiState(
                dateKey = dateKey,
                yearLabel = dateProvider.formatKoreanYear(),
                monthDayWeekdayLabel = dateProvider.formatKoreanMonthDayWeekday(),
                morningTaken = record.morningTaken,
                lunchTaken = record.lunchTaken,
                eveningTaken = record.eveningTaken,
                waterCups = record.waterCups,
                waterGoal = 8
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HealthCheckUiState(
                dateKey = dateProvider.currentDateKey(),
                yearLabel = dateProvider.formatKoreanYear(),
                monthDayWeekdayLabel = dateProvider.formatKoreanMonthDayWeekday()
            )
        )

    init {
        viewModelScope.launch {
            repository.clearStaleRecords(currentDateKeyFlow.value)
        }
        if (enableMidnightWatcher) {
            startMidnightWatcher()
        }
    }

    private fun startMidnightWatcher() {
        midnightWatcherJob?.cancel()
        midnightWatcherJob = viewModelScope.launch {
            while (isActive) {
                val waitMillis = dateProvider.millisUntilNextMidnight() + 100L
                delay(waitMillis)
                refreshDateIfNeeded()
            }
        }
    }

    internal fun stopMidnightWatcher() {
        midnightWatcherJob?.cancel()
        midnightWatcherJob = null
    }

    fun refreshDateIfNeeded() {
        val latestKey = dateProvider.currentDateKey()
        if (currentDateKeyFlow.value != latestKey) {
            currentDateKeyFlow.value = latestKey
            viewModelScope.launch {
                repository.clearStaleRecords(latestKey)
            }
        }
    }

    fun toggleMedication(slot: MedicationSlot) {
        viewModelScope.launch {
            val latestKey = syncAndGetCurrentDateKey()
            repository.toggleMedication(latestKey, slot)
        }
    }

    fun addWaterCup() {
        viewModelScope.launch {
            val latestKey = syncAndGetCurrentDateKey()
            repository.incrementWater(latestKey)
        }
    }

    fun removeWaterCup() {
        viewModelScope.launch {
            val latestKey = syncAndGetCurrentDateKey()
            repository.decrementWater(latestKey)
        }
    }

    private suspend fun syncAndGetCurrentDateKey(): String {
        val latestKey = dateProvider.currentDateKey()
        if (currentDateKeyFlow.value != latestKey) {
            currentDateKeyFlow.value = latestKey
            repository.clearStaleRecords(latestKey)
        }
        return latestKey
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val dao = AppDatabase.getInstance(application).dailyRecordDao()
            val repository = HealthCheckRepository(dao)
            return HealthCheckViewModel(repository = repository) as T
        }
    }
}
