package com.group.mobileparkingchain.features.booking.data.remote

import com.group.mobileparkingchain.features.booking.data.model.BookingRequest
import com.group.mobileparkingchain.features.booking.data.model.BookingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApiService {
    @POST("bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<BookingResponse>
}
