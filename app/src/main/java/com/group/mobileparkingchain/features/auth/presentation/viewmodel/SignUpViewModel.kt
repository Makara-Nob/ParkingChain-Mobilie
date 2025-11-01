package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import com.group.mobileparkingchain.core.Resource
import com.group.mobileparkingchain.features.auth.domain.RegisterUseCase
import com.group.mobileparkingchain.features.auth.domain.model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<User>>(Resource.Idle)
    val registerState: StateFlow<Resource<User>> = _registerState.asStateFlow()

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val result = registerUseCase(firstName, lastName, email, password, confirmPassword)
            _registerState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun clearState() {
        _registerState.value = Resource.Idle
    }
}
