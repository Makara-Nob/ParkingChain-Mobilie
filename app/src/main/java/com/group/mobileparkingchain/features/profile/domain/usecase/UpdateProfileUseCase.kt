package com.group.mobileparkingchain.features.profile.domain.usecase

import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.profile.domain.repository.IProfileRepository

class UpdateProfileUseCase(
    private val profileRepository: IProfileRepository
) {
    suspend operator fun invoke(
        firstName: String?,
        lastName: String?,
        phone: String?
    ): Result<User> {
        // Validate inputs
        if (firstName != null && (firstName.length < 2 || firstName.length > 50)) {
            return Result.failure(Exception("First name must be between 2 and 50 characters"))
        }
        
        if (lastName != null && (lastName.length < 2 || lastName.length > 50)) {
            return Result.failure(Exception("Last name must be between 2 and 50 characters"))
        }
        
        if (phone != null && phone.isNotBlank()) {
            val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
            if (cleanPhone.length < 9 || cleanPhone.length > 15) {
                return Result.failure(Exception("Phone number must be between 9 and 15 digits"))
            }
        }
        
        // At least one field must be provided
        if (firstName == null && lastName == null && phone == null) {
            return Result.failure(Exception("At least one field is required for update"))
        }
        
        return profileRepository.updateProfile(firstName, lastName, phone)
    }
}
