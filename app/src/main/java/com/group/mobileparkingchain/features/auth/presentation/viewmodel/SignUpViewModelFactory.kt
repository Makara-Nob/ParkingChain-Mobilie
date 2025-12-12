package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group.mobileparkingchain.network.RetrofitInstance
import com.group.mobileparkingchain.features.auth.data.repository.AuthRepository
import com.group.mobileparkingchain.features.auth.domain.RegisterUseCase
import com.group.mobileparkingchain.network.datastore.TokenDataStore

class SignUpViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            val tokenDataStore = TokenDataStore(context)
            val authService = RetrofitInstance.authApi
            val repository = AuthRepository(authService, tokenDataStore)
            val registerUseCase = RegisterUseCase(repository)
            return SignUpViewModel(registerUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
