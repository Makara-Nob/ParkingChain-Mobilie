package com.group.mobileparkingchain.features.auth.domain.repository

import com.group.mobileparkingchain.features.auth.domain.model.User

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<User>
    suspend fun logout()
    suspend fun verifyEmail(email: String, otp: String): Result<User>
    suspend fun getCurrentUser(): Result<User>

    // Password Reset
    suspend fun requestPasswordReset(email: String): Result<Boolean>
    suspend fun verifyResetOtp(email: String, otp: String): Result<Boolean>
    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Boolean>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Boolean>
}
