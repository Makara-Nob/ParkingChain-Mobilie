package com.group.mobileparkingchain.features.auth.domain

import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository

class RegisterUseCase(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        // Validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || 
            password.isBlank() || confirmPassword.isBlank()) {
            return Result.failure(Exception("All fields are required"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }

        // Call repository
        return authRepository.register(firstName, lastName, email, password)
    }
}

