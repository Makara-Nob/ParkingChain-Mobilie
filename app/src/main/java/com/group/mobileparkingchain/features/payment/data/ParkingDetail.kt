package com.group.mobileparkingchain.features.payment.data

data class ParkingDetail(
    val id: String,
    val location: String?,
    val type: String,
    val lastUpdated: String?,
    val pricePerHour: Double?,
    val status: String
)
