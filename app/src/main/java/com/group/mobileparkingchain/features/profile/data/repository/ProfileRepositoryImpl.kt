package com.group.mobileparkingchain.features.profile.data.repository

import com.group.mobileparkingchain.features.auth.data.model.UpdateProfileRequest
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.data.remote.AuthApiService
import com.group.mobileparkingchain.features.profile.domain.repository.IProfileRepository
import okhttp3.MultipartBody

class ProfileRepositoryImpl(
    private val authApiService: AuthApiService
) : IProfileRepository {

    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        phone: String?
    ): Result<User> {
        return try {
            val request = UpdateProfileRequest(firstName, lastName, phone)
            val response = authApiService.updateProfile(request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.data.user.toDomain()
                Result.success(user)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to update profile"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(imagePart: MultipartBody.Part): Result<User> {
        return try {
            val response = authApiService.uploadProfileImage(imagePart)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.data.user.toDomain()
                Result.success(user)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to upload image"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // New base64 upload method
    suspend fun uploadProfileImageBase64(base64Image: String): Result<User> {
        return try {
            val request = com.group.mobileparkingchain.features.auth.data.model.UploadImageBase64Request(base64Image)
            val response = authApiService.uploadProfileImageBase64(request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.data.user.toDomain()
                Result.success(user)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to upload image"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun com.group.mobileparkingchain.features.auth.data.model.User.toDomain(): User {
        return User(
            id = this.id,
            fullName = "${this.firstName} ${this.lastName}",
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email,
            phone = this.phone ?: "",
            profileImage = this.profileImage
        )
    }
}
