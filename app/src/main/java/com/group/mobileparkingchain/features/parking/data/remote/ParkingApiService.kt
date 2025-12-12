package com.group.mobileparkingchain.features.parking.data.remote

import com.group.mobileparkingchain.features.parking.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ParkingApiService {
    // Parking Spots
    @GET("parking/spots")
    suspend fun getAllSpots(): Response<ApiResponse<SpotsData>>

    @GET("parking/spots/available")
    suspend fun getAvailableSpots(): Response<ApiResponse<SpotsData>>

    @GET("parking/spots/type/{type}")
    suspend fun getSpotsByType(@Path("type") type: SpotType): Response<ApiResponse<SpotsData>>

    @GET("parking/spots/{spotId}")
    suspend fun getSpotById(@Path("spotId") spotId: String): Response<ParkingSpot>

    @GET("parking/statistics")
    suspend fun getStatistics(): Response<ParkingStats>

    // Bookings
    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<ApiResponse<BookingResponse>>

    @POST("bookings/me")
    suspend fun getUserBookings(@Body request: BookingHistoryRequest = BookingHistoryRequest()): Response<ApiResponse<BookingsData>>

    @GET("bookings/me/active")
    suspend fun getActiveBooking(): Response<Booking>

    @GET("bookings/{bookingId}")
    suspend fun getBookingById(@Path("bookingId") bookingId: String): Response<Booking>

    @PATCH("bookings/{bookingId}")
    suspend fun updateBookingStatus(
        @Path("bookingId") bookingId: String,
        @Body request: UpdateBookingStatusRequest
    ): Response<Booking>
}
