package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group.mobileparkingchain.features.auth.data.repository.AuthRepository
import com.group.mobileparkingchain.features.auth.domain.GetCurrentUserUseCase
import com.group.mobileparkingchain.network.RetrofitInstance
import com.group.mobileparkingchain.network.datastore.TokenDataStore

/**
 * Factory for creating SplashViewModel with required dependencies.
 */
class SplashViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            val tokenDataStore = TokenDataStore(context)
            val authRepository = AuthRepository(
                RetrofitInstance.authApi,
                tokenDataStore
            )
            val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
            
            return SplashViewModel(tokenDataStore, getCurrentUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
