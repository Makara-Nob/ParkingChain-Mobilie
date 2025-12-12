package com.group.mobileparkingchain.features.profile.presentation.viewmodel

import Resource
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.domain.GetCurrentUserUseCase
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository
import com.group.mobileparkingchain.features.profile.data.UserProfile
import com.group.mobileparkingchain.features.profile.domain.usecase.UpdateProfileUseCase
import com.group.mobileparkingchain.features.profile.domain.usecase.UploadProfileImageUseCase
import com.group.mobileparkingchain.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<UserProfile>>(Resource.Loading)
    val profileState: StateFlow<Resource<UserProfile>> = _profileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Resource<User>?>(null)
    val updateProfileState: StateFlow<Resource<User>?> = _updateProfileState.asStateFlow()

    private val _uploadImageState = MutableStateFlow<Resource<User>?>(null)
    val uploadImageState: StateFlow<Resource<User>?> = _uploadImageState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading
            val result = getCurrentUserUseCase()
            _profileState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                Resource.Success(user.toUserProfile())
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(firstName: String?, lastName: String?, phone: String?) {
        viewModelScope.launch {
            _updateProfileState.value = Resource.Loading
            val result = updateProfileUseCase(firstName, lastName, phone)
            _updateProfileState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                // Update the main profile state as well
                _profileState.value = Resource.Success(user.toUserProfile())
                Resource.Success(user)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to update profile")
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _uploadImageState.value = Resource.Loading
            val result = uploadProfileImageUseCase(imageUri)
            _uploadImageState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                // Update the main profile state as well
                _profileState.value = Resource.Success(user.toUserProfile())
                Resource.Success(user)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to upload image")
            }
        }
    }

    fun resetUpdateState() {
        _updateProfileState.value = null
    }

    fun resetUploadState() {
        _uploadImageState.value = null
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun User.toUserProfile(): UserProfile {
        return UserProfile(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phone,
            profileImageUrl = ImageUtils.getFullImageUrl(profileImage)
        )
    }
}
