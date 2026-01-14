package com.group.mobileparkingchain.features.auth.data.model

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isVerified: Boolean,
    val phone: String?,
    val profileImage: String?,
    val createdAt: String,
    val updatedAt: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

data class BasicResponse(
    val success: Boolean,
    val message: String
)

data class AuthData(
    val user: User,
    val token: String,
    val refreshToken: String? = null
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class VerifyEmailRequest(
    val email: String,
    val otp: String
)

data class ResendEmailRequest(
    val email: String
)

data class PasswordResetRequestRequest(
    val email: String
)

data class VerifyResetRequest(
    val email: String,
    val otp: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class TokenVerifyRequest(
    val token: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val success: Boolean,
    val message: String,
    val data: RefreshTokenData?
)

data class RefreshTokenData(
    val token: String
)

data class LogoutRequest(
    val refreshToken: String
)

// Profile Update Models
data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
)

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: ProfileData
)

data class ProfileData(
    val user: User
)

// Base64 Image Upload
data class UploadImageBase64Request(
    val image: String  // Base64 string with or without data URL prefix
)
