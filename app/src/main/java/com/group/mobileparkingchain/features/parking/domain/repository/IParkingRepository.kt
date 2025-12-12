package com.group.mobileparkingchain.features.parking.domain.repository

import com.group.mobileparkingchain.features.parking.data.model.Booking
import com.group.mobileparkingchain.features.parking.data.model.BookingHistoryRequest
import com.group.mobileparkingchain.features.parking.data.model.BookingResponse
import com.group.mobileparkingchain.features.parking.data.model.BookingsData
import com.group.mobileparkingchain.features.parking.data.model.BookingStatus
import com.group.mobileparkingchain.features.parking.data.model.ParkingStats
import com.group.mobileparkingchain.features.parking.data.model.ParkingSpot
import com.group.mobileparkingchain.features.parking.data.model.SpotType

interface IParkingRepository {
    suspend fun getAllSpots(): Result<List<ParkingSpot>>
    suspend fun getAvailableSpots(): Result<List<ParkingSpot>>
    suspend fun getSpotsByType(type: SpotType): Result<List<ParkingSpot>>
    suspend fun getSpotById(spotId: String): Result<ParkingSpot>
    suspend fun getStatistics(): Result<ParkingStats>
    suspend fun createBooking(
        spotId: String, 
        startTime: String? = null, 
        endTime: String? = null, 
        durationHours: Double? = null,
        paymentMethod: String = "khqr",
        currency: String = "KHR"
    ): Result<BookingResponse>
    suspend fun getUserBookings(request: BookingHistoryRequest = BookingHistoryRequest()): Result<BookingsData>
    suspend fun getActiveBooking(): Result<Booking>
    suspend fun getBookingById(bookingId: String): Result<Booking>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Booking>
}
