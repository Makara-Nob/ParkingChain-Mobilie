package com.group.mobileparkingchain.features.auth.domain

import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository

class VerifyEmailUseCase(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(email: String, otp: String): Result<Boolean> {
        if (email.isBlank() || otp.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and OTP cannot be empty"))
        }
        return repository.verifyEmail(email, otp)
    }
}
