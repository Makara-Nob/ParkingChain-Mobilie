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
        // Get token synchronously (ok for small requests)
        val token = runBlocking { tokenDataStore.token.map { it }.firstOrNull() }

        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
