package com.group.mobileparkingchain.features.payment.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.data.repository.PaymentRepository
import com.group.mobileparkingchain.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PaymentRepository(RetrofitInstance.paymentApi)

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    fun initiatePayment(
        bookingId: String,
        amount: Double,
        currency: String = "USD",
        paymentMethod: String = "khqr"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _paymentState.value = PaymentState.Loading
            
            val result = repository.createPayment(
                bookingId = bookingId,
                amount = amount,
                currency = currency,
                description = "Parking Fee",
                paymentMethod = paymentMethod
            )

            result.onSuccess { response ->
                _paymentState.value = PaymentState.Success(response)
                // KHQR flow: No deeplink opening, QR is displayed in app
            }.onFailure { error ->
                _paymentState.value = PaymentState.Error(error.message ?: "Payment failed")
            }
        }
    }

    fun fetchUserPayments(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserTransactions(userId)
                .onSuccess { list ->
                    _payments.value = list
                }
                .onFailure {
                    // Handle error if needed
                }
        }
    }
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val response: Payment) : PaymentState()
    data class Error(val message: String) : PaymentState()
}
