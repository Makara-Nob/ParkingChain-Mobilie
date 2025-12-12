package com.group.mobileparkingchain.features.booking.data.model

data class BookingRequest(
    val parkingSpotId: String,
    val startTime: String,
    val endTime: String
)
