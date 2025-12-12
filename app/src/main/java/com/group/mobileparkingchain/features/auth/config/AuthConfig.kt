package com.group.mobileparkingchain.features.auth.config

/**
 * Feature flag for authentication
 * Set to false to use mock data instead of API calls
 * Set to true to use real API calls
 */
object AuthConfig {
    // Change this to false to use mock data
    // When false, login will use static mock data and fields will be pre-filled
    const val USE_API = true

    // Mock user credentials (used when USE_API is false)
    const val MOCK_EMAIL = "admin@gmail.com"
    const val MOCK_PASSWORD = "88889999"
}
