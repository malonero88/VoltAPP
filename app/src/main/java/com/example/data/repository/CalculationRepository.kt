package com.example.data.repository

import com.example.data.db.CalculationDao
import com.example.data.db.CalculationEntity
import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    val allCalculations: Flow<List<CalculationEntity>> = dao.getAllCalculations()

    suspend fun insert(calculation: CalculationEntity): Long {
        return dao.insertCalculation(calculation)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteCalculationById(id)
    }

    suspend fun clearAll() {
        dao.clearHistory()
    }
}
