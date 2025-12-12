package com.group.mobileparkingchain.features.parking.data.repository

import com.group.mobileparkingchain.features.parking.data.model.Booking
import com.group.mobileparkingchain.features.parking.data.model.BookingHistoryRequest
import com.group.mobileparkingchain.features.parking.data.model.BookingResponse
import com.group.mobileparkingchain.features.parking.data.model.BookingsData
import com.group.mobileparkingchain.features.parking.data.model.BookingStatus
import com.group.mobileparkingchain.features.parking.data.model.CreateBookingRequest
import com.group.mobileparkingchain.features.parking.data.model.ParkingSpot
import com.group.mobileparkingchain.features.parking.data.model.ParkingStats
import com.group.mobileparkingchain.features.parking.data.model.SpotType
import com.group.mobileparkingchain.features.parking.data.model.UpdateBookingStatusRequest
import com.group.mobileparkingchain.features.parking.data.remote.ParkingApiService
import com.group.mobileparkingchain.features.parking.domain.repository.IParkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ParkingRepository(
    private val parkingApiService: ParkingApiService
) : IParkingRepository {

    override suspend fun getAllSpots(): Result<List<ParkingSpot>> = safeApiCall {
        parkingApiService.getAllSpots()
    }.mapCatching { it.data.spots }

    override suspend fun getAvailableSpots(): Result<List<ParkingSpot>> = safeApiCall {
        parkingApiService.getAvailableSpots()
    }.mapCatching { it.data.spots }

    override suspend fun getSpotsByType(type: SpotType): Result<List<ParkingSpot>> = safeApiCall {
        parkingApiService.getSpotsByType(type)
    }.mapCatching { it.data.spots }

    override suspend fun getSpotById(spotId: String): Result<ParkingSpot> = safeApiCall {
        parkingApiService.getSpotById(spotId)
    }

    override suspend fun getStatistics(): Result<ParkingStats> = safeApiCall {
        parkingApiService.getStatistics()
    }

    override suspend fun createBooking(
        spotId: String, 
        startTime: String?, 
        endTime: String?, 
        durationHours: Double?,
        paymentMethod: String,
        currency: String
    ): Result<BookingResponse> = safeApiCall {
        parkingApiService.createBooking(
            CreateBookingRequest(
                spotId = spotId, 
                startTime = startTime, 
                endTime = endTime, 
                durationHours = durationHours,
                paymentMethod = paymentMethod,
                currency = currency
            )
        )
    }.mapCatching { it.data }

    override suspend fun getUserBookings(request: BookingHistoryRequest): Result<BookingsData> = safeApiCall {
        parkingApiService.getUserBookings(request)
    }.mapCatching { it.data }

    override suspend fun getActiveBooking(): Result<Booking> = safeApiCall {
        parkingApiService.getActiveBooking()
    }

    override suspend fun getBookingById(bookingId: String): Result<Booking> = safeApiCall {
        parkingApiService.getBookingById(bookingId)
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Booking> = safeApiCall {
        parkingApiService.updateBookingStatus(bookingId, UpdateBookingStatusRequest(status))
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    // Parse error body to extract backend message
                    val errorMessage = try {
                        response.errorBody()?.string()?.let { errorBody ->
                            // Parse JSON to extract message field
                            org.json.JSONObject(errorBody).optString("message", "Unknown error")
                        } ?: response.message() ?: "Unknown error"
                    } catch (e: Exception) {
                        response.message() ?: "Unknown error"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                // Parse error body from HttpException
                val errorMessage = try {
                    e.response()?.errorBody()?.string()?.let { errorBody ->
                        org.json.JSONObject(errorBody).optString("message", e.message())
                    } ?: e.message()
                } catch (ex: Exception) {
                    e.message()
                }
                Result.failure(Exception(errorMessage))
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
