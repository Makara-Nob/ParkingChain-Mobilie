package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.auth.di.AuthModule
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val uiState: StateFlow<ChangePasswordState> = _uiState

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            if (currentPassword.isBlank() || newPassword.isBlank()) {
                _uiState.value = ChangePasswordState.Error("All fields are required")
                return@launch
            }
            if (newPassword.length < 8) {
                _uiState.value = ChangePasswordState.Error("Password must be at least 8 characters")
                return@launch
            }
            if (currentPassword == newPassword) {
                _uiState.value = ChangePasswordState.Error("New password must be different")
                return@launch
            }

            _uiState.value = ChangePasswordState.Loading
            val result = authRepository.changePassword(currentPassword, newPassword)
            _uiState.value = if (result.isSuccess) {
                ChangePasswordState.Success("Password changed successfully")
            } else {
                ChangePasswordState.Error(result.exceptionOrNull()?.message ?: "Failed to change password")
            }
        }
    }

    fun clearState() {
        _uiState.value = ChangePasswordState.Idle
    }
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    data class Success(val message: String) : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

class ChangePasswordViewModelFactory(private val authModule: AuthModule) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            return ChangePasswordViewModel(authModule.authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
