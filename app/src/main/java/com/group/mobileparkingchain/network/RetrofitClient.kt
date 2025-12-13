package com.group.mobileparkingchain.network

import android.content.Context
import com.group.mobileparkingchain.features.auth.data.remote.AuthApiService
import com.group.mobileparkingchain.features.parking.data.remote.ParkingApiService
import com.group.mobileparkingchain.features.payment.data.remote.PaymentApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // Host IP for Emulator (10.0.2.2) or Physical Device (IP)
    private const val HOST_IP = "10.0.2.2" 
    
    // Updated base URLs to match API specification
    // Auth Service: port 3000
    // Parking Service: port 3002
    // Payment Service: port 3003
    private const val AUTH_URL = "http://$HOST_IP:3000/api/v1/auth/"
    private const val PARKING_URL = "http://$HOST_IP:3002/api/v1/"
    private const val PAYMENT_URL = "http://$HOST_IP:3003/api/v1/"

    private var authInterceptor: AuthInterceptor? = null

    fun initialize(context: Context) {
        authInterceptor = AuthInterceptor(context)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
        
        authInterceptor?.let {
            builder.addInterceptor(it)
        }
        
        builder.build()
    }

    private fun <T> createService(baseUrl: String, serviceClass: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    val authApi: AuthApiService by lazy {
        createService(AUTH_URL, AuthApiService::class.java)
    }

    val parkingApi: ParkingApiService by lazy {
        createService(PARKING_URL, ParkingApiService::class.java)
    }

    val paymentApi: PaymentApiService by lazy {
        createService(PAYMENT_URL, PaymentApiService::class.java)
    }

    // TODO: Replace with your actual Ngrok/Colab URL
    private const val CHAT_URL = "https://grouseless-nonphysically-craig.ngrok-free.dev/api/"
    
    val chatApi: com.group.mobileparkingchain.features.chat.data.remote.ChatApiService by lazy {
        createService(CHAT_URL, com.group.mobileparkingchain.features.chat.data.remote.ChatApiService::class.java)
    }
}