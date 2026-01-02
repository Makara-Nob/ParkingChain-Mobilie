package com.group.mobileparkingchain.features.auth.domain.model

/**
 * Sealed class representing authentication state throughout the app lifecycle.
 * Follows Single Source of Truth pattern for auth state management.
 */
sealed class AuthState {
    /**
     * Initial state while checking stored credentials.
     * Shown during app startup/splash screen.
     */
    object Loading : AuthState()

    /**
     * User is authenticated with valid token.
     * @param user Current authenticated user profile
     * @param token JWT access token (optional, already stored in DataStore)
     */
    data class Authenticated(val user: User, val token: String? = null) : AuthState()

    /**
     * User is not authenticated or token is invalid/expired.
     * @param reason Optional reason for unauthenticated state (e.g., "Token expired")
     */
    data class Unauthenticated(val reason: String? = null) : AuthState()

    /**
     * Error occurred during auth check (network failure, etc.)
     * Unlike Unauthenticated, this represents a temporary failure.
     * @param message Error message to display
     */
    data class Error(val message: String) : AuthState()
}
