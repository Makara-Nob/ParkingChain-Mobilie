package com.group.mobileparkingchain.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.auth.domain.GetCurrentUserUseCase
import com.group.mobileparkingchain.features.auth.domain.model.AuthState
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel for splash screen and initial authentication check.
 * 
 * Responsibilities:
 * - Check for stored authentication token on app startup
 * - Validate token with backend (GET /auth/me)
 * - Emit appropriate AuthState for navigation decisions
 * 
 * Best Practices Implemented:
 * - Single Source of Truth: AuthState flow
 * - Token validation before granting access (prevents stale token usage)
 * - Graceful error handling (network failures don't force logout)
 * - Security: Token validation server-side (not just local expiry check)
 */
class SplashViewModel(
    private val tokenDataStore: TokenDataStore,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    /**
     * Check authentication status on app startup.
     * 
     * Flow:
     * 1. Read token from DataStore
     * 2. If no token → Unauthenticated
     * 3. If token exists → validate with backend (GET /auth/me)
     * 4. If valid → Authenticated with user profile
     * 5. If invalid/expired → Unauthenticated (user must login again)
     * 6. If network error → Error (retry option, don't force logout)
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                // Step 1: Check if token exists in local storage
                val token = tokenDataStore.token.firstOrNull()
                
                if (token.isNullOrEmpty()) {
                    // No token stored → user must login
                    _authState.value = AuthState.Unauthenticated("No stored credentials")
                    return@launch
                }

                // Step 2: Validate token with backend
                // Note: AuthInterceptor will automatically add "Authorization: Bearer <token>"
                val result = getCurrentUserUseCase()

                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        // Check if email is verified
                        if (user.isVerified) {
                            // Fully authenticated and verified
                            _authState.value = AuthState.Authenticated(user, token)
                        } else {
                            // Registered but not verified - navigate to OTP screen
                            _authState.value = AuthState.Unverified(user, user.email)
                        }
                    } else {
                        // Shouldn't happen, but handle gracefully
                        _authState.value = AuthState.Unauthenticated("Invalid user data")
                    }
                } else {
                    // Token validation failed (401, 403, or other error)
                    val error = result.exceptionOrNull()
                    val errorMessage = error?.message ?: "Authentication failed"

                    // Check if it's an auth error (401/403) vs network error
                    // Also check for "Invalid or expired token" message from backend
                    if (errorMessage.contains("401") || 
                        errorMessage.contains("403") || 
                        errorMessage.contains("Unauthorized") ||
                        errorMessage.contains("Invalid token") ||
                        errorMessage.contains("expired token") ||
                        errorMessage.contains("Invalid or expired token") ||
                        errorMessage.contains("User not found")) {
                        // Token is invalid/expired → clear and force login
                        tokenDataStore.clearToken()
                        _authState.value = AuthState.Unauthenticated("Session expired. Please login again.")
                    } else {
                        // Network or other temporary error → show error state with retry
                        _authState.value = AuthState.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                // Unexpected error during auth check
                _authState.value = AuthState.Error(e.message ?: "Authentication check failed")
            }
        }
    }

    /**
     * Retry authentication check (called when user taps retry after error).
     */
    fun retry() {
        checkAuthStatus()
    }

    /**
     * Clear auth state and force logout.
     * Use when user explicitly logs out or when unrecoverable auth error occurs.
     */
    fun logout() {
        viewModelScope.launch {
            tokenDataStore.clearToken()
            _authState.value = AuthState.Unauthenticated("Logged out")
        }
    }
}
