package com.group.mobileparkingchain.features.auth.data.repository

import com.group.mobileparkingchain.features.auth.data.remote.AuthService
import com.group.mobileparkingchain.features.auth.data.remote.models.LoginRequest
import com.group.mobileparkingchain.features.auth.data.remote.models.RegisterRequest
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.domain.repository.IAuthRepository
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val authService: AuthService,
    private val tokenDataStore: TokenDataStore
) : IAuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authService.login(LoginRequest(email, password))

                if (response.success && response.data != null) {
                    // Save token
                    tokenDataStore.saveToken(response.data.token)

                    // Return user directly from API response
                    Result.success(response.data.user)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: HttpException) {
                // HTTP errors (4xx, 5xx)
                val errorMessage = when (e.code()) {
                    401 -> "Invalid email or password"
                    404 -> "Service not found"
                    500 -> "Server error. Please try again later"
                    else -> "Error: ${e.message()}"
                }
                Result.failure(Exception(errorMessage))
            } catch (e: IOException) {
                // Network errors
                Result.failure(Exception("Network error. Check your connection"))
            } catch (e: Exception) {
                // Unexpected errors
                Result.failure(Exception("Unexpected error: ${e.message}"))
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
                val request = RegisterRequest(firstName, lastName, email, password)
                val response = authService.register(request)

                if (response.success && response.data != null) {
                    // Save token
                    tokenDataStore.saveToken(response.data.token)

                    // Return user directly from API response
                    Result.success(response.data.user)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    409 -> "Email already exists"
                    400 -> "Invalid registration data"
                    else -> "Error: ${e.message()}"
                }
                Result.failure(Exception(errorMessage))
            } catch (e: IOException) {
                Result.failure(Exception("Network error. Check your connection"))
            } catch (e: Exception) {
                Result.failure(Exception("Unexpected error: ${e.message}"))
            }
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }
}