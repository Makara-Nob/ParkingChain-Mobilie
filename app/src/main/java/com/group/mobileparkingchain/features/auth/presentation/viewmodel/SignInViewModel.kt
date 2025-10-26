package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.core.Resource
import com.group.mobileparkingchain.features.auth.data.remote.models.LoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginData>?>(null)
    val loginState: StateFlow<Resource<LoginData>?> = _loginState.asStateFlow()

    fun signIn(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            var result = repository.login(email, password)
            _loginState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun clearState() {
        _loginState.value = null
    }
}