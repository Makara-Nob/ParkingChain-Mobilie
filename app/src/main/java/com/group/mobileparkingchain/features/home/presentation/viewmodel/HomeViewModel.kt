package com.group.mobileparkingchain.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.features.home.data.ParkingSpot
import com.group.mobileparkingchain.features.parking.data.model.SpotType
import com.group.mobileparkingchain.features.parking.domain.repository.IParkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val parkingRepository: IParkingRepository,
    private val paymentRepository: com.group.mobileparkingchain.features.payment.data.repository.PaymentRepository
) : ViewModel() {

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchParkingSpots()
    }

    fun fetchParkingSpots(filterType: com.group.mobileparkingchain.enumuration.FilterType = com.group.mobileparkingchain.enumuration.FilterType.ALL) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = when (filterType) {
                com.group.mobileparkingchain.enumuration.FilterType.ALL -> parkingRepository.getAllSpots()
                com.group.mobileparkingchain.enumuration.FilterType.AVAILABLE -> parkingRepository.getAvailableSpots()
                com.group.mobileparkingchain.enumuration.FilterType.OCCUPIED -> parkingRepository.getAllSpots() // Will filter locally
                com.group.mobileparkingchain.enumuration.FilterType.RESERVED -> parkingRepository.getAllSpots() // Will filter locally
                com.group.mobileparkingchain.enumuration.FilterType.CAR -> parkingRepository.getSpotsByType(SpotType.CAR)
                com.group.mobileparkingchain.enumuration.FilterType.MOTORCYCLE -> parkingRepository.getSpotsByType(SpotType.MOTORCYCLE)
                com.group.mobileparkingchain.enumuration.FilterType.LEV -> parkingRepository.getAllSpots() // No LEV endpoint, fallback to all
            }
            
            result.onSuccess { apiSpots ->
                var uiSpots = apiSpots.map { apiSpot ->
                    ParkingSpot(
                        id = apiSpot.spotName.ifBlank { apiSpot.id },
                        dbId = apiSpot.id,
                        type = when (apiSpot.spotType) {
                            SpotType.CAR -> "Car"
                            SpotType.MOTORCYCLE -> "Motorcycle"
                        },
                        status = if (apiSpot.isAvailable) ParkingStatus.AVAILABLE else ParkingStatus.OCCUPIED,
                        pricePerHour = apiSpot.pricePerHour.toDoubleOrNull(),
                        lastUpdated = apiSpot.lastUpdated
                    )
                }

                // Apply local filtering for status
                if (filterType == com.group.mobileparkingchain.enumuration.FilterType.OCCUPIED) {
                    uiSpots = uiSpots.filter { it.status == ParkingStatus.OCCUPIED }
                } else if (filterType == com.group.mobileparkingchain.enumuration.FilterType.RESERVED) {
                    uiSpots = uiSpots.filter { it.status == ParkingStatus.RESERVED }
                }

                _parkingSpots.value = uiSpots
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to fetch parking spots"
                // Keep existing data or show error state
            }
            
            _isLoading.value = false
        }
    }
    
    // Function to handle local updates (optimistic UI updates)
    fun updateSpotStatus(spotId: String, status: ParkingStatus) {
        val currentList = _parkingSpots.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == spotId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = status)
            _parkingSpots.value = currentList
        }
    }
    
    // Booking creation
    private val _bookingResult = MutableStateFlow<Result<com.group.mobileparkingchain.features.parking.data.model.BookingResponse>?>(null)
    val bookingResult: StateFlow<Result<com.group.mobileparkingchain.features.parking.data.model.BookingResponse>?> = _bookingResult.asStateFlow()
    
    fun createBooking(spotId: String, startTime: String?, durationHours: Double, paymentMethod: String, currency: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = parkingRepository.createBooking(spotId, startTime, null, durationHours, paymentMethod, currency)
            _bookingResult.value = result

            result.onSuccess { bookingResponse ->
                // Booking created (RESERVED). Now create Bakong/KHQR payment separately.
                val booking = bookingResponse.booking
                val amount = booking.totalPrice ?: 0.0
                val currencyToUse = booking.currency ?: currency
                try {
                    val payResult = paymentRepository.createPayment(
                        bookingId = booking.id,
                        amount = amount,
                        currency = currencyToUse,
                        description = "Parking payment",
                        paymentMethod = com.group.mobileparkingchain.features.payment.data.model.PaymentMethod.KHQR
                    )

                    payResult.onSuccess { payment ->
                        // Map payment model to UI state. qrImage may be null depending on API.
                        _paymentState.value = PaymentState.PaymentCreated(
                            deeplink = payment.deeplinkUrl,
                            qrString = payment.qrString,
                            qrImage = payment.qrImage,
                            paymentId = payment.paymentId,
                            md5 = payment.md5 ?: "",
                            amount = payment.amount,
                            currency = payment.currency,
                            expiresAt = payment.expiresAt
                        )
                        _paymentStatus.value = normalizePaymentStatus(payment.status)
                    }.onFailure { ex ->
                        _error.value = ex.message ?: "Payment init failed"
                    }
                } catch (e: Exception) {
                    _error.value = e.message ?: "Payment init failed"
                }

            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to create booking"
            }

            _isLoading.value = false
        }
    }
    
    fun clearBookingResult() {
        _bookingResult.value = null
    }

    // Payment Flow
    sealed class PaymentState {
        object Idle : PaymentState()
        object Loading : PaymentState()
        data class PaymentCreated(
            val deeplink: String?,
            val qrString: String?,
            val qrImage: String?,
            val paymentId: String,
            val md5: String,
            val amount: Double,
            val currency: String,
            val expiresAt: String?
        ) : PaymentState()
        object PaymentConfirmed : PaymentState()
        data class Error(val message: String) : PaymentState()
    }

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _paymentStatus = MutableStateFlow("PENDING")
    val paymentStatus: StateFlow<String> = _paymentStatus.asStateFlow()

    private fun normalizePaymentStatus(rawStatus: String): String {
        return rawStatus.trim().uppercase().let { status ->
            if (status == "RESERVED") "PENDING" else status
        }
    }

    // Deprecated: merged into createBooking
    fun createPayment(bookingId: String, amount: Double) {
        // No-op or legacy support if needed
    }

    fun confirmPayment(paymentId: String) {
        // For KHQR flow: poll GET /payments/payway/{id}/status until PAID/COMPLETED or timeout.
        viewModelScope.launch {
            Log.d("PaymentPoll", "Start polling paymentId=$paymentId")
            _paymentState.value = PaymentState.Loading

            val maxAttempts = 40
            var attempt = 0
            val delayMs = 3000L

            while (attempt < maxAttempts) {
                try {
                    val statusRes = paymentRepository.getPaymentStatus(paymentId)
                    statusRes.onSuccess { payment ->
                        val st = normalizePaymentStatus(payment.status)
                        _paymentStatus.value = st
                        Log.d("PaymentPoll", "Status=${'$'}st for paymentId=$paymentId")
                        if (st == "PAID" || st == "COMPLETED") {
                            _paymentState.value = PaymentState.PaymentConfirmed
                            fetchParkingSpots()
                            return@launch
                        } else if (st == "CANCELLED" || st == "EXPIRED") {
                            _paymentState.value = PaymentState.Error("Payment cancelled.")
                            return@launch
                        } else if (st == "FAILED") {
                            _paymentState.value = PaymentState.Error("Payment failed.")
                            return@launch
                        }
                    }.onFailure {
                        Log.d("PaymentPoll", "Status check failed for paymentId=$paymentId: ${'$'}{it.message}")
                        // ignore and continue polling
                    }
                } catch (e: Exception) {
                    Log.d("PaymentPoll", "Polling exception for paymentId=$paymentId: ${'$'}{e.message}")
                    // continue/try again
                }
                attempt++
                kotlinx.coroutines.delay(delayMs)
            }

            _paymentState.value = PaymentState.Error("Payment cancelled.")
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
        _paymentStatus.value = "PENDING"
    }

    fun cancelPayment(paymentId: String) {
        viewModelScope.launch {
            try {
                val result = paymentRepository.cancelPayment(paymentId)
                result.onSuccess {
                    _paymentStatus.value = "CANCELLED"
                    _paymentState.value = PaymentState.Error("Payment cancelled.")
                }.onFailure { ex ->
                    _paymentState.value = PaymentState.Error(ex.message ?: "Cancel failed")
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(e.message ?: "Cancel failed")
            }
        }
    }
}
