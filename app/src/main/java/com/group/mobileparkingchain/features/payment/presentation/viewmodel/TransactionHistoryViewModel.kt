package com.group.mobileparkingchain.features.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.di.PaymentModule
import com.group.mobileparkingchain.features.payment.domain.repository.IPaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TransactionHistoryState {
    object Loading : TransactionHistoryState()
    data class Success(val payments: List<Payment>) : TransactionHistoryState()
    data class Error(val message: String) : TransactionHistoryState()
}

class TransactionHistoryViewModel(
    private val paymentRepository: IPaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionHistoryState>(TransactionHistoryState.Loading)
    val uiState: StateFlow<TransactionHistoryState> = _uiState

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = TransactionHistoryState.Loading
            val result = paymentRepository.getMyTransactions()
            if (result.isSuccess) {
                _uiState.value = TransactionHistoryState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = TransactionHistoryState.Error(result.exceptionOrNull()?.message ?: "Failed to load transactions")
            }
        }
    }
}

class TransactionHistoryViewModelFactory(private val paymentModule: PaymentModule) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionHistoryViewModel(paymentModule.paymentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
