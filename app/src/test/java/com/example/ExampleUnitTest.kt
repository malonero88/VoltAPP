package com.example

import com.example.data.electrical.ComplianceStatus
import com.example.data.electrical.ConductorMaterial
import com.example.data.electrical.ConductorTable
import com.example.data.electrical.SystemType
import com.example.data.electrical.VoltageDropCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testConductorTableHasSections() {
        val sections = ConductorTable.standardSections
        assertTrue(sections.isNotEmpty())
        assertEquals(1.0, sections.first().sectionMm2, 0.001)
        val sec25 = ConductorTable.getSection(2.5)
        assertEquals(2.5, sec25.sectionMm2, 0.001)
        assertEquals(21.0, sec25.admissibleCurrentMonoA, 0.001)
    }

    @Test
    fun testMonophasicCalculation() {
        // 2.5 mm², 2200 W, 25 m, Monofásico 220V, cos phi = 0.85
        val result = VoltageDropCalculator.calculate(
            sectionMm2 = 2.5,
            powerWatts = 2200.0,
            distanceMeters = 25.0,
            systemType = SystemType.MONOFASICO_220,
            cosPhi = 0.85,
            material = ConductorMaterial.COPPER
        )

        // I = 2200 / (220 * 0.85) = 11.76 A
        assertEquals(11.76, result.currentAmps, 0.1)
        assertTrue(result.deltaVolts > 0)
        assertTrue(result.deltaVoltsPercent > 0)
        // For 25m at 11.76A and 2.5mm2, deltaVolts is ~ 4.2V (< 3% of 220V = 6.6V)
        assertEquals(ComplianceStatus.OPTIMAL, result.complianceStatus)
    }

    @Test
    fun testTriphasicCalculation() {
        // 10 mm², 15000 W, 50 m, Trifásico 380V, cos phi = 0.85
        val result = VoltageDropCalculator.calculate(
            sectionMm2 = 10.0,
            powerWatts = 15000.0,
            distanceMeters = 50.0,
            systemType = SystemType.TRIFASICO_380,
            cosPhi = 0.85,
            material = ConductorMaterial.COPPER
        )

        // I = 15000 / (sqrt(3) * 380 * 0.85) = 26.82 A
        assertEquals(26.82, result.currentAmps, 0.2)
        assertTrue(result.deltaVoltsPercent <= 3.0)
    }

    @Test
    fun testExcessiveVoltageDropSuggestsLargerSection() {
        // 1.5 mm², 4000 W, 80 m, Monofásico 220V (High drop and overload)
        val result = VoltageDropCalculator.calculate(
            sectionMm2 = 1.5,
            powerWatts = 4000.0,
            distanceMeters = 80.0,
            systemType = SystemType.MONOFASICO_220,
            cosPhi = 0.85,
            material = ConductorMaterial.COPPER
        )

        assertTrue(result.complianceStatus == ComplianceStatus.NON_COMPLIANT || result.complianceStatus == ComplianceStatus.OVERLOAD)
        assertNotNull(result.recommendedSectionMm2)
        assertTrue(result.recommendedSectionMm2!! > 1.5)
    }

    @Test
    fun testSubterraneanCableHigherAmperage() {
        // 4 mm² cable:
        // Unipolar mono Iadm = 28 A
        // Subterráneo mono Iadm = 43 A
        val unipolarAdm = ConductorTable.getAdmissibleCurrent(4.0, com.example.data.electrical.CableType.UNIPOLAR, SystemType.MONOFASICO_220)
        val subterranAdm = ConductorTable.getAdmissibleCurrent(4.0, com.example.data.electrical.CableType.SUBTERRANEO, SystemType.MONOFASICO_220)
        
        assertEquals(28.0, unipolarAdm, 0.001)
        assertEquals(43.0, subterranAdm, 0.001)
        assertTrue(subterranAdm > unipolarAdm)

        // Test with load of 6500W at 220V, cosPhi=0.85 -> I = 6500 / (220 * 0.85) = 34.75 A
        // For unipolar 4mm2 (Iadm=28A): Overload!
        val unipolarResult = VoltageDropCalculator.calculate(
            sectionMm2 = 4.0,
            powerWatts = 6500.0,
            distanceMeters = 10.0,
            systemType = SystemType.MONOFASICO_220,
            cableType = com.example.data.electrical.CableType.UNIPOLAR,
            cosPhi = 0.85
        )
        assertTrue(unipolarResult.isThermallyOverloaded)
        assertEquals(ComplianceStatus.OVERLOAD, unipolarResult.complianceStatus)

        // For subterranean 4mm2 (Iadm=43A): No overload! (34.75A < 43A)
        val subterraneoResult = VoltageDropCalculator.calculate(
            sectionMm2 = 4.0,
            powerWatts = 6500.0,
            distanceMeters = 10.0,
            systemType = SystemType.MONOFASICO_220,
            cableType = com.example.data.electrical.CableType.SUBTERRANEO,
            cosPhi = 0.85
        )
        org.junit.Assert.assertFalse(subterraneoResult.isThermallyOverloaded)
        assertEquals(ComplianceStatus.OPTIMAL, subterraneoResult.complianceStatus)
    }
}
