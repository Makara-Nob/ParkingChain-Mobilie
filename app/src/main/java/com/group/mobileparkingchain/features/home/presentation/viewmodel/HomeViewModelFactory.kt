package com.group.mobileparkingchain.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group.mobileparkingchain.features.parking.domain.repository.IParkingRepository

class HomeViewModelFactory(
    private val parkingRepository: IParkingRepository,
    private val paymentRepository: com.group.mobileparkingchain.features.payment.data.repository.PaymentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(parkingRepository, paymentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
