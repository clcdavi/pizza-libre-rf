package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = try {
        PaymentStatus.valueOf(value)
    } catch (e: Exception) {
        PaymentStatus.PENDIENTE
    }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try {
        PaymentMethod.valueOf(value)
    } catch (e: Exception) {
        PaymentMethod.EFECTIVO
    }

    @TypeConverter
    fun fromSaleType(type: SaleType): String = type.name

    @TypeConverter
    fun toSaleType(value: String): SaleType = try {
        SaleType.valueOf(value)
    } catch (e: Exception) {
        SaleType.PREVENTA
    }
}
