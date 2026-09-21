package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.PaymentMethod
import com.example.data.PaymentStatus
import com.example.data.SaleType
import com.example.data.TicketEntity
import com.example.ui.theme.SaleTypePreVenta
import com.example.ui.theme.SaleTypePuerta
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPending
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TicketDialog(
    ticketToEdit: TicketEntity? = null,
    defaultPreSalePrice: Double = 4000.0,
    defaultDoorPrice: Double = 5000.0,
    onDismiss: () -> Unit,
    onSave: (
        buyerName: String,
        ticketCount: Int,
        unitPrice: Double,
        amountPaid: Double,
        paymentStatus: PaymentStatus,
        paymentMethod: PaymentMethod,
        saleType: SaleType,
        isCheckedIn: Boolean,
        phone: String,
        notes: String
    ) -> Unit
) {
    var saleType by remember {
        mutableStateOf(ticketToEdit?.saleType ?: SaleType.PREVENTA)
    }

    val initialUnitPrice = ticketToEdit?.unitPrice?.toInt()?.toString()
        ?: (if (saleType == SaleType.PREVENTA) defaultPreSalePrice.toInt().toString() else defaultDoorPrice.toInt().toString())

    var buyerName by remember { mutableStateOf(ticketToEdit?.buyerName ?: "") }
    var ticketCount by remember { mutableIntStateOf(ticketToEdit?.ticketCount ?: 1) }
    var unitPriceStr by remember { mutableStateOf(initialUnitPrice) }
    var paymentStatus by remember {
        mutableStateOf(ticketToEdit?.paymentStatus ?: PaymentStatus.PAGADO)
    }
    var paymentMethod by remember {
        mutableStateOf(ticketToEdit?.paymentMethod ?: PaymentMethod.EFECTIVO)
    }
    var isCheckedIn by remember {
        mutableStateOf(ticketToEdit?.isCheckedIn ?: (saleType == SaleType.PUERTA))
    }

    val unitPrice = unitPriceStr.toDoubleOrNull() ?: 0.0
    val totalAmount = ticketCount * unitPrice

    var amountPaidStr by remember {
        mutableStateOf(
            ticketToEdit?.amountPaid?.toInt()?.toString()
                ?: (if (paymentStatus == PaymentStatus.PAGADO) totalAmount.toInt().toString() else "0")
        )
    }

    var phone by remember { mutableStateOf(ticketToEdit?.phone ?: "") }
    var notes by remember { mutableStateOf(ticketToEdit?.notes ?: "") }
    var nameError by remember { mutableStateOf(false) }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
            maximumFractionDigits = 0
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("ticket_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalPizza,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (ticketToEdit == null) "Nueva Entrada" else "Editar Entrada",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sale Type selector (Pre-venta vs En puerta)
                Column {
                    Text(
                        text = "Tipo de venta / Entrada *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pre-venta chip
                        FilterChip(
                            selected = saleType == SaleType.PREVENTA,
                            onClick = {
                                saleType = SaleType.PREVENTA
                                if (ticketToEdit == null) {
                                    unitPriceStr = defaultPreSalePrice.toInt().toString()
                                    if (paymentStatus == PaymentStatus.PAGADO) {
                                        amountPaidStr = (ticketCount * defaultPreSalePrice).toInt().toString()
                                    }
                                }
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ConfirmationNumber,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pre-venta")
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaleTypePreVenta,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sale_type_preventa")
                        )

                        // En puerta chip
                        FilterChip(
                            selected = saleType == SaleType.PUERTA,
                            onClick = {
                                saleType = SaleType.PUERTA
                                if (ticketToEdit == null) {
                                    unitPriceStr = defaultDoorPrice.toInt().toString()
                                    if (paymentStatus == PaymentStatus.PAGADO) {
                                        amountPaidStr = (ticketCount * defaultDoorPrice).toInt().toString()
                                    }
                                    // Default to checked-in if buying at the door
                                    isCheckedIn = true
                                }
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DoorFront,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("En puerta")
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaleTypePuerta,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sale_type_puerta")
                        )
                    }
                }

                // Buyer name
                OutlinedTextField(
                    value = buyerName,
                    onValueChange = {
                        buyerName = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Nombre de persona o familia *") },
                    placeholder = { Text("Ej: Familia Gómez / Hno. Carlos") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    isError = nameError,
                    supportingText = {
                        if (nameError) Text("El nombre es requerido", color = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ticket_buyer_input")
                )

                // Ticket counter stepper
                Text(
                    text = "Cantidad de personas / entradas",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            if (ticketCount > 1) {
                                ticketCount--
                                if (paymentStatus == PaymentStatus.PAGADO) {
                                    amountPaidStr = (ticketCount * unitPrice).toInt().toString()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("ticket_stepper_minus")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Menos")
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$ticketCount",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (ticketCount == 1) "persona" else "personas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    IconButton(
                        onClick = {
                            ticketCount++
                            if (paymentStatus == PaymentStatus.PAGADO) {
                                amountPaidStr = (ticketCount * unitPrice).toInt().toString()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("ticket_stepper_plus")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más")
                    }
                }

                // Unit price and calculated total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = unitPriceStr,
                        onValueChange = {
                            unitPriceStr = it
                            val p = it.toDoubleOrNull() ?: 0.0
                            if (paymentStatus == PaymentStatus.PAGADO) {
                                amountPaidStr = (ticketCount * p).toInt().toString()
                            }
                        },
                        label = { Text("Precio unit.") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ticket_unit_price_input")
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Total a Pagar",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = currencyFormat.format(totalAmount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Payment Status Chips
                Text(
                    text = "Estado del pago",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentStatus.values().forEach { status ->
                        val isSelected = paymentStatus == status
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                paymentStatus = status
                                when (status) {
                                    PaymentStatus.PAGADO -> amountPaidStr = totalAmount.toInt().toString()
                                    PaymentStatus.PENDIENTE -> amountPaidStr = "0"
                                    PaymentStatus.PARCIAL -> {
                                        if (amountPaidStr.toDoubleOrNull() == 0.0 || amountPaidStr.toDoubleOrNull() == totalAmount) {
                                            amountPaidStr = (totalAmount / 2).toInt().toString()
                                        }
                                    }
                                }
                            },
                            label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (status) {
                                    PaymentStatus.PAGADO -> StatusPaid
                                    PaymentStatus.PENDIENTE -> StatusPending
                                    PaymentStatus.PARCIAL -> StatusPartial
                                },
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("payment_status_${status.name.lowercase()}")
                        )
                    }
                }

                // If partial payment, ask for amount paid
                if (paymentStatus == PaymentStatus.PARCIAL) {
                    OutlinedTextField(
                        value = amountPaidStr,
                        onValueChange = { amountPaidStr = it },
                        label = { Text("Monto abonado (Seña)") },
                        prefix = { Text("$") },
                        supportingText = {
                            val paid = amountPaidStr.toDoubleOrNull() ?: 0.0
                            val remaining = (totalAmount - paid).coerceAtLeast(0.0)
                            Text("Resta por abonar: ${currencyFormat.format(remaining)}")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ticket_partial_paid_input")
                    )
                }

                // Payment Method
                Text(
                    text = "Método de pago",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentMethod.values().forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method.label) }
                        )
                    }
                }

                // Check-in status toggle directly in dialog
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Marcar como ingresado en puerta",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Si la persona ya está presente en el salón",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Checkbox(
                            checked = isCheckedIn,
                            onCheckedChange = { isCheckedIn = it },
                            modifier = Modifier.testTag("dialog_checkin_checkbox")
                        )
                    }
                }

                // Phone (optional)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono / WhatsApp (Opcional)") },
                    placeholder = { Text("Ej: 11 1234-5678") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ticket_phone_input")
                )

                // Notes (optional)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas / Observaciones (Opcional)") },
                    placeholder = { Text("Ej: Mesa adelante, traen gaseosa, vegetariano...") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ticket_notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (buyerName.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val paid = when (paymentStatus) {
                        PaymentStatus.PAGADO -> totalAmount
                        PaymentStatus.PENDIENTE -> 0.0
                        PaymentStatus.PARCIAL -> amountPaidStr.toDoubleOrNull() ?: 0.0
                    }
                    onSave(
                        buyerName.trim(),
                        ticketCount,
                        unitPrice,
                        paid,
                        paymentStatus,
                        paymentMethod,
                        saleType,
                        isCheckedIn,
                        phone.trim(),
                        notes.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("ticket_save_button")
            ) {
                Text(if (ticketToEdit == null) "Registrar Entrada" else "Guardar Cambios")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("ticket_cancel_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
