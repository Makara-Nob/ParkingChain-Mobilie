package com.group.mobileparkingchain.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
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
                        id = "P-${apiSpot.id}", // Add P- prefix to match UI design
                        dbId = apiSpot.id,
                        type = when (apiSpot.spotType) {
                            SpotType.CAR -> "Car"
                            SpotType.MOTORCYCLE -> "Motorcycle"
                        },
                        status = if (apiSpot.isAvailable) ParkingStatus.AVAILABLE else ParkingStatus.OCCUPIED
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
                // Extract separated booking and payment
                val booking = bookingResponse.booking
                val payment = bookingResponse.payment
                
                // Automatically handle payment state
                payment?.let { paymentData ->
                    _paymentState.value = PaymentState.PaymentCreated(
                        deeplink = paymentData.deeplinkUrl,
                        qrString = paymentData.qrString,
                        qrImage = paymentData.qrImage,
                        paymentId = paymentData.paymentId,
                        md5 = paymentData.md5,
                        amount = paymentData.amount,
                        currency = paymentData.currency
                    )
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
            val currency: String
        ) : PaymentState()
        object PaymentConfirmed : PaymentState()
        data class Error(val message: String) : PaymentState()
    }

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    // Deprecated: merged into createBooking
    fun createPayment(bookingId: String, amount: Double) {
        // No-op or legacy support if needed
    }

    fun confirmPayment(paymentId: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            
            // Wait for 2 seconds as recommended
            kotlinx.coroutines.delay(2000)
            
            val result = paymentRepository.confirmPayment(paymentId)
            
            result.onSuccess { confirmed ->
                if (confirmed) {
                    _paymentState.value = PaymentState.PaymentConfirmed
                    // Refresh active booking or spots if needed
                    fetchParkingSpots() 
                } else {
                    _paymentState.value = PaymentState.Error("Payment confirmation failed")
                }
            }.onFailure { exception ->
                _paymentState.value = PaymentState.Error(exception.message ?: "Payment confirmation failed")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
}
