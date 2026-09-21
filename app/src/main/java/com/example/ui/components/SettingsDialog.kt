package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SaleTypePreVenta
import com.example.ui.theme.SaleTypePuerta

@Composable
fun SettingsDialog(
    currentPreSalePrice: Double,
    currentDoorPrice: Double,
    currentTargetAttendees: Int,
    onDismiss: () -> Unit,
    onSave: (preSalePrice: Double, doorPrice: Double, targetAttendees: Int) -> Unit,
    onLoadSampleData: () -> Unit
) {
    var preSalePriceStr by remember { mutableStateOf(currentPreSalePrice.toInt().toString()) }
    var doorPriceStr by remember { mutableStateOf(currentDoorPrice.toInt().toString()) }
    var targetStr by remember { mutableStateOf(currentTargetAttendees.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("settings_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configuración del Evento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Ajusta los precios de Pre-venta y En puerta para el evento Pizza Libre de la Iglesia Roca Fuerte.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Pre-sale price
                OutlinedTextField(
                    value = preSalePriceStr,
                    onValueChange = { preSalePriceStr = it },
                    label = { Text("Precio Entrada Pre-venta") },
                    prefix = { Text("$") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = SaleTypePreVenta
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_presale_price")
                )

                // Door price
                OutlinedTextField(
                    value = doorPriceStr,
                    onValueChange = { doorPriceStr = it },
                    label = { Text("Precio Entrada En Puerta") },
                    prefix = { Text("$") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DoorFront,
                            contentDescription = null,
                            tint = SaleTypePuerta
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_door_price")
                )

                // Target attendees
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Meta de asistentes (Personas)") },
                    leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_target_attendees")
                )

                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                OutlinedButton(
                    onClick = {
                        onLoadSampleData()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("btn_load_sample_data")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cargar datos de ejemplo")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val preSale = preSalePriceStr.toDoubleOrNull() ?: currentPreSalePrice
                    val door = doorPriceStr.toDoubleOrNull() ?: currentDoorPrice
                    val target = targetStr.toIntOrNull() ?: currentTargetAttendees
                    onSave(preSale, door, target)
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_save_settings")
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
