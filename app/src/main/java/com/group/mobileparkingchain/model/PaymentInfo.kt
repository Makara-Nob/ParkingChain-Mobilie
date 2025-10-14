package com.group.mobileparkingchain.model

data class PaymentInfo(
    val spotId: String,
    val duration: Int,
    val startTime: Long,
    val total: Double
)