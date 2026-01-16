package com.group.mobileparkingchain.network

import android.content.Context
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import com.group.mobileparkingchain.network.AuthEventBus
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject

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
    private val refreshClient = OkHttpClient.Builder().build()
    private val refreshLock = Any()

    private val authBaseUrl = "http://18.142.125.101:3001/api/v1/auth/"
    private val authHost = authBaseUrl.toHttpUrl().host
    private val authPort = authBaseUrl.toHttpUrl().port

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenDataStore.token.map { it }.firstOrNull()
        }

        android.util.Log.d("AuthInterceptor", "Token found: ${token?.take(10)}")

        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
            android.util.Log.d("AuthInterceptor", "Added Authorization header")
        } ?: android.util.Log.e("AuthInterceptor", "Token is null, header not added")

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // Handle 401 Unauthorized - token expired or invalid
        val isAuthHost = request.url.host == authHost && request.url.port == authPort
        if (response.code == 401 && request.header("X-Auth-Retry") == null && isAuthHost) {
            android.util.Log.w("AuthInterceptor", "401 Unauthorized - attempting token refresh")

            val newToken = synchronized(refreshLock) {
                tryRefreshToken()
            }

            if (newToken != null) {
                response.close()
                runBlocking { tokenDataStore.saveToken(newToken) }
                val retryRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .header("X-Auth-Retry", "1")
                    .build()
                return chain.proceed(retryRequest)
            }

            android.util.Log.w("AuthInterceptor", "Token refresh failed - clearing token")
            runBlocking { tokenDataStore.clearToken() }
            AuthEventBus.emitLogout()
        }

        return response
    }

    private fun tryRefreshToken(): String? {
        val refreshToken = runBlocking {
            tokenDataStore.refreshToken.map { it }.firstOrNull()
        } ?: return null

        val payload = JSONObject().apply {
            put("refreshToken", refreshToken)
        }

        val requestBody = payload.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = okhttp3.Request.Builder()
            .url("${authBaseUrl}token/refresh")
            .post(requestBody)
            .build()

        val response = refreshClient.newCall(request).execute()
        response.use {
            if (!it.isSuccessful) {
                return null
            }
            val body = it.body?.string() ?: return null
            val json = JSONObject(body)
            if (!json.optBoolean("success", false)) {
                return null
            }
            val data = json.optJSONObject("data") ?: return null
            return data.optString("token", null)
        }
    }
}
