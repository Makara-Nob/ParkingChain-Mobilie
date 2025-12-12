package com.group.mobileparkingchain.network

import android.content.Context
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

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

        return chain.proceed(requestBuilder.build())
    }
}
