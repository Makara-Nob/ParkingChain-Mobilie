package com.group.mobileparkingchain.features.auth.data.remote.models

import com.group.mobileparkingchain.features.auth.domain.model.User

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class LoginData(
    val token: String,
    val user: User
)
