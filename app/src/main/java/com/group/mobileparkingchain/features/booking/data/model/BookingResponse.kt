package com.group.mobileparkingchain.features.booking.data.model

data class BookingResponse(
    val success: Boolean,
    val data: BookingData?,
    val message: String?
)

data class BookingData(
    val bookingId: String,
    val status: String,
    val parkingSpotId: String,
    val startTime: String,
    val endTime: String,
    val totalPrice: Double
)
