package com.group.mobileparkingchain.features.auth.domain.model

data class User(
    val id: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val profileImage: String? = null
)

