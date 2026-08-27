package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.CalculationEntity
import com.example.data.electrical.CableType
import com.example.data.electrical.CalculationResult
import com.example.data.electrical.ComplianceStatus
import com.example.data.electrical.ConductorMaterial
import com.example.data.electrical.SystemType
import com.example.data.electrical.VoltageDropCalculator
import com.example.data.repository.CalculationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val sectionMm2: Double = 2.5,
    val powerInput: String = "2200",
    val distanceInput: String = "25",
    val systemType: SystemType = SystemType.MONOFASICO_220,
    val cableType: CableType = CableType.UNIPOLAR,
    val cosPhiInput: String = "0.85",
    val material: ConductorMaterial = ConductorMaterial.COPPER,
    val powerUnit: String = "W", // "W", "kW", "HP"
    val result: CalculationResult? = null,
    val isCalculationSaved: Boolean = false,
    val lastSavedId: Long? = null
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("voltcalc_settings", Context.MODE_PRIVATE)

    private val repository: CalculationRepository

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("pref_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    val historyList: StateFlow<List<CalculationEntity>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CalculationRepository(db.calculationDao())
        historyList = repository.allCalculations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Realizar cálculo inicial con valores por defecto
        recalculate()
    }

    fun toggleDarkMode() {
        val nextMode = !_isDarkMode.value
        _isDarkMode.value = nextMode
        prefs.edit().putBoolean("pref_dark_mode", nextMode).apply()
        viewModelScope.launch {
            _toastEvent.emit(if (nextMode) "Modo oscuro activado" else "Modo claro activado")
        }
    }

    fun setDarkMode(enabled: Boolean) {
        if (_isDarkMode.value != enabled) {
            _isDarkMode.value = enabled
            prefs.edit().putBoolean("pref_dark_mode", enabled).apply()
        }
    }

    fun onSectionChanged(section: Double) {
        _uiState.update { it.copy(sectionMm2 = section, isCalculationSaved = false) }
        recalculate()
    }

    fun onPowerChanged(power: String) {
        val sanitized = power.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        _uiState.update { it.copy(powerInput = sanitized, isCalculationSaved = false) }
        recalculate()
    }

    fun onDistanceChanged(distance: String) {
        val sanitized = distance.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        _uiState.update { it.copy(distanceInput = sanitized, isCalculationSaved = false) }
        recalculate()
    }

    fun onSystemTypeChanged(systemType: SystemType) {
        _uiState.update { it.copy(systemType = systemType, isCalculationSaved = false) }
        recalculate()
    }

    fun onCableTypeChanged(cableType: CableType) {
        _uiState.update { it.copy(cableType = cableType, isCalculationSaved = false) }
        recalculate()
    }

    fun onCosPhiChanged(cosPhi: String) {
        val sanitized = cosPhi.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        _uiState.update { it.copy(cosPhiInput = sanitized, isCalculationSaved = false) }
        recalculate()
    }

    fun onMaterialChanged(material: ConductorMaterial) {
        _uiState.update { it.copy(material = material, isCalculationSaved = false) }
        recalculate()
    }

    fun onPowerUnitChanged(unit: String) {
        _uiState.update { it.copy(powerUnit = unit, isCalculationSaved = false) }
        recalculate()
    }

    fun applySectionQuick(sectionMm2: Double) {
        onSectionChanged(sectionMm2)
    }

    fun addDistance(deltaMeters: Double) {
        val current = _uiState.value.distanceInput.toDoubleOrNull() ?: 0.0
        val newDistance = (current + deltaMeters).coerceAtLeast(1.0)
        onDistanceChanged(if (newDistance % 1.0 == 0.0) newDistance.toInt().toString() else "%.1f".format(newDistance))
    }

    fun addPower(deltaWatts: Double) {
        val current = _uiState.value.powerInput.toDoubleOrNull() ?: 0.0
        val newPower = (current + deltaWatts).coerceAtLeast(100.0)
        onPowerChanged(if (newPower % 1.0 == 0.0) newPower.toInt().toString() else "%.1f".format(newPower))
    }

    private fun recalculate() {
        val state = _uiState.value
        val rawPower = state.powerInput.toDoubleOrNull() ?: 0.0
        val powerInWatts = when (state.powerUnit) {
            "kW" -> rawPower * 1000.0
            "HP" -> rawPower * 746.0
            else -> rawPower
        }

        val distance = state.distanceInput.toDoubleOrNull() ?: 0.0
        val cosPhi = state.cosPhiInput.toDoubleOrNull() ?: 0.85

        val calculationResult = VoltageDropCalculator.calculate(
            sectionMm2 = state.sectionMm2,
            powerWatts = powerInWatts,
            distanceMeters = distance,
            systemType = state.systemType,
            cableType = state.cableType,
            cosPhi = cosPhi,
            material = state.material
        )

        _uiState.update { it.copy(result = calculationResult) }
    }

    fun saveCalculation() {
        val state = _uiState.value
        val res = state.result ?: return

        if (res.powerWatts <= 0 || res.distanceMeters <= 0) {
            viewModelScope.launch {
                _toastEvent.emit("Ingrese valores válidos de potencia y distancia")
            }
            return
        }

        viewModelScope.launch {
            val entity = CalculationEntity(
                sectionMm2 = res.sectionMm2,
                powerWatts = res.powerWatts,
                distanceMeters = res.distanceMeters,
                isThreePhase = res.systemType == SystemType.TRIFASICO_380,
                cableType = res.cableType.name,
                voltage = res.systemType.nominalVoltage,
                cosPhi = res.cosPhi,
                material = res.material.label,
                currentAmps = res.currentAmps,
                coefficientK = res.coefficientK,
                deltaVolts = res.deltaVolts,
                deltaVoltsPercent = res.deltaVoltsPercent,
                status = res.complianceStatus.name,
                note = "${if (res.systemType == SystemType.MONOFASICO_220) "Mono 220V" else "Tri 380V"} - ${res.sectionMm2} mm² (${res.cableType.shortLabel})"
            )
            val id = repository.insert(entity)
            _uiState.update { it.copy(isCalculationSaved = true, lastSavedId = id) }
            _toastEvent.emit("¡Cálculo guardado en el historial!")
        }
    }

    fun loadFromHistory(item: CalculationEntity) {
        val systemType = if (item.isThreePhase) SystemType.TRIFASICO_380 else SystemType.MONOFASICO_220
        val material = if (item.material.contains("Aluminio", ignoreCase = true)) ConductorMaterial.ALUMINUM else ConductorMaterial.COPPER
        val cableType = try {
            CableType.valueOf(item.cableType)
        } catch (_: Exception) {
            CableType.UNIPOLAR
        }

        _uiState.update {
            it.copy(
                sectionMm2 = item.sectionMm2,
                powerInput = if (item.powerWatts % 1.0 == 0.0) item.powerWatts.toInt().toString() else "%.1f".format(item.powerWatts),
                distanceInput = if (item.distanceMeters % 1.0 == 0.0) item.distanceMeters.toInt().toString() else "%.1f".format(item.distanceMeters),
                systemType = systemType,
                cableType = cableType,
                cosPhiInput = "%.2f".format(item.cosPhi).replace(',', '.'),
                material = material,
                powerUnit = "W",
                isCalculationSaved = true,
                lastSavedId = item.id
            )
        }
        recalculate()
        viewModelScope.launch {
            _toastEvent.emit("Cálculo cargado: ${item.sectionMm2} mm² (${cableType.shortLabel}) - ${item.distanceMeters} m")
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            _toastEvent.emit("Registro eliminado del historial")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            _toastEvent.emit("Historial vaciado")
        }
    }
}
