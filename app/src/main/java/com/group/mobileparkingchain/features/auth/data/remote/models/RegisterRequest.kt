package com.group.mobileparkingchain.features.auth.data.remote.models

data class RegisterRequest(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = ""
)
