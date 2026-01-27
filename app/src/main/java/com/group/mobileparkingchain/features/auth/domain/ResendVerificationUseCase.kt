package com.group.mobileparkingchain.features.auth.domain

import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository

class ResendVerificationUseCase(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email is required"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        return authRepository.resendVerification(email)
    }
}
