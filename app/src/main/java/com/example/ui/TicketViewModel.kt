package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PaymentMethod
import com.example.data.PaymentStatus
import com.example.data.SaleType
import com.example.data.TicketEntity
import com.example.data.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TicketFilter(val label: String) {
    ALL("Todas"),
    PREVENTA("Pre-venta"),
    PUERTA("En puerta"),
    PENDING("Pendientes"),
    PAID("Pagadas"),
    PARTIAL("Parciales"),
    NOT_CHECKED_IN("Por ingresar"),
    CHECKED_IN("Ingresados")
}

data class EventStats(
    val totalTicketsSold: Int = 0,
    val preSaleTicketsSold: Int = 0,
    val doorTicketsSold: Int = 0,
    val totalAttendeesCheckedIn: Int = 0,
    val totalAmountCollected: Double = 0.0,
    val totalAmountPending: Double = 0.0,
    val totalRevenueExpected: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val pendingOrdersCount: Int = 0,
    val paidOrdersCount: Int = 0
)

data class TicketUiState(
    val tickets: List<TicketEntity> = emptyList(),
    val filteredTickets: List<TicketEntity> = emptyList(),
    val currentFilter: TicketFilter = TicketFilter.ALL,
    val searchQuery: String = "",
    val stats: EventStats = EventStats(),
    val defaultPreSalePrice: Double = 4000.0,
    val defaultDoorPrice: Double = 5000.0,
    val targetAttendees: Int = 100,
    val isLoading: Boolean = false
)

private data class FilterAndSettings(
    val filter: TicketFilter,
    val query: String,
    val preSalePrice: Double,
    val doorPrice: Double,
    val target: Int
)

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    private val _currentFilter = MutableStateFlow(TicketFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _defaultPreSalePrice = MutableStateFlow(4000.0)
    private val _defaultDoorPrice = MutableStateFlow(5000.0)
    private val _targetAttendees = MutableStateFlow(100)

    private val _settingsFlow = combine(
        _currentFilter,
        _searchQuery,
        _defaultPreSalePrice,
        _defaultDoorPrice,
        _targetAttendees
    ) { filter, query, preSalePrice, doorPrice, target ->
        FilterAndSettings(filter, query, preSalePrice, doorPrice, target)
    }

    val uiState: StateFlow<TicketUiState> = combine(
        repository.allTickets,
        _settingsFlow
    ) { tickets, settings ->
        val filter = settings.filter
        val query = settings.query
        val preSalePrice = settings.preSalePrice
        val doorPrice = settings.doorPrice
        val target = settings.target

        val queryTrimmed = query.trim().lowercase()
        val filtered = tickets.filter { ticket ->
            val matchesFilter = when (filter) {
                TicketFilter.ALL -> true
                TicketFilter.PREVENTA -> ticket.saleType == SaleType.PREVENTA
                TicketFilter.PUERTA -> ticket.saleType == SaleType.PUERTA
                TicketFilter.PENDING -> ticket.paymentStatus == PaymentStatus.PENDIENTE
                TicketFilter.PAID -> ticket.paymentStatus == PaymentStatus.PAGADO
                TicketFilter.PARTIAL -> ticket.paymentStatus == PaymentStatus.PARCIAL
                TicketFilter.NOT_CHECKED_IN -> !ticket.isCheckedIn
                TicketFilter.CHECKED_IN -> ticket.isCheckedIn
            }

            val matchesQuery = if (queryTrimmed.isEmpty()) {
                true
            } else {
                ticket.buyerName.lowercase().contains(queryTrimmed) ||
                        ticket.phone.lowercase().contains(queryTrimmed) ||
                        ticket.notes.lowercase().contains(queryTrimmed)
            }

            matchesFilter && matchesQuery
        }

        val totalSold = tickets.sumOf { it.ticketCount }
        val preSaleSold = tickets.filter { it.saleType == SaleType.PREVENTA }.sumOf { it.ticketCount }
        val doorSold = tickets.filter { it.saleType == SaleType.PUERTA }.sumOf { it.ticketCount }
        val totalCheckedIn = tickets.sumOf { if (it.isCheckedIn) it.ticketCount else 0 }
        val totalCollected = tickets.sumOf { it.amountPaid }
        val totalPending = tickets.sumOf { it.remainingAmount }
        val totalExpected = tickets.sumOf { it.totalAmount }
        val pendingOrders = tickets.count { it.paymentStatus != PaymentStatus.PAGADO }
        val paidOrders = tickets.count { it.paymentStatus == PaymentStatus.PAGADO }

        val stats = EventStats(
            totalTicketsSold = totalSold,
            preSaleTicketsSold = preSaleSold,
            doorTicketsSold = doorSold,
            totalAttendeesCheckedIn = totalCheckedIn,
            totalAmountCollected = totalCollected,
            totalAmountPending = totalPending,
            totalRevenueExpected = totalExpected,
            totalOrdersCount = tickets.size,
            pendingOrdersCount = pendingOrders,
            paidOrdersCount = paidOrders
        )

        TicketUiState(
            tickets = tickets,
            filteredTickets = filtered,
            currentFilter = filter,
            searchQuery = query,
            stats = stats,
            defaultPreSalePrice = preSalePrice,
            defaultDoorPrice = doorPrice,
            targetAttendees = target,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TicketUiState(isLoading = true)
    )

    fun setFilter(filter: TicketFilter) {
        _currentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDefaultPrices(preSalePrice: Double, doorPrice: Double) {
        if (preSalePrice > 0) _defaultPreSalePrice.value = preSalePrice
        if (doorPrice > 0) _defaultDoorPrice.value = doorPrice
    }

    fun setTargetAttendees(target: Int) {
        if (target > 0) {
            _targetAttendees.value = target
        }
    }

    fun addTicket(
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
    ) {
        viewModelScope.launch {
            val ticket = TicketEntity(
                buyerName = buyerName.trim(),
                ticketCount = ticketCount,
                unitPrice = unitPrice,
                amountPaid = amountPaid,
                paymentStatus = paymentStatus,
                paymentMethod = paymentMethod,
                saleType = saleType,
                phone = phone.trim(),
                notes = notes.trim(),
                isCheckedIn = isCheckedIn,
                checkedInCount = if (isCheckedIn) ticketCount else 0
            )
            repository.insert(ticket)
        }
    }

    fun updateTicket(ticket: TicketEntity) {
        viewModelScope.launch {
            repository.update(ticket)
        }
    }

    fun deleteTicket(ticket: TicketEntity) {
        viewModelScope.launch {
            repository.delete(ticket)
        }
    }

    fun toggleCheckIn(ticket: TicketEntity) {
        viewModelScope.launch {
            val updated = ticket.copy(
                isCheckedIn = !ticket.isCheckedIn,
                checkedInCount = if (!ticket.isCheckedIn) ticket.ticketCount else 0
            )
            repository.update(updated)
        }
    }

    fun markAsFullyPaid(ticket: TicketEntity) {
        viewModelScope.launch {
            val updated = ticket.copy(
                amountPaid = ticket.totalAmount,
                paymentStatus = PaymentStatus.PAGADO
            )
            repository.update(updated)
        }
    }

    fun addSampleDataIfEmpty() {
        viewModelScope.launch {
            val current = uiState.value.tickets
            if (current.isEmpty()) {
                val samples = listOf(
                    TicketEntity(
                        buyerName = "Familia González",
                        ticketCount = 4,
                        unitPrice = 4000.0,
                        amountPaid = 16000.0,
                        paymentStatus = PaymentStatus.PAGADO,
                        paymentMethod = PaymentMethod.TRANSFERENCIA,
                        saleType = SaleType.PREVENTA,
                        isCheckedIn = true,
                        checkedInCount = 4,
                        phone = "11 5544-3322",
                        notes = "Pre-venta cancelada"
                    ),
                    TicketEntity(
                        buyerName = "Marcos y Ana López",
                        ticketCount = 2,
                        unitPrice = 4000.0,
                        amountPaid = 4000.0,
                        paymentStatus = PaymentStatus.PARCIAL,
                        paymentMethod = PaymentMethod.EFECTIVO,
                        saleType = SaleType.PREVENTA,
                        isCheckedIn = false,
                        phone = "11 9988-7766",
                        notes = "Seña pre-venta, abona resto en puerta"
                    ),
                    TicketEntity(
                        buyerName = "Grupo Amigos de Esteban",
                        ticketCount = 3,
                        unitPrice = 5000.0,
                        amountPaid = 15000.0,
                        paymentStatus = PaymentStatus.PAGADO,
                        paymentMethod = PaymentMethod.EFECTIVO,
                        saleType = SaleType.PUERTA,
                        isCheckedIn = true,
                        checkedInCount = 3,
                        phone = "11 3344-5566",
                        notes = "Compraron en puerta al llegar"
                    )
                )
                for (item in samples) {
                    repository.insert(item)
                }
            }
        }
    }
}

class TicketViewModelFactory(private val repository: TicketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
