package com.group.mobileparkingchain.features.profile.data

data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String? = null
)