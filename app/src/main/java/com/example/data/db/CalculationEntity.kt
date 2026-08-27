package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val sectionMm2: Double,
    val powerWatts: Double,
    val distanceMeters: Double,
    val isThreePhase: Boolean,
    val voltage: Double,
    val cosPhi: Double,
    val material: String,
    val currentAmps: Double,
    val coefficientK: Double,
    val deltaVolts: Double,
    val deltaVoltsPercent: Double,
    val status: String,
    val note: String = ""
)
