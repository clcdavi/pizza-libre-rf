package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PaymentStatus {
    PAGADO,
    PENDIENTE,
    PARCIAL
}

enum class PaymentMethod(val label: String) {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA("Transferencia"),
    OTRO("Otro")
}

enum class SaleType(val label: String) {
    PREVENTA("Pre-venta"),
    PUERTA("En puerta")
}

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val buyerName: String,
    val ticketCount: Int = 1,
    val unitPrice: Double = 4000.0,
    val amountPaid: Double = 4000.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PAGADO,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val saleType: SaleType = SaleType.PREVENTA,
    val isCheckedIn: Boolean = false,
    val checkedInCount: Int = 0,
    val phone: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalAmount: Double
        get() = ticketCount * unitPrice

    val remainingAmount: Double
        get() = (totalAmount - amountPaid).coerceAtLeast(0.0)
}
