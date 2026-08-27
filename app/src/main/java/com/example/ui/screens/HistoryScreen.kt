package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CalculationEntity
import com.example.data.electrical.ComplianceStatus
import com.example.ui.theme.PolishBg
import com.example.ui.theme.PolishCardBg
import com.example.ui.theme.PolishCardBorder
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryLight
import com.example.ui.theme.PolishStatusError
import com.example.ui.theme.PolishStatusErrorBg
import com.example.ui.theme.PolishStatusSuccess
import com.example.ui.theme.PolishStatusSuccessBg
import com.example.ui.theme.PolishStatusWarning
import com.example.ui.theme.PolishStatusWarningBg
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.theme.PolishTextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    historyList: List<CalculationEntity>,
    onLoadCalculation: (CalculationEntity) -> Unit,
    onDeleteCalculation: (Long) -> Unit,
    onClearAll: () -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBg)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("history_screen")
    ) {
        // Barra Superior del Historial
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "HISTORIAL RECIENTE",
                    style = MaterialTheme.typography.labelSmall,
                    color = PolishTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PolishPrimaryLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${historyList.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (historyList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PolishStatusErrorBg)
                        .clickable { showClearDialog = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = PolishStatusError,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Vaciar",
                            style = MaterialTheme.typography.labelSmall,
                            color = PolishStatusError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (historyList.isEmpty()) {
            EmptyHistoryState(onNavigateToCalculator = onNavigateToCalculator)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onLoad = { onLoadCalculation(item) },
                        onDelete = { onDeleteCalculation(item.id) },
                        onShare = { shareCalculation(context, item) }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = "Vaciar Historial", color = PolishTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(text = "¿Deseas eliminar todos los registros de cálculos guardados?", color = PolishTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishStatusError),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Eliminar todo", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = "Cancelar", color = PolishTextSecondary)
                }
            },
            containerColor = PolishCardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: CalculationEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val statusEnum = try {
        ComplianceStatus.valueOf(item.status)
    } catch (_: Exception) {
        ComplianceStatus.OPTIMAL
    }

    val statusColor = when (statusEnum) {
        ComplianceStatus.OPTIMAL -> PolishStatusSuccess
        ComplianceStatus.ADMISSIBLE -> PolishStatusWarning
        ComplianceStatus.NON_COMPLIANT, ComplianceStatus.OVERLOAD -> PolishStatusError
    }

    val statusBg = when (statusEnum) {
        ComplianceStatus.OPTIMAL -> PolishStatusSuccessBg
        ComplianceStatus.ADMISSIBLE -> PolishStatusWarningBg
        ComplianceStatus.NON_COMPLIANT, ComplianceStatus.OVERLOAD -> PolishStatusErrorBg
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "AR")) }
    val formattedDate = dateFormatter.format(Date(item.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PolishCardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PolishCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Fila Superior: Fecha y Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = PolishTextTertiary,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (statusEnum) {
                            ComplianceStatus.OPTIMAL -> Icons.Default.CheckCircle
                            ComplianceStatus.ADMISSIBLE -> Icons.Default.Warning
                            ComplianceStatus.NON_COMPLIANT, ComplianceStatus.OVERLOAD -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusEnum.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Datos Principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val cableLabel = if (item.cableType.equals("SUBTERRANEO", ignoreCase = true)) "Subterráneo" else "Unipolar"
                    Text(
                        text = "${item.sectionMm2} mm² ($cableLabel) • ${item.powerWatts.toInt()}W • ${item.distanceMeters.toInt()}m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PolishTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (item.isThreePhase) "Trifásico 380V • I=${String.format(Locale.US, "%.1fA", item.currentAmps)}" else "Monofásico 220V • I=${String.format(Locale.US, "%.1fA", item.currentAmps)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "%.2f %%", item.deltaVoltsPercent),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (statusEnum == ComplianceStatus.OPTIMAL) PolishPrimary else statusColor,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "ΔV = ${String.format(Locale.US, "%.2f V", item.deltaVolts)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishTextTertiary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            HorizontalDivider(
                color = PolishCardBorder,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Acciones: Cargar, Compartir, Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onLoad,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PolishPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PolishPrimary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Cargar Datos", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = PolishTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = PolishStatusError.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(onNavigateToCalculator: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(PolishPrimaryLight, CircleShape)
                .border(1.dp, PolishCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = PolishPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin cálculos guardados",
            style = MaterialTheme.typography.titleMedium,
            color = PolishTextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Realizá un cálculo en la calculadora y presioná 'Calcular y Guardar' para registrarlo aquí.",
            style = MaterialTheme.typography.bodyMedium,
            color = PolishTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNavigateToCalculator,
            colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Ir a la Calculadora", fontWeight = FontWeight.Bold)
        }
    }
}

private fun shareCalculation(context: Context, item: CalculationEntity) {
    val sys = if (item.isThreePhase) "Trifásico 380V" else "Monofásico 220V"
    val cableLabel = if (item.cableType.equals("SUBTERRANEO", ignoreCase = true)) "Subterráneo / Sintenax (IRAM 2178)" else "Unipolar en Cañería (IRAM 2183)"
    val text = """
        ⚡ REPORTE DE CAÍDA DE TENSIÓN (VoltCalc AR)
        ------------------------------------------
        • Sección: ${item.sectionMm2} mm² (${item.material})
        • Tipo de Cable: $cableLabel
        • Sistema: $sys (cos φ = ${String.format(Locale.US, "%.2f", item.cosPhi)})
        • Potencia: ${item.powerWatts.toInt()} W
        • Distancia: ${item.distanceMeters.toInt()} m
        • Corriente calculada (I): ${String.format(Locale.US, "%.2f A", item.currentAmps)}
        ------------------------------------------
        • Caída de Tensión (ΔV): ${String.format(Locale.US, "%.2f V", item.deltaVolts)}
        • Caída Porcentual (ΔV%): ${String.format(Locale.US, "%.2f %%", item.deltaVoltsPercent)}
        • Coeficiente k: ${String.format(Locale.US, "%.2f V/(A·km)", item.coefficientK)}
        • Estado reglamentario: ${item.status}
        ------------------------------------------
        Calculado con VoltCalc AR • Normativa AEA 90364
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Cálculo de Conductor - VoltCalc AR")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir cálculo"))
}
