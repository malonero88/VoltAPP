package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.electrical.CableType
import com.example.data.electrical.ConductorTable
import com.example.ui.theme.PolishBg
import com.example.ui.theme.PolishCardBg
import com.example.ui.theme.PolishCardBorder
import com.example.ui.theme.PolishDarkContainer
import com.example.ui.theme.PolishDarkContainerBorder
import com.example.ui.theme.PolishDarkContainerSurface
import com.example.ui.theme.PolishInputBg
import com.example.ui.theme.PolishInputBorder
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryLight
import com.example.ui.theme.PolishStatusError
import com.example.ui.theme.PolishStatusSuccess
import com.example.ui.theme.PolishStatusWarning
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.theme.PolishTextTertiary

@Composable
fun FormulasScreen() {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Fórmulas", "Tabla IRAM", "Normativa AEA")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBg)
            .testTag("formulas_screen")
    ) {
        // Tab de navegación interna
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = PolishCardBg,
            contentColor = PolishPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = PolishPrimary,
                    height = 3.dp
                )
            },
            divider = { HorizontalDivider(color = PolishCardBorder) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedSubTab == index) PolishPrimary else PolishTextSecondary
                        )
                    }
                )
            }
        }

        // Contenido del Sub-Tab
        when (selectedSubTab) {
            0 -> FormulasExplanationContent()
            1 -> IramTableContent()
            2 -> AeaRegulationsContent()
        }
    }
}

@Composable
private fun FormulasExplanationContent() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Método por Coeficiente de Caída k (Tarjeta Técnica Oscura estilo Polish Footer)
        TechnicalTerminalCard(
            title = "1. Método por Coeficiente de Caída (k)",
            formula = "ΔU = (2 · L · P) / (56 · S · U)   o   ΔV = k · I · (L / 1000)",
            description = "Fórmula práctica recomendada para proyectistas y electricistas en Argentina según IRAM 2183:"
        ) {
            Text(text = "• ΔV: Caída de tensión en Voltios [V]", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            Text(text = "• k: Coeficiente de caída en [V / (A · km)] para cada sección normalizada", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            Text(text = "• I: Corriente de carga en Amperes [A]", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            Text(text = "• L: Longitud del circuito en metros [m]", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
        }

        // 2. Cálculo de Corriente de Carga (I)
        FormulaCard(
            title = "2. Cálculo de Intensidad de Corriente (I)",
            icon = Icons.Default.ElectricBolt,
            description = "Determinación de la corriente nominal a partir de la potencia activa requerida:"
        ) {
            Text(text = "Circuito Monofásico (220V):", style = MaterialTheme.typography.labelMedium, color = PolishPrimary, fontWeight = FontWeight.Bold)
            FormulaBox(formula = "I = P / (V · cos φ)")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Circuito Trifásico (380V):", style = MaterialTheme.typography.labelMedium, color = PolishPrimary, fontWeight = FontWeight.Bold)
            FormulaBox(formula = "I = P / (√3 · V · cos φ)")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "• P: Potencia activa en Watts [W]", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
            Text(text = "• V: Tensión nominal de línea (220V o 380V)", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
            Text(text = "• cos φ: Factor de potencia (habitual 0.85)", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
            Text(text = "• √3 ≈ 1.732", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
        }

        // 3. Fórmulas Físicas Directas por Resistividad (ρ)
        FormulaCard(
            title = "3. Fórmula Física Directa por Resistividad",
            icon = Icons.Default.Info,
            description = "Cálculo por resistividad del conductor a temperatura máxima de servicio (70°C):"
        ) {
            Text(text = "Monofásico:", style = MaterialTheme.typography.labelMedium, color = PolishPrimary, fontWeight = FontWeight.Bold)
            FormulaBox(formula = "ΔV = (2 · ρ · L · I · cos φ) / S")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Trifásico:", style = MaterialTheme.typography.labelMedium, color = PolishPrimary, fontWeight = FontWeight.Bold)
            FormulaBox(formula = "ΔV = (√3 · ρ · L · I · cos φ) / S")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "• ρ Cobre (70°C) = 0.0210 Ω·mm²/m", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
            Text(text = "• ρ Aluminio (70°C) = 0.0330 Ω·mm²/m", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
            Text(text = "• S: Sección nominal del conductor en mm²", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
        }

        // 4. Caída Porcentual
        FormulaCard(
            title = "4. Caída Porcentual (ΔV%)",
            icon = Icons.Default.Rule,
            description = "Porcentaje de variación respecto a la tensión nominal:"
        ) {
            FormulaBox(formula = "ΔV% = (ΔV / V_nominal) · 100")
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "• Tensión nominal: 220V (Monofásico) o 380V (Trifásico)", style = MaterialTheme.typography.bodyMedium, color = PolishTextPrimary)
        }
    }
}

@Composable
private fun TechnicalTerminalCard(
    title: String,
    formula: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PolishDarkContainer),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PolishDarkContainerBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PolishDarkContainerSurface)
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF93C5FD),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun IramTableContent() {
    val scrollState = rememberScrollState()
    var selectedTableType by remember { mutableStateOf(CableType.UNIPOLAR) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PolishCardBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TableChart, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedTableType == CableType.UNIPOLAR) "Tabla Conductores (IRAM 2183 / 247-3)" else "Tabla Subterráneos (IRAM 2178)",
                        style = MaterialTheme.typography.titleMedium,
                        color = PolishTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (selectedTableType == CableType.UNIPOLAR) {
                        "Valores de k (cos φ=0.85) e intensidades admisibles para conductores unipolares de cobre en cañería embutida (30°C ambiente)."
                    } else {
                        "Valores e intensidades admisibles para cables subterráneos multipolares (Sintenax IRAM 2178) directamente enterrados (25°C terreno) / al aire."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = PolishTextSecondary
                )
            }
        }

        // Selector de tipo de tabla (Unipolar vs Subterráneo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PolishInputBg, RoundedCornerShape(12.dp))
                .border(1.dp, PolishInputBorder, RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CableType.entries.forEach { type ->
                val isSelected = selectedTableType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PolishPrimary else Color.Transparent)
                        .clickable { selectedTableType = type }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (type == CableType.UNIPOLAR) "Unipolar (IRAM 2183)" else "Subterráneo (IRAM 2178)",
                        color = if (isSelected) Color.White else PolishTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Cabecera de la Tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PolishPrimary, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sección", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.weight(1.1f), fontWeight = FontWeight.Bold)
            Text(
                text = if (selectedTableType == CableType.UNIPOLAR) "Iadm (Mono)" else "Iadm (2x Bip)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.weight(1.3f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (selectedTableType == CableType.UNIPOLAR) "Iadm (Tri)" else "Iadm (4x Tet)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.weight(1.3f),
                fontWeight = FontWeight.Bold
            )
            Text(text = "k (Mono)", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.weight(1.0f), fontWeight = FontWeight.Bold)
        }

        // Filas de la Tabla
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PolishCardBorder, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
        ) {
            ConductorTable.standardSections.forEachIndexed { index, item ->
                val rowBg = if (index % 2 == 0) PolishCardBg else PolishInputBg
                val admMono = if (selectedTableType == CableType.UNIPOLAR) item.admissibleCurrentMonoA else item.admissibleSubterraneoMonoA
                val admTri = if (selectedTableType == CableType.UNIPOLAR) item.admissibleCurrentTriA else item.admissibleSubterraneoTriA

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PolishPrimary,
                        modifier = Modifier.weight(1.1f),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${admMono.toInt()} A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PolishTextPrimary,
                        modifier = Modifier.weight(1.3f),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${admTri.toInt()} A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PolishTextPrimary,
                        modifier = Modifier.weight(1.3f),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${item.kMono}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PolishTextSecondary,
                        modifier = Modifier.weight(1.0f),
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (index < ConductorTable.standardSections.size - 1) {
                    HorizontalDivider(color = PolishCardBorder.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
private fun AeaRegulationsContent() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PolishCardBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Rule, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Límites AEA 90364",
                        style = MaterialTheme.typography.titleMedium,
                        color = PolishTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reglamentación para la Ejecución de Instalaciones Eléctricas en Inmuebles (Asociación Electrotécnica Argentina):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PolishTextSecondary
                )
            }
        }

        // Límite 3% - Iluminación y Tomas
        RegulationLimitCard(
            title = "Circuitos Terminales de Iluminación y Usos Generales",
            maxDrop = "Máx 3.0 % (6.6 V en 220V)",
            statusColor = PolishStatusSuccess,
            description = "Aplica a circuitos de iluminación de uso general (IUG), tomacorrientes (TUG) y tomas de usos especiales (TUE). Es el estándar principal de confort y vida útil de artefactos."
        )

        // Límite 5% - Fuerza Motriz
        RegulationLimitCard(
            title = "Circuitos de Fuerza Motriz y Motores",
            maxDrop = "Máx 5.0 % (11.0 V en 220V / 19.0 V en 380V)",
            statusColor = PolishStatusWarning,
            description = "Aplica a circuitos específicos que alimentan motores eléctricos, bombas, climatización o maquinaria industrial durante régimen permanente de funcionamiento."
        )

        // Límite 1% a 1.5% - Línea Principal
        RegulationLimitCard(
            title = "Línea Principal (Medidor a Tablero Principal)",
            maxDrop = "Máx 1.0 % a 1.5 % (2.2 V a 3.3 V)",
            statusColor = PolishPrimary,
            description = "Línea de alimentación desde el gabinete de medidor hasta el Tablero Principal (TP) o seccionales para reservar margen de caída a los circuitos terminales."
        )

        // Verificación de Corriente Admisible
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PolishCardBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Criterio Térmico (I ≤ Iadm):",
                    style = MaterialTheme.typography.labelLarge,
                    color = PolishPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "El conductor seleccionado por caída de tensión debe además soportar la corriente de carga sin sobrecalentamiento. La corriente calculada (I) debe ser menor o igual a la corriente admisible (Iadm) según la sección y canalización.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PolishTextSecondary
                )
            }
        }
    }
}

@Composable
private fun FormulaCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PolishCardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PolishTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = PolishTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun FormulaBox(formula: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PolishInputBg, RoundedCornerShape(12.dp))
            .border(1.dp, PolishInputBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formula,
            style = MaterialTheme.typography.titleMedium,
            color = PolishPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun RegulationLimitCard(
    title: String,
    maxDrop: String,
    statusColor: Color,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PolishCardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PolishTextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = maxDrop,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = PolishTextSecondary
            )
        }
    }
}
