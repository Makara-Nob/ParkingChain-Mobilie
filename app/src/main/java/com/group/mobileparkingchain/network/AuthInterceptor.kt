package com.group.mobileparkingchain.network

import android.content.Context
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import com.group.mobileparkingchain.network.AuthEventBus
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor adds authentication headers and handles token expiration.
 * 
 * Responsibilities:
 * - Add "Authorization: Bearer <token>" header to all requests
 * - Detect 401 Unauthorized responses (token expired/invalid)
 * - Clear token on 401 to trigger re-login flow
 * 
 * Note: This interceptor runs synchronously in the OkHttp chain.
 * Token clearing uses runBlocking but is safe here (I/O thread).
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    private val tokenDataStore = TokenDataStore(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenDataStore.token.map { it }.firstOrNull()
        }

        android.util.Log.d("AuthInterceptor", "Token found: ${token?.take(10)}")

        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
            android.util.Log.d("AuthInterceptor", "Added Authorization header")
        } ?: android.util.Log.e("AuthInterceptor", "Token is null, header not added")

        val response = chain.proceed(requestBuilder.build())

        // Handle 401 Unauthorized - token expired or invalid
        if (response.code == 401) {
            android.util.Log.w("AuthInterceptor", "401 Unauthorized - clearing token")
            runBlocking {
                tokenDataStore.clearToken()
            }
            AuthEventBus.emitLogout()
            // Note: NavGraph will detect cleared token on next API call or app restart
            // For immediate logout, use a global event bus or shared flow (future enhancement)
        }

        return response
    }
}
