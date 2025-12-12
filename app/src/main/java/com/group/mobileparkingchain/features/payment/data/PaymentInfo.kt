package com.group.mobileparkingchain.features.payment.data

data class PaymentInfo(
    val bookingId: String,
    val spotId: String,
    val duration: Int,
    val startTime: Long,
    val total: Double
)