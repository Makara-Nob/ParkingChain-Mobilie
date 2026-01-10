package com.group.mobileparkingchain.features.parking.data.model

enum class SpotType { CAR, MOTORCYCLE }

enum class BookingStatus { RESERVED, ACTIVE, COMPLETED, CANCELLED }

data class ParkingSpot(
    val id: String,
    val spotName: String,
    val spotType: SpotType,
    val isAvailable: Boolean,
    val pricePerHour: String,
    val lastUpdated: String,
    val createdAt: String
)

data class Booking(
    val id: String,
    val userId: String,
    val spotId: String,
    val startTime: String,
    val endTime: String?,
    val durationHours: Double?,
    val totalPrice: Double?,
    val currency: String? = "USD", // Default to USD if missing
    val status: BookingStatus,
    val createdAt: String,
    val updatedAt: String,
    val spot: ParkingSpot?
)

data class CreateBookingRequest(
    val spotId: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val durationHours: Double? = null,
    val paymentMethod: String = "payway",
    val currency: String = "KHR"
)

data class UpdateBookingStatusRequest(
    val status: BookingStatus
)

data class ParkingStats(
    val totalSpots: Int,
    val availableSpots: Int,
    val occupiedSpots: Int
    // Add more fields if needed based on actual API response
)

data class PaymentDetails(
    val paymentId: String,
    val qrImage: String?,
    val qrString: String?,
    val deeplinkUrl: String?,
    val md5: String? = null,
    val amount: Double,
    val currency: String,
    val status: String,
    val createdAt: String? = null
)

// API Response Wrappers
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

data class SpotsData(
    val spots: List<ParkingSpot>,
    val count: Int? = null
)

data class BookingResponse(
    val booking: Booking,
    val payment: PaymentDetails?
)

data class BookingsData(
    val bookings: List<Booking>,
    val pagination: Pagination? = null  // Made optional for backward compatibility
)

// Pagination Models
data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasMore: Boolean
)

data class BookingHistoryRequest(
    val page: Int = 1,
    val limit: Int = 20,
    val status: String? = null,
    val sortField: String = "createdAt",
    val sortOrder: String = "desc"
)
