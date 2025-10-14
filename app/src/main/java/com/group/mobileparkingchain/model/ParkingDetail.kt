package com.group.mobileparkingchain.model

data class ParkingDetail(
    val id: String,
    val location: String,
    val type: String,
    val lastUpdated: String,
    val pricePerHour: Double,
    val status: String
)