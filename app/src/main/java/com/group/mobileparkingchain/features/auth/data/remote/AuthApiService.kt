package com.group.mobileparkingchain.features.auth.data.remote

import com.group.mobileparkingchain.features.auth.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface AuthApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("email/verify")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<AuthResponse>

    @POST("email/verify/resend")
    suspend fun resendVerification(@Body request: ResendEmailRequest): Response<AuthResponse>

    @POST("password/reset/request")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestRequest): Response<AuthResponse>

    @POST("password/reset/verify")
    suspend fun verifyResetOtp(@Body request: VerifyResetRequest): Response<AuthResponse>

    @POST("password/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<AuthResponse>

    @GET("me")
    suspend fun getCurrentUser(): Response<AuthResponse>

    @POST("token/verify")
    suspend fun verifyToken(@Body request: TokenVerifyRequest): Response<AuthResponse>

    @PUT("me")
    suspend fun updateProfile(@Body updateRequest: UpdateProfileRequest): Response<ProfileResponse>

    @Multipart
    @POST("me/profile-image")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): Response<ProfileResponse>

    @PUT("me/profile-image-base64")
    suspend fun uploadProfileImageBase64(@Body request: UploadImageBase64Request): Response<ProfileResponse>
}
