package com.example

import android.content.Context
import android.os.Looper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DateProvider
import com.example.data.HealthCheckRepository
import com.example.data.MedicationSlot
import com.example.ui.HealthCheckViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: HealthCheckRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor(Runnable::run)
            .setTransactionExecutor(Runnable::run)
            .build()
        repository = HealthCheckRepository(database.dailyRecordDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read updated app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("약·물 체크", appName)
    }

    @Test
    fun `toggle medications and water cups persist per date`() = runTest {
        val today = "2026-11-17"

        val initial = repository.observeRecord(today).first()
        assertFalse(initial.morningTaken)
        assertFalse(initial.lunchTaken)
        assertFalse(initial.eveningTaken)
        assertEquals(0, initial.waterCups)

        repository.toggleMedication(today, MedicationSlot.MORNING)
        repository.toggleMedication(today, MedicationSlot.EVENING)
        repository.incrementWater(today)
        repository.incrementWater(today)

        val updated = repository.observeRecord(today).first()
        assertTrue(updated.morningTaken)
        assertFalse(updated.lunchTaken)
        assertTrue(updated.eveningTaken)
        assertEquals(2, updated.waterCups)

        repository.decrementWater(today)
        val decremented = repository.observeRecord(today).first()
        assertEquals(1, decremented.waterCups)
    }

    @Test
    fun `clearStaleRecords removes yesterday records on new day`() = runTest {
        val yesterday = "2026-11-16"
        val today = "2026-11-17"

        repository.toggleMedication(yesterday, MedicationSlot.MORNING)
        repository.incrementWater(yesterday)
        assertTrue(database.dailyRecordDao().getRecord(yesterday)?.morningTaken == true)

        repository.clearStaleRecords(today)
        assertNull(database.dailyRecordDao().getRecord(yesterday))
    }

    @Test
    fun `midnight watcher automatically resets medications and water cups after midnight`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            try {
                val fakeDateProvider = object : DateProvider {
                    var dateKey = "2026-11-17"
                    var dayLabel = "11월 17일 화요일"
                    var untilMidnightMs = 5_000L

                    override fun currentDateKey(): String = dateKey
                    override fun formatKoreanYear(): String = "2026년"
                    override fun formatKoreanMonthDayWeekday(): String = dayLabel
                    override fun millisUntilNextMidnight(): Long = untilMidnightMs
                }

                val viewModel = HealthCheckViewModel(
                    repository = repository,
                    dateProvider = fakeDateProvider,
                    enableMidnightWatcher = true
                )

                val collectorJob = launch(testDispatcher) {
                    viewModel.uiState.collect()
                }
                runCurrent()
                shadowOf(Looper.getMainLooper()).idle()

                viewModel.toggleMedication(MedicationSlot.MORNING)
                viewModel.toggleMedication(MedicationSlot.LUNCH)
                viewModel.addWaterCup()
                viewModel.addWaterCup()
                viewModel.addWaterCup()

                repeat(5) {
                    runCurrent()
                    Thread.sleep(30)
                    shadowOf(Looper.getMainLooper()).idle()
                    runCurrent()
                }

                assertTrue(viewModel.uiState.value.morningTaken)
                assertTrue(viewModel.uiState.value.lunchTaken)
                assertEquals(3, viewModel.uiState.value.waterCups)

                // Simulate clock crossing midnight while app remains open
                fakeDateProvider.dateKey = "2026-11-18"
                fakeDateProvider.dayLabel = "11월 18일 수요일"
                fakeDateProvider.untilMidnightMs = 86_400_000L

                advanceTimeBy(5_200L)
                repeat(5) {
                    runCurrent()
                    Thread.sleep(30)
                    shadowOf(Looper.getMainLooper()).idle()
                    runCurrent()
                }

                val afterMidnight = viewModel.uiState.value
                assertEquals("2026-11-18", afterMidnight.dateKey)
                assertEquals("11월 18일 수요일", afterMidnight.monthDayWeekdayLabel)
                assertFalse(afterMidnight.morningTaken)
                assertFalse(afterMidnight.lunchTaken)
                assertFalse(afterMidnight.eveningTaken)
                assertEquals(0, afterMidnight.waterCups)
                assertNull(database.dailyRecordDao().getRecord("2026-11-17"))

                viewModel.stopMidnightWatcher()
                collectorJob.cancel()
            } finally {
                Dispatchers.resetMain()
            }
        }
}
