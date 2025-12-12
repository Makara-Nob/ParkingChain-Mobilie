package com.group.mobileparkingchain.features.auth.domain

import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository

class GetCurrentUserUseCase(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getCurrentUser()
    }
}
