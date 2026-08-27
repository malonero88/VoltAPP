package com.example.data.electrical

import kotlin.math.sqrt

enum class SystemType(val nominalVoltage: Double, val label: String) {
    MONOFASICO_220(220.0, "Monofásico 220V"),
    TRIFASICO_380(380.0, "Trifásico 380V")
}

enum class ConductorMaterial(val label: String, val rho70: Double) {
    COPPER("Cobre (Cu)", ConductorTable.RHO_COPPER_70),
    ALUMINUM("Aluminio (Al)", ConductorTable.RHO_ALUMINUM_70)
}

enum class ComplianceStatus(val label: String, val description: String) {
    OPTIMAL("Óptimo (≤ 3%)", "Cumple norma AEA 90364 para iluminación y tomacorrientes."),
    ADMISSIBLE("Admisible (3% a 5%)", "Apto para fuerza motriz / motores (excede 3% de iluminación)."),
    NON_COMPLIANT("No Cumple (> 5%)", "Caída de tensión excesiva. Supera el límite reglamentario del 5%."),
    OVERLOAD("Sobrecarga Térmica", "La corriente calculada supera la intensidad máxima admisible del conductor.")
}

data class CalculationResult(
    val sectionMm2: Double,
    val powerWatts: Double,
    val distanceMeters: Double,
    val systemType: SystemType,
    val cosPhi: Double,
    val material: ConductorMaterial,
    val currentAmps: Double,
    val coefficientK: Double, // V/(A·km)
    val deltaVolts: Double,
    val deltaVoltsPercent: Double,
    val finalVoltage: Double,
    val maxAdmissibleCurrentA: Double,
    val isThermallyOverloaded: Boolean,
    val complianceStatus: ComplianceStatus,
    val recommendedSectionMm2: Double? = null
)

object VoltageDropCalculator {

    /**
     * Calcula la corriente I (A), caída de tensión ΔV (V), caída porcentual ΔV (%)
     * y verifica la normativa AEA 90364.
     */
    fun calculate(
        sectionMm2: Double,
        powerWatts: Double,
        distanceMeters: Double,
        systemType: SystemType = SystemType.MONOFASICO_220,
        cosPhi: Double = 0.85,
        material: ConductorMaterial = ConductorMaterial.COPPER
    ): CalculationResult {
        if (powerWatts <= 0 || distanceMeters <= 0 || sectionMm2 <= 0) {
            return CalculationResult(
                sectionMm2 = sectionMm2,
                powerWatts = powerWatts,
                distanceMeters = distanceMeters,
                systemType = systemType,
                cosPhi = cosPhi,
                material = material,
                currentAmps = 0.0,
                coefficientK = 0.0,
                deltaVolts = 0.0,
                deltaVoltsPercent = 0.0,
                finalVoltage = systemType.nominalVoltage,
                maxAdmissibleCurrentA = 0.0,
                isThermallyOverloaded = false,
                complianceStatus = ComplianceStatus.OPTIMAL
            )
        }

        val voltage = systemType.nominalVoltage
        val validCosPhi = cosPhi.coerceIn(0.5, 1.0)

        // 1. Corriente en Amperes
        val currentAmps = when (systemType) {
            SystemType.MONOFASICO_220 -> powerWatts / (voltage * validCosPhi)
            SystemType.TRIFASICO_380 -> powerWatts / (sqrt(3.0) * voltage * validCosPhi)
        }

        // 2. Obtener datos del conductor
        val conductor = ConductorTable.getSection(sectionMm2)
        val maxAdmissibleCurrentA = when (systemType) {
            SystemType.MONOFASICO_220 -> conductor.admissibleCurrentMonoA
            SystemType.TRIFASICO_380 -> conductor.admissibleCurrentTriA
        }

        // 3. Coeficiente k de caída de tensión en V / (A · km)
        // Para cobre a cos phi = 0.85 usamos la tabla normalizada; si cambia cos phi o material, calculamos el k equivalente
        val baseK = when (systemType) {
            SystemType.MONOFASICO_220 -> {
                // k = 2 * rho * 1000 * cosPhi / S
                (2.0 * material.rho70 * 1000.0 * validCosPhi) / sectionMm2
            }
            SystemType.TRIFASICO_380 -> {
                // k = sqrt(3) * rho * 1000 * cosPhi / S
                (sqrt(3.0) * material.rho70 * 1000.0 * validCosPhi) / sectionMm2
            }
        }

        // 4. Caída de tensión ΔV = k * I * (L / 1000)
        val deltaVolts = baseK * currentAmps * (distanceMeters / 1000.0)

        // 5. Caída Porcentual ΔV% = (ΔV / V) * 100
        val deltaVoltsPercent = (deltaVolts / voltage) * 100.0
        val finalVoltage = (voltage - deltaVolts).coerceAtLeast(0.0)

        val isThermallyOverloaded = currentAmps > maxAdmissibleCurrentA

        // 6. Evaluación de cumplimiento AEA 90364
        val complianceStatus = when {
            isThermallyOverloaded -> ComplianceStatus.OVERLOAD
            deltaVoltsPercent <= 3.0 -> ComplianceStatus.OPTIMAL
            deltaVoltsPercent <= 5.0 -> ComplianceStatus.ADMISSIBLE
            else -> ComplianceStatus.NON_COMPLIANT
        }

        // 7. Calcular sección recomendada si excede 3% o hay sobrecarga
        var recommendedSection: Double? = null
        if (deltaVoltsPercent > 3.0 || isThermallyOverloaded) {
            for (sec in ConductorTable.standardSections) {
                val secAdm = if (systemType == SystemType.MONOFASICO_220) sec.admissibleCurrentMonoA else sec.admissibleCurrentTriA
                val secK = if (systemType == SystemType.MONOFASICO_220) {
                    (2.0 * material.rho70 * 1000.0 * validCosPhi) / sec.sectionMm2
                } else {
                    (sqrt(3.0) * material.rho70 * 1000.0 * validCosPhi) / sec.sectionMm2
                }
                val secDeltaV = secK * currentAmps * (distanceMeters / 1000.0)
                val secDeltaVPercent = (secDeltaV / voltage) * 100.0

                if (secDeltaVPercent <= 3.0 && currentAmps <= secAdm) {
                    recommendedSection = sec.sectionMm2
                    break
                }
            }
        }

        return CalculationResult(
            sectionMm2 = sectionMm2,
            powerWatts = powerWatts,
            distanceMeters = distanceMeters,
            systemType = systemType,
            cosPhi = validCosPhi,
            material = material,
            currentAmps = currentAmps,
            coefficientK = baseK,
            deltaVolts = deltaVolts,
            deltaVoltsPercent = deltaVoltsPercent,
            finalVoltage = finalVoltage,
            maxAdmissibleCurrentA = maxAdmissibleCurrentA,
            isThermallyOverloaded = isThermallyOverloaded,
            complianceStatus = complianceStatus,
            recommendedSectionMm2 = recommendedSection
        )
    }
}
