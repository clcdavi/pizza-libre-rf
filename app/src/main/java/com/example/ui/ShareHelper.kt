package com.example.ui

import android.content.Context
import android.content.Intent
import com.example.data.PaymentStatus
import com.example.data.TicketEntity
import java.text.NumberFormat
import java.util.Locale

object ShareHelper {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
        maximumFractionDigits = 0
    }

    fun shareTicketReceipt(context: Context, ticket: TicketEntity) {
        val paymentText = when (ticket.paymentStatus) {
            PaymentStatus.PAGADO -> "PAGADO COMPLETO (${currencyFormat.format(ticket.totalAmount)})"
            PaymentStatus.PENDIENTE -> "PENDIENTE DE PAGO (${currencyFormat.format(ticket.totalAmount)})"
            PaymentStatus.PARCIAL -> "SEÑA ABONADA: ${currencyFormat.format(ticket.amountPaid)} | RESTA: ${currencyFormat.format(ticket.remainingAmount)}"
        }

        val message = """
🍕 *IGLESIA ROCA FUERTE - PIZZA LIBRE* 🍕
¡Hola *${ticket.buyerName}*! Aquí está el registro de tus entradas para nuestro evento:

🎟️ *Entradas:* ${ticket.ticketCount} ${if (ticket.ticketCount == 1) "persona" else "personas"}
🏷️ *Tipo de Entrada:* ${ticket.saleType.label} ($${ticket.unitPrice.toInt()} c/u)
💵 *Monto Total:* ${currencyFormat.format(ticket.totalAmount)}
💳 *Estado de Pago:* $paymentText
💳 *Método:* ${ticket.paymentMethod.label}
📍 *Asistencia:* ${if (ticket.isCheckedIn) "✅ Ya ingresó al evento" else "⏳ Pendiente de ingreso"}
${if (ticket.notes.isNotBlank()) "📝 *Nota:* ${ticket.notes}\n" else ""}
¡Te esperamos con muchas ganas para disfrutar y compartir en comunión! Dios te bendiga.
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir comprobante de ${ticket.buyerName}")
        context.startActivity(shareIntent)
    }

    fun shareGeneralReport(context: Context, stats: EventStats, tickets: List<TicketEntity>) {
        val message = buildString {
            appendLine("🍕 *PIZZA LIBRE - IGLESIA ROCA FUERTE* 🍕")
            appendLine("📊 *Reporte General de Entradas y Recaudación*")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("👥 *Total Personas / Entradas:* ${stats.totalTicketsSold}")
            appendLine("🏷️ *Pre-venta:* ${stats.preSaleTicketsSold} entradas | 🚪 *En Puerta:* ${stats.doorTicketsSold} entradas")
            appendLine("🎫 *Total Grupos/Familias Registradas:* ${stats.totalOrdersCount}")
            appendLine("💰 *Total Recaudado:* ${currencyFormat.format(stats.totalAmountCollected)}")
            appendLine("⏳ *Total Pendiente de Cobro:* ${currencyFormat.format(stats.totalAmountPending)}")
            appendLine("🎯 *Total Esperado:* ${currencyFormat.format(stats.totalRevenueExpected)}")
            appendLine("🚪 *Presentes en Puerta (Check-in):* ${stats.totalAttendeesCheckedIn} personas")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("📋 *Detalle resumido:*")
            tickets.forEachIndexed { index, t ->
                val statusEmoji = when (t.paymentStatus) {
                    PaymentStatus.PAGADO -> "✅"
                    PaymentStatus.PENDIENTE -> "❌"
                    PaymentStatus.PARCIAL -> "⚠️"
                }
                val checkinEmoji = if (t.isCheckedIn) " [Presente]" else ""
                appendLine("${index + 1}. $statusEmoji ${t.buyerName} (${t.ticketCount} ent. - ${t.saleType.label}) - ${t.paymentStatus.name}$checkinEmoji")
            }
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir balance del evento")
        context.startActivity(shareIntent)
    }
}
