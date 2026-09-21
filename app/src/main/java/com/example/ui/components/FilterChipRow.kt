package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PaymentStatus
import com.example.data.SaleType
import com.example.data.TicketEntity
import com.example.ui.TicketFilter
import com.example.ui.theme.SaleTypePreVenta
import com.example.ui.theme.SaleTypePuerta
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPending

@Composable
fun FilterChipRow(
    currentFilter: TicketFilter,
    tickets: List<TicketEntity>,
    onFilterSelected: (TicketFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TicketFilter.values().forEach { filter ->
            val count = when (filter) {
                TicketFilter.ALL -> tickets.size
                TicketFilter.PREVENTA -> tickets.count { it.saleType == SaleType.PREVENTA }
                TicketFilter.PUERTA -> tickets.count { it.saleType == SaleType.PUERTA }
                TicketFilter.PENDING -> tickets.count { it.paymentStatus == PaymentStatus.PENDIENTE }
                TicketFilter.PAID -> tickets.count { it.paymentStatus == PaymentStatus.PAGADO }
                TicketFilter.PARTIAL -> tickets.count { it.paymentStatus == PaymentStatus.PARCIAL }
                TicketFilter.NOT_CHECKED_IN -> tickets.count { !it.isCheckedIn }
                TicketFilter.CHECKED_IN -> tickets.count { it.isCheckedIn }
            }

            val isSelected = currentFilter == filter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = "${filter.label} ($count)",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = when (filter) {
                        TicketFilter.PREVENTA -> SaleTypePreVenta
                        TicketFilter.PUERTA -> SaleTypePuerta
                        TicketFilter.PENDING -> StatusPending
                        TicketFilter.PAID -> StatusPaid
                        else -> MaterialTheme.colorScheme.primary
                    },
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
