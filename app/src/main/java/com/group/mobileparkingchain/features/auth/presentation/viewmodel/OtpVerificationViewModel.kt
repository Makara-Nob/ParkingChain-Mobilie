package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.auth.domain.VerifyEmailUseCase
import com.group.mobileparkingchain.features.auth.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OtpVerificationViewModel(
    private val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _verificationState = MutableStateFlow<Resource<User>>(Resource.Idle)
    val verificationState: StateFlow<Resource<User>> = _verificationState.asStateFlow()

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

    fun clearState() {
        _verificationState.value = Resource.Idle
    }
}
