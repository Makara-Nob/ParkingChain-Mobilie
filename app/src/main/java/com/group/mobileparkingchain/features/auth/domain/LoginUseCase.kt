package com.group.mobileparkingchain.features.auth.domain

import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository

class LoginUseCase(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validation
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        // Call repository
        return authRepository.login(email, password)
    }
}