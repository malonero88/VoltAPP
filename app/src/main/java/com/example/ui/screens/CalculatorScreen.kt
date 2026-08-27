package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.electrical.CableType
import com.example.data.electrical.CalculationResult
import com.example.data.electrical.ComplianceStatus
import com.example.data.electrical.ConductorMaterial
import com.example.data.electrical.ConductorTable
import com.example.data.electrical.SystemType
import com.example.ui.theme.PolishBg
import com.example.ui.theme.PolishCardBg
import com.example.ui.theme.PolishCardBorder
import com.example.ui.theme.PolishInputBg
import com.example.ui.theme.PolishInputBorder
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryLight
import com.example.ui.theme.PolishStatusError
import com.example.ui.theme.PolishStatusErrorBg
import com.example.ui.theme.PolishStatusSuccess
import com.example.ui.theme.PolishStatusWarning
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.theme.PolishTextTertiary
import com.example.ui.viewmodel.CalculatorUiState
import com.example.ui.viewmodel.CalculatorViewModel
import java.util.Locale

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    viewModel: CalculatorViewModel,
    onNavigateToFormulas: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var isAdvancedOptionsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Selector rápido Monofásico / Trifásico
        SystemTypeSelector(
            currentType = uiState.systemType,
            onTypeSelected = { viewModel.onSystemTypeChanged(it) }
        )

        // CARD PRINCIPAL DE ENTRADAS (Estilo Modern Polish)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PolishCardBg),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header de la Card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = PolishPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INGRESO DE DATOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishTextTertiary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                // 2. TIPO DE CABLE / INSTALACIÓN (Unipolar vs Subterráneo)
                CableTypeSelector(
                    selectedCableType = uiState.cableType,
                    onCableTypeSelected = { viewModel.onCableTypeChanged(it) }
                )

                // 3. SECCIÓN DEL CONDUCTOR (Menú Desplegable Normalizado)
                ConductorSectionDropdown(
                    selectedSection = uiState.sectionMm2,
                    cableType = uiState.cableType,
                    systemType = uiState.systemType,
                    onSectionSelected = { viewModel.onSectionChanged(it) }
                )

                // 4. GRID DISTANCIA Y POTENCIA
                DistanceInputField(
                    value = uiState.distanceInput,
                    onValueChange = { viewModel.onDistanceChanged(it) },
                    onQuickAdd = { viewModel.addDistance(it) },
                    onDone = { focusManager.clearFocus() }
                )

                PowerInputField(
                    value = uiState.powerInput,
                    unit = uiState.powerUnit,
                    onValueChange = { viewModel.onPowerChanged(it) },
                    onUnitChange = { viewModel.onPowerUnitChanged(it) },
                    onQuickAdd = { viewModel.addPower(it) },
                    onDone = { focusManager.clearFocus() }
                )

                // Parámetros Opcionales / Avanzados (cos phi, Material Cobre/Aluminio)
                AdvancedSettingsSection(
                    isExpanded = isAdvancedOptionsExpanded,
                    onToggleExpand = { isAdvancedOptionsExpanded = !isAdvancedOptionsExpanded },
                    cosPhi = uiState.cosPhiInput,
                    material = uiState.material,
                    onCosPhiChange = { viewModel.onCosPhiChanged(it) },
                    onMaterialChange = { viewModel.onMaterialChanged(it) },
                    onDone = { focusManager.clearFocus() }
                )

                // BOTÓN ACCIÓN "CALCULAR CAÍDA / GUARDAR"
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.saveCalculation()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("calculate_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PolishPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.isCalculationSaved) Icons.Default.Save else Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isCalculationSaved) "¡GUARDADO EN HISTORIAL!" else "CALCULAR Y GUARDAR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // HERO RESULT CARD (Professional Polish Design)
        val result = uiState.result
        if (result != null) {
            ResultsHeroCard(
                result = result,
                onApplyRecommendedSection = { rec ->
                    viewModel.applySectionQuick(rec)
                },
                onNavigateToFormulas = onNavigateToFormulas
            )
        }

        // Pie técnico
        Text(
            text = "Cálculo según AEA 90364 & Normas IRAM 2183 / 2178",
            style = MaterialTheme.typography.labelSmall,
            color = PolishTextTertiary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun SystemTypeSelector(
    currentType: SystemType,
    onTypeSelected: (SystemType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PolishCardBg, RoundedCornerShape(16.dp))
            .border(1.dp, PolishCardBorder, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SystemType.entries.forEach { type ->
            val isSelected = currentType == type
            val bgColor by animateColorAsState(
                if (isSelected) PolishPrimary else Color.Transparent,
                animationSpec = tween(150),
                label = "typeBg"
            )
            val textColor = if (isSelected) Color.White else PolishTextSecondary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = type.label,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CableTypeSelector(
    selectedCableType: CableType,
    onCableTypeSelected: (CableType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TIPO DE CABLE / INSTALACIÓN",
                style = MaterialTheme.typography.labelSmall,
                color = PolishTextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = selectedCableType.standardNorm,
                style = MaterialTheme.typography.labelSmall,
                color = PolishPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PolishInputBg, RoundedCornerShape(14.dp))
                .border(1.dp, PolishInputBorder, RoundedCornerShape(14.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CableType.entries.forEach { type ->
                val isSelected = selectedCableType == type
                val bgColor by animateColorAsState(
                    if (isSelected) PolishPrimary else Color.Transparent,
                    animationSpec = tween(150),
                    label = "cableTypeBg"
                )
                val textColor = if (isSelected) Color.White else PolishTextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .clickable { onCableTypeSelected(type) }
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (type == CableType.UNIPOLAR) Icons.Default.Bolt else Icons.Default.Security,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = type.shortLabel,
                            color = textColor,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConductorSectionDropdown(
    selectedSection: Double,
    cableType: CableType,
    systemType: SystemType,
    onSectionSelected: (Double) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val maxAdm = ConductorTable.getAdmissibleCurrent(selectedSection, cableType, systemType)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SECCIÓN DEL CABLE",
                style = MaterialTheme.typography.labelSmall,
                color = PolishPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Iadm: ${maxAdm.toInt()} A (${cableType.shortLabel})",
                style = MaterialTheme.typography.labelSmall,
                color = PolishTextTertiary,
                fontWeight = FontWeight.Medium
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(PolishInputBg, RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (expanded) PolishPrimary else PolishInputBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selectedSection % 1.0 == 0.0) "${selectedSection.toInt()}" else "$selectedSection",
                        style = MaterialTheme.typography.titleLarge,
                        color = PolishTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "mm²",
                        style = MaterialTheme.typography.titleMedium,
                        color = PolishPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Seleccionar",
                        style = MaterialTheme.typography.labelMedium,
                        color = PolishTextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar secciones",
                        tint = PolishPrimary
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(PolishCardBg)
                    .border(1.dp, PolishCardBorder, RoundedCornerShape(12.dp))
            ) {
                ConductorTable.standardSections.forEach { sec ->
                    val secAdm = ConductorTable.getAdmissibleCurrent(sec.sectionMm2, cableType, systemType)
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sec.label,
                                    color = if (sec.sectionMm2 == selectedSection) PolishPrimary else PolishTextPrimary,
                                    fontWeight = if (sec.sectionMm2 == selectedSection) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Iadm: ${secAdm.toInt()} A",
                                    color = PolishTextSecondary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        onClick = {
                            onSectionSelected(sec.sectionMm2)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DistanceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onQuickAdd: (Double) -> Unit,
    onDone: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "LARGO / DISTANCIA (MTS)",
            style = MaterialTheme.typography.labelSmall,
            color = PolishTextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("distance_input"),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp,
                color = PolishTextPrimary,
                fontWeight = FontWeight.Bold
            ),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    if (value.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar", tint = PolishTextTertiary)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "mts",
                        style = MaterialTheme.typography.labelMedium,
                        color = PolishPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onDone() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PolishInputBg,
                unfocusedContainerColor = PolishInputBg,
                focusedBorderColor = PolishPrimary,
                unfocusedBorderColor = PolishInputBorder,
                cursorColor = PolishPrimary
            ),
            shape = RoundedCornerShape(16.dp)
        )

        // Botones de incremento rápido
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(5.0 to "+5m", 10.0 to "+10m", 25.0 to "+25m", 50.0 to "+50m").forEach { (delta, label) ->
                QuickPillButton(text = label, onClick = { onQuickAdd(delta) })
            }
        }
    }
}

@Composable
private fun PowerInputField(
    value: String,
    unit: String,
    onValueChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onQuickAdd: (Double) -> Unit,
    onDone: () -> Unit
) {
    var unitMenuExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "POTENCIA DE CARGA",
            style = MaterialTheme.typography.labelSmall,
            color = PolishTextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("power_input"),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp,
                color = PolishTextPrimary,
                fontWeight = FontWeight.Bold
            ),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    if (value.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar", tint = PolishTextTertiary)
                        }
                    }
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PolishPrimaryLight)
                                .clickable { unitMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = unit,
                                style = MaterialTheme.typography.labelMedium,
                                color = PolishPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar unidad",
                                tint = PolishPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = unitMenuExpanded,
                            onDismissRequest = { unitMenuExpanded = false },
                            modifier = Modifier.background(PolishCardBg)
                        ) {
                            listOf("W", "kW", "HP").forEach { u ->
                                DropMenuItemOption(u = u, currentUnit = unit, onSelect = {
                                    onUnitChange(it)
                                    unitMenuExpanded = false
                                })
                            }
                        }
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PolishInputBg,
                unfocusedContainerColor = PolishInputBg,
                focusedBorderColor = PolishPrimary,
                unfocusedBorderColor = PolishInputBorder,
                cursorColor = PolishPrimary
            ),
            shape = RoundedCornerShape(16.dp)
        )

        // Botones de incremento rápido
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val pills = when (unit) {
                "kW" -> listOf(0.5 to "+0.5k", 1.0 to "+1k", 2.5 to "+2.5k", 5.0 to "+5k")
                "HP" -> listOf(0.5 to "+0.5 HP", 1.0 to "+1 HP", 2.0 to "+2 HP", 5.0 to "+5 HP")
                else -> listOf(500.0 to "+500W", 1000.0 to "+1kW", 2000.0 to "+2kW", 5000.0 to "+5kW")
            }
            pills.forEach { (delta, label) ->
                QuickPillButton(text = label, onClick = { onQuickAdd(delta) })
            }
        }
    }
}

@Composable
private fun DropMenuItemOption(u: String, currentUnit: String, onSelect: (String) -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                text = when (u) {
                    "W" -> "Watts (W)"
                    "kW" -> "Kilowatts (kW)"
                    "HP" -> "Caballos de Fuerza (HP)"
                    else -> u
                },
                color = if (u == currentUnit) PolishPrimary else PolishTextPrimary,
                fontWeight = if (u == currentUnit) FontWeight.Bold else FontWeight.Normal
            )
        },
        onClick = { onSelect(u) }
    )
}

@Composable
private fun QuickPillButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = PolishPrimaryLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder),
        modifier = Modifier.height(30.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = PolishPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AdvancedSettingsSection(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    cosPhi: String,
    material: ConductorMaterial,
    onCosPhiChange: (String) -> Unit,
    onMaterialChange: (ConductorMaterial) -> Unit,
    onDone: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = PolishPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ajustes Avanzados (cos φ, Material)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PolishTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = PolishTextSecondary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(PolishInputBg, RoundedCornerShape(16.dp))
                    .border(1.dp, PolishInputBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Factor de potencia cos phi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Factor de Potencia (cos φ)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PolishTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Típico en Argentina: 0.85 a 0.95",
                            style = MaterialTheme.typography.labelSmall,
                            color = PolishTextTertiary
                        )
                    }

                    OutlinedTextField(
                        value = cosPhi,
                        onValueChange = onCosPhiChange,
                        modifier = Modifier.width(90.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = PolishTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onDone() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PolishCardBg,
                            unfocusedContainerColor = PolishCardBg,
                            focusedBorderColor = PolishPrimary,
                            unfocusedBorderColor = PolishInputBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                HorizontalDivider(color = PolishCardBorder)

                // Material del conductor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Material Conductor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PolishTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ConductorMaterial.entries.forEach { mat ->
                            val isSelected = mat == material
                            Surface(
                                onClick = { onMaterialChange(mat) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PolishPrimary else PolishCardBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) PolishPrimary else PolishInputBorder
                                )
                            ) {
                                Text(
                                    text = if (mat == ConductorMaterial.COPPER) "Cobre" else "Aluminio",
                                    color = if (isSelected) Color.White else PolishTextSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultsHeroCard(
    result: CalculationResult,
    onApplyRecommendedSection: (Double) -> Unit,
    onNavigateToFormulas: () -> Unit
) {
    val bannerBg = when (result.complianceStatus) {
        ComplianceStatus.OPTIMAL -> PolishPrimary
        ComplianceStatus.ADMISSIBLE -> Color(0xFFD97706)
        ComplianceStatus.NON_COMPLIANT, ComplianceStatus.OVERLOAD -> Color(0xFFDC2626)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_card"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. VIBRANT HERO CARD (De acuerdo al diseño "Professional Polish")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = bannerBg),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RESULTADO ESTIMADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%.2f", result.deltaVoltsPercent),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = result.complianceStatus.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Círculo decorativo con icono
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (result.complianceStatus) {
                            ComplianceStatus.OPTIMAL -> Icons.Default.CheckCircle
                            ComplianceStatus.ADMISSIBLE -> Icons.Default.Warning
                            ComplianceStatus.NON_COMPLIANT, ComplianceStatus.OVERLOAD -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // 2. DETALLE DE MÉTRICAS TÉCNICAS (Tarjeta Blanca Pulida)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PolishCardBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Caída en Voltios
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Caída (ΔV)", style = MaterialTheme.typography.labelSmall, color = PolishTextTertiary)
                        Text(
                            text = String.format(Locale.US, "%.2f V", result.deltaVolts),
                            style = MaterialTheme.typography.titleMedium,
                            color = PolishTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Corriente
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Corriente (I)", style = MaterialTheme.typography.labelSmall, color = PolishTextTertiary)
                        Text(
                            text = String.format(Locale.US, "%.2f A", result.currentAmps),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (result.isThermallyOverloaded) PolishStatusError else PolishTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Tensión Final
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(text = "Tensión Final", style = MaterialTheme.typography.labelSmall, color = PolishTextTertiary)
                        Text(
                            text = String.format(Locale.US, "%.1f V", result.finalVoltage),
                            style = MaterialTheme.typography.titleMedium,
                            color = PolishPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                HorizontalDivider(
                    color = PolishCardBorder,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Iadm: ${result.maxAdmissibleCurrentA.toInt()} A • k: ${String.format(Locale.US, "%.2f", result.coefficientK)} V/(A·km)",
                        style = MaterialTheme.typography.bodySmall,
                        color = PolishTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${result.cableType.shortLabel} (${result.cableType.standardNorm.split(" ").first()})",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Alerta de sobrecarga térmica si corresponde
                if (result.isThermallyOverloaded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PolishStatusErrorBg, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = PolishStatusError, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Alerta! Corriente ${String.format(Locale.US, "%.1f A", result.currentAmps)} supera la admisible ${result.maxAdmissibleCurrentA.toInt()} A (${result.cableType.shortLabel}).",
                            style = MaterialTheme.typography.labelSmall,
                            color = PolishStatusError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Sugerencia de sección recomendada si no cumple
                if (result.recommendedSectionMm2 != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PolishPrimaryLight, RoundedCornerShape(12.dp))
                            .border(1.dp, PolishPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PolishPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Sección sugerida (${result.cableType.shortLabel} ≤ 3%):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PolishTextSecondary
                                )
                                Text(
                                    text = "${result.recommendedSectionMm2} mm²",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PolishPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Button(
                            onClick = { onApplyRecommendedSection(result.recommendedSectionMm2) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
                        ) {
                            Text(text = "Aplicar", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
