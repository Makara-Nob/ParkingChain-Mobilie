package com.group.mobileparkingchain.features.auth.data.repository

import com.group.mobileparkingchain.features.auth.data.model.LoginRequest
import com.group.mobileparkingchain.features.auth.data.model.PasswordResetRequestRequest
import com.group.mobileparkingchain.features.auth.data.model.RegisterRequest
import com.group.mobileparkingchain.features.auth.data.model.ResetPasswordRequest
import com.group.mobileparkingchain.features.auth.data.model.VerifyEmailRequest
import com.group.mobileparkingchain.features.auth.data.model.VerifyResetRequest
import com.group.mobileparkingchain.features.auth.data.remote.AuthApiService
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val authApiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : IAuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    val authData = authResponse.data
                    
                    if (authData != null) {
                        tokenDataStore.saveToken(authData.token)
                        tokenDataStore.saveUserId(authData.user.id)
                        Result.success(authData.user.toDomain())
                    } else {
                        Result.failure(Exception("No data received"))
                    }
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Login failed"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(e.message()))
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(email, password, firstName, lastName)
                val response = authApiService.register(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    val authData = authResponse.data

                    if (authData != null) {
                        tokenDataStore.saveToken(authData.token)
                        tokenDataStore.saveUserId(authData.user.id)
                        Result.success(authData.user.toDomain())
                    } else {
                        Result.failure(Exception("No data received"))
                    }
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Registration failed"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(e.message()))
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }

    override suspend fun verifyEmail(email: String, otp: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = VerifyEmailRequest(email, otp)
                val response = authApiService.verifyEmail(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Verification failed"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(e.message()))
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.getCurrentUser()

                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    val authData = authResponse.data

                    if (authData != null) {
                        Result.success(authData.user.toDomain())
                    } else {
                        Result.failure(Exception("No data received"))
                    }
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to get user"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(e.message()))
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.requestPasswordReset(PasswordResetRequestRequest(email))
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to request password reset"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun verifyResetOtp(email: String, otp: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.verifyResetOtp(VerifyResetRequest(email, otp))
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Invalid OTP"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.resetPassword(ResetPasswordRequest(email, otp, newPassword))
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to reset password"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun com.group.mobileparkingchain.features.auth.data.model.User.toDomain(): User {
        return User(
            id = this.id,
            fullName = "${this.firstName} ${this.lastName}",
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email,
            phone = this.phone ?: "",
            profileImage = this.profileImage
        )
    }
}