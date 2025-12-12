package com.group.mobileparkingchain.features.profile.domain.repository

import com.group.mobileparkingchain.features.auth.domain.model.User
import okhttp3.MultipartBody

interface IProfileRepository {
    suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        phone: String?
    ): Result<User>

    suspend fun uploadProfileImage(imagePart: MultipartBody.Part): Result<User>
}
