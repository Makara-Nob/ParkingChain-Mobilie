package com.group.mobileparkingchain.features.profile.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group.mobileparkingchain.features.auth.data.repository.AuthRepository
import com.group.mobileparkingchain.features.auth.domain.GetCurrentUserUseCase
import com.group.mobileparkingchain.features.profile.data.repository.ProfileRepositoryImpl
import com.group.mobileparkingchain.features.profile.domain.usecase.UpdateProfileUseCase
import com.group.mobileparkingchain.features.profile.domain.usecase.UploadProfileImageUseCase
import com.group.mobileparkingchain.network.RetrofitInstance
import com.group.mobileparkingchain.network.datastore.TokenDataStore

class ProfileViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val tokenDataStore = TokenDataStore(context)
            val authService = RetrofitInstance.authApi
            val authRepository = AuthRepository(authService, tokenDataStore)
            
            // Create profile repository
            val profileRepository = ProfileRepositoryImpl(authService)
            
            // Create use cases
            val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
            val updateProfileUseCase = UpdateProfileUseCase(profileRepository)
            val uploadProfileImageUseCase = UploadProfileImageUseCase(profileRepository, context)
            
            return ProfileViewModel(
                getCurrentUserUseCase,
                updateProfileUseCase,
                uploadProfileImageUseCase,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
