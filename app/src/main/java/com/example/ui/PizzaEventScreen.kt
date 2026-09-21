package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TicketEntity
import com.example.ui.components.EventHeader
import com.example.ui.components.FilterChipRow
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatsSummaryCard
import com.example.ui.components.TicketCard
import com.example.ui.components.TicketDialog

@Composable
fun PizzaEventScreen(
    viewModel: TicketViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var ticketToEdit by remember { mutableStateOf<TicketEntity?>(null) }
    var ticketToDelete by remember { mutableStateOf<TicketEntity?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Seed sample data on initial launch if empty
    LaunchedEffect(Unit) {
        viewModel.addSampleDataIfEmpty()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EventHeader(
                onOpenSettings = { showSettingsDialog = true },
                onShareReport = {
                    if (uiState.tickets.isEmpty()) {
                        Toast.makeText(context, "No hay entradas registradas para compartir", Toast.LENGTH_SHORT).show()
                    } else {
                        ShareHelper.shareGeneralReport(context, uiState.stats, uiState.tickets)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Entrada", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_ticket")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Stats summary card
            StatsSummaryCard(
                stats = uiState.stats,
                targetAttendees = uiState.targetAttendees,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("search_input"),
                placeholder = { Text("Buscar persona, teléfono o nota...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Filter chips
            FilterChipRow(
                currentFilter = uiState.currentFilter,
                tickets = uiState.tickets,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            // Tickets List or Empty State
            if (uiState.filteredTickets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalPizza,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (uiState.searchQuery.isNotBlank() || uiState.currentFilter != TicketFilter.ALL) {
                                "No se encontraron entradas con los filtros seleccionados"
                            } else {
                                "No hay entradas registradas aún"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) {
                                "Prueba escribiendo otro nombre o limpia la búsqueda."
                            } else {
                                "¡Registra las entradas para la noche de Pizza Libre de la Iglesia Roca Fuerte!"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("btn_empty_add_ticket")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Registrar Primera Entrada")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("tickets_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.filteredTickets,
                        key = { it.id }
                    ) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            onToggleCheckIn = { viewModel.toggleCheckIn(ticket) },
                            onMarkAsPaid = { viewModel.markAsFullyPaid(ticket) },
                            onEdit = { ticketToEdit = ticket },
                            onDelete = { ticketToDelete = ticket },
                            onShare = { ShareHelper.shareTicketReceipt(context, ticket) }
                        )
                    }
                }
            }
        }
    }

    // Add Ticket Dialog
    if (showAddDialog) {
        TicketDialog(
            ticketToEdit = null,
            defaultPreSalePrice = uiState.defaultPreSalePrice,
            defaultDoorPrice = uiState.defaultDoorPrice,
            onDismiss = { showAddDialog = false },
            onSave = { name, count, unitPrice, amountPaid, status, method, saleType, isCheckedIn, phone, notes ->
                viewModel.addTicket(
                    buyerName = name,
                    ticketCount = count,
                    unitPrice = unitPrice,
                    amountPaid = amountPaid,
                    paymentStatus = status,
                    paymentMethod = method,
                    saleType = saleType,
                    isCheckedIn = isCheckedIn,
                    phone = phone,
                    notes = notes
                )
                showAddDialog = false
                Toast.makeText(context, "Entrada registrada con éxito", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Ticket Dialog
    ticketToEdit?.let { ticket ->
        TicketDialog(
            ticketToEdit = ticket,
            defaultPreSalePrice = uiState.defaultPreSalePrice,
            defaultDoorPrice = uiState.defaultDoorPrice,
            onDismiss = { ticketToEdit = null },
            onSave = { name, count, unitPrice, amountPaid, status, method, saleType, isCheckedIn, phone, notes ->
                val updated = ticket.copy(
                    buyerName = name,
                    ticketCount = count,
                    unitPrice = unitPrice,
                    amountPaid = amountPaid,
                    paymentStatus = status,
                    paymentMethod = method,
                    saleType = saleType,
                    isCheckedIn = isCheckedIn,
                    checkedInCount = if (isCheckedIn) count else 0,
                    phone = phone,
                    notes = notes
                )
                viewModel.updateTicket(updated)
                ticketToEdit = null
                Toast.makeText(context, "Entrada actualizada", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    ticketToDelete?.let { ticket ->
        AlertDialog(
            onDismissRequest = { ticketToDelete = null },
            modifier = Modifier.testTag("delete_confirmation_dialog"),
            title = { Text("¿Eliminar entrada?") },
            text = {
                Text("¿Estás seguro de que deseas eliminar el registro de '${ticket.buyerName}' (${ticket.ticketCount} entradas)? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTicket(ticket)
                        ticketToDelete = null
                        Toast.makeText(context, "Entrada eliminada", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("btn_confirm_delete")
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { ticketToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            currentPreSalePrice = uiState.defaultPreSalePrice,
            currentDoorPrice = uiState.defaultDoorPrice,
            currentTargetAttendees = uiState.targetAttendees,
            onDismiss = { showSettingsDialog = false },
            onSave = { preSalePrice, doorPrice, target ->
                viewModel.setDefaultPrices(preSalePrice, doorPrice)
                viewModel.setTargetAttendees(target)
                Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()
            },
            onLoadSampleData = {
                viewModel.addSampleDataIfEmpty()
                Toast.makeText(context, "Datos de ejemplo cargados", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
