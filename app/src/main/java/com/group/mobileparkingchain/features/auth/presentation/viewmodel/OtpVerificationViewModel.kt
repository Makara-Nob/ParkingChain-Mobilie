package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import com.group.mobileparkingchain.core.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.auth.domain.ResendVerificationUseCase
import com.group.mobileparkingchain.features.auth.domain.VerifyEmailUseCase
import com.group.mobileparkingchain.features.auth.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OtpVerificationViewModel(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val resendVerificationUseCase: ResendVerificationUseCase
) : ViewModel() {

    private val _verificationState = MutableStateFlow<Resource<User>>(Resource.Idle)
    val verificationState: StateFlow<Resource<User>> = _verificationState.asStateFlow()

    private val _resendState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val resendState: StateFlow<Resource<String>> = _resendState.asStateFlow()

    fun verifyEmail(email: String, otp: String) {
        viewModelScope.launch {
            _verificationState.value = Resource.Loading
            val result = verifyEmailUseCase(email, otp)
            _verificationState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Verification failed")
            }
        }
    }

    fun resendVerification(email: String) {
        viewModelScope.launch {
            _resendState.value = Resource.Loading
            val result = resendVerificationUseCase(email)
            _resendState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull() ?: "OTP resent")
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to resend OTP")
            }
        }
    }

    fun clearState() {
        _verificationState.value = Resource.Idle
    }

    fun clearResendState() {
        _resendState.value = Resource.Idle
    }
}
