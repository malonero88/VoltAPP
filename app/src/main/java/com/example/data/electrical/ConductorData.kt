package com.example.data.electrical

/**
 * Tipo de cable e instalación según normas IRAM y AEA 90364.
 */
enum class CableType(
    val label: String,
    val shortLabel: String,
    val standardNorm: String,
    val description: String
) {
    UNIPOLAR(
        label = "Unipolar en Cañería",
        shortLabel = "Unipolar",
        standardNorm = "IRAM 2183 / NM 247-3",
        description = "Conductores aislados en PVC instalados en cañerías embutidas o a la vista."
    ),
    SUBTERRANEO(
        label = "Subterráneo / Sintenax",
        shortLabel = "Subterráneo",
        standardNorm = "IRAM 2178",
        description = "Cable multipolar con vaina de protección y doble aislación (enterrado o al aire)."
    )
}

/**
 * Datos normalizados para conductores según normas IRAM 2183 / 247-3 / 2178 y AEA 90364 (Argentina).
 */
data class ConductorSection(
    val sectionMm2: Double,
    val label: String,
    val admissibleCurrentMonoA: Double,        // Corriente admisible 2 conductores en cañería (IRAM 2183)
    val admissibleCurrentTriA: Double,         // Corriente admisible 3 conductores en cañería (IRAM 2183)
    val admissibleSubterraneoMonoA: Double,    // Corriente admisible Subterráneo Bipolar 2x (IRAM 2178)
    val admissibleSubterraneoTriA: Double,     // Corriente admisible Subterráneo Tetrapolar 4x (IRAM 2178)
    val kMono: Double,                        // Coeficiente k de caída V/(A·km) a cos phi = 0.85
    val kTri: Double,                         // Coeficiente k de caída V/(A·km) a cos phi = 0.85
    val resistanceOhmPerKm: Double            // Resistencia eléctrica a 70°C (Ohm/km)
)

object ConductorTable {
    // Resistividad del Cobre a 20°C y a 70°C (régimen de trabajo según AEA)
    const val RHO_COPPER_20 = 0.0178 // Ohm * mm2 / m
    const val RHO_COPPER_70 = 0.0210 // Ohm * mm2 / m
    const val RHO_ALUMINUM_20 = 0.0282 // Ohm * mm2 / m
    const val RHO_ALUMINUM_70 = 0.0330 // Ohm * mm2 / m

    /**
     * Lista de secciones estándar de conductores eléctricos en Argentina (en mm²).
     * Incluye coeficientes de caída k [V / (A · km)] a cos phi = 0.85 e intensidades admisibles IRAM 2183 e IRAM 2178.
     */
    val standardSections = listOf(
        ConductorSection(1.0, "1.0 mm²", 11.0, 9.6, 18.0, 15.0, 36.10, 31.26, 21.0),
        ConductorSection(1.5, "1.5 mm²", 15.0, 13.0, 24.0, 20.0, 24.08, 20.85, 14.0),
        ConductorSection(2.5, "2.5 mm²", 21.0, 18.0, 33.0, 27.0, 14.45, 12.51, 8.40),
        ConductorSection(4.0, "4.0 mm²", 28.0, 24.0, 43.0, 36.0, 9.03, 7.82, 5.25),
        ConductorSection(6.0, "6.0 mm²", 36.0, 31.0, 54.0, 45.0, 6.02, 5.21, 3.50),
        ConductorSection(10.0, "10.0 mm²", 50.0, 43.0, 73.0, 62.0, 3.61, 3.13, 2.10),
        ConductorSection(16.0, "16.0 mm²", 66.0, 59.0, 96.0, 81.0, 2.27, 1.97, 1.31),
        ConductorSection(25.0, "25.0 mm²", 88.0, 79.0, 124.0, 106.0, 1.46, 1.26, 0.84),
        ConductorSection(35.0, "35.0 mm²", 109.0, 97.0, 150.0, 128.0, 1.05, 0.91, 0.60),
        ConductorSection(50.0, "50.0 mm²", 131.0, 117.0, 179.0, 153.0, 0.77, 0.67, 0.42),
        ConductorSection(70.0, "70.0 mm²", 167.0, 149.0, 221.0, 191.0, 0.55, 0.48, 0.30),
        ConductorSection(95.0, "95.0 mm²", 202.0, 180.0, 265.0, 231.0, 0.41, 0.36, 0.22),
        ConductorSection(120.0, "120.0 mm²", 234.0, 208.0, 301.0, 264.0, 0.33, 0.29, 0.175),
        ConductorSection(150.0, "150.0 mm²", 267.0, 238.0, 341.0, 301.0, 0.27, 0.23, 0.140),
        ConductorSection(185.0, "185.0 mm²", 300.0, 268.0, 384.0, 341.0, 0.22, 0.19, 0.113),
        ConductorSection(240.0, "240.0 mm²", 351.0, 313.0, 446.0, 399.0, 0.17, 0.15, 0.087)
    )

    fun getSection(sectionMm2: Double): ConductorSection {
        return standardSections.find { it.sectionMm2 == sectionMm2 }
            ?: standardSections[2] // Default 2.5 mm²
    }

    fun getAdmissibleCurrent(
        sectionMm2: Double,
        cableType: CableType,
        systemType: SystemType
    ): Double {
        val sec = getSection(sectionMm2)
        return when (cableType) {
            CableType.UNIPOLAR -> {
                if (systemType == SystemType.MONOFASICO_220) sec.admissibleCurrentMonoA else sec.admissibleCurrentTriA
            }
            CableType.SUBTERRANEO -> {
                if (systemType == SystemType.MONOFASICO_220) sec.admissibleSubterraneoMonoA else sec.admissibleSubterraneoTriA
            }
        }
    }
}
