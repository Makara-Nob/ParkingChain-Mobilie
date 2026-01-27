package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository
import com.group.mobileparkingchain.features.auth.di.AuthModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class ResetStep {
    REQUEST_OTP,
    VERIFY_OTP,
    RESET_PASSWORD,
    SUCCESS
}

class PasswordResetViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val uiState: StateFlow<PasswordResetState> = _uiState

    private val _currentStep = MutableStateFlow(ResetStep.REQUEST_OTP)
    val currentStep: StateFlow<ResetStep> = _currentStep

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp
    private var verifiedOtp: String? = null

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onOtpChange(newOtp: String) {
        _otp.value = newOtp
    }

    fun requestOtp() {
        if (_email.value.isBlank()) {
            _uiState.value = PasswordResetState.Error("Email is required")
            return
        }
        viewModelScope.launch {
            _uiState.value = PasswordResetState.Loading
            val result = authRepository.requestPasswordReset(_email.value.trim())
            if (result.isSuccess) {
                _uiState.value = PasswordResetState.Success("OTP sent to your email")
                _currentStep.value = ResetStep.VERIFY_OTP
            } else {
                _uiState.value = PasswordResetState.Error(result.exceptionOrNull()?.message ?: "Failed to send OTP")
            }
        }
    }

    fun verifyOtp() {
        if (_otp.value.isBlank()) {
            _uiState.value = PasswordResetState.Error("OTP is required")
            return
        }
        viewModelScope.launch {
            _uiState.value = PasswordResetState.Loading
            val result = authRepository.verifyResetOtp(_email.value.trim(), _otp.value)
            if (result.isSuccess) {
                verifiedOtp = _otp.value
                _uiState.value = PasswordResetState.Success("OTP verified")
                _currentStep.value = ResetStep.RESET_PASSWORD
            } else {
                _uiState.value = PasswordResetState.Error(result.exceptionOrNull()?.message ?: "Invalid OTP")
            }
        }
    }

    fun resetPassword(newPassword: String) {
        if (newPassword.isBlank()) {
            _uiState.value = PasswordResetState.Error("New password is required")
            return
        }
        val otpToUse = verifiedOtp ?: _otp.value
        if (otpToUse.isBlank()) {
            _uiState.value = PasswordResetState.Error("OTP missing. Please verify again.")
            return
        }
        viewModelScope.launch {
            _uiState.value = PasswordResetState.Loading
            val result = authRepository.resetPassword(_email.value.trim(), otpToUse, newPassword)
            if (result.isSuccess) {
                _uiState.value = PasswordResetState.Success("Password reset successfully")
                _currentStep.value = ResetStep.SUCCESS
            } else {
                _uiState.value = PasswordResetState.Error(result.exceptionOrNull()?.message ?: "Failed to reset password")
            }
        }
    }

    fun clearState() {
        _uiState.value = PasswordResetState.Idle
    }
}

sealed class PasswordResetState {
    object Idle : PasswordResetState()
    object Loading : PasswordResetState()
    data class Success(val message: String) : PasswordResetState()
    data class Error(val message: String) : PasswordResetState()
}

class PasswordResetViewModelFactory(private val authModule: AuthModule) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordResetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordResetViewModel(authModule.authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
