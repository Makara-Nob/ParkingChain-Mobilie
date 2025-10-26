package com.group.mobileparkingchain.features.auth.data.remote.models

data class UserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
)
