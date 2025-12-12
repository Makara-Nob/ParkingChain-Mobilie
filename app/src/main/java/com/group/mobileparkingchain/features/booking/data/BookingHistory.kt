package com.group.mobileparkingchain.features.booking.data

enum class BookingStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

data class BookingHistory(
    val id: String,
    val spotId: String,
    val spotLocation: String,
    val spotType: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Int, // in hours
    val totalPrice: Double,
    val status: BookingStatus,
    val paymentMethod: String,
    val bookingDate: Long
)
