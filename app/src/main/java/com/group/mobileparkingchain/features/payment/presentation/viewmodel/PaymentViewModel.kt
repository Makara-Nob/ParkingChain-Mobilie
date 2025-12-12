package com.group.mobileparkingchain.features.payment.presentation.viewmodel

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.data.model.PaymentMethod
import com.group.mobileparkingchain.features.payment.data.repository.PaymentRepository
import com.group.mobileparkingchain.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PaymentRepository(RetrofitInstance.paymentApi)

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    fun initiatePayment(
        bookingId: String,
        amount: Double,
        currency: String = "USD",
        paymentMethod: String = PaymentMethod.ABA
    ) {
        viewModelScope.launch {
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
                
                val deepLink = response.deeplinkUrl
                
                if (deepLink.isNotEmpty()) {
                    // Check if ABA app is installed first to prevent app switching/flickering on emulator
                    val abaPackage = "com.paygo24.ibank"
                    if (isPackageInstalled(abaPackage)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        try {
                            getApplication<Application>().startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            android.widget.Toast.makeText(getApplication(), "ABA App not installed.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // ABA Mobile app not installed
                        // For testing purposes, we treat this as success so the user can see the receipt flow
                        // In production, you might want to show a dialog with the QR code or fallback
                        android.widget.Toast.makeText(getApplication(), "ABA App not installed. Simulating success.", android.widget.Toast.LENGTH_LONG).show()
                    }
                } else {
                    // If no deep link, it might be a QR code only response or success without deep link
                    // For now, we still treat it as success
                }
            }.onFailure { error ->
                _paymentState.value = PaymentState.Error(error.message ?: "Payment failed")
            }
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            getApplication<Application>().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val response: Payment) : PaymentState()
    data class Error(val message: String) : PaymentState()
}
