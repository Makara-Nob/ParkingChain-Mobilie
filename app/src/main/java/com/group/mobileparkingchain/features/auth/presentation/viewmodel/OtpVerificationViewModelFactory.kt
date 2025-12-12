package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group.mobileparkingchain.features.auth.domain.VerifyEmailUseCase

class OtpVerificationViewModelFactory(
    private val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtpVerificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OtpVerificationViewModel(verifyEmailUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
