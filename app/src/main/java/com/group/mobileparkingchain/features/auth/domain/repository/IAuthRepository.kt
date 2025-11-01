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
}

