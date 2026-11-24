package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_records")
data class DailyRecordEntity(
    @PrimaryKey
    val dateKey: String,
    val morningTaken: Boolean = false,
    val lunchTaken: Boolean = false,
    val eveningTaken: Boolean = false,
    val waterCups: Int = 0
)
