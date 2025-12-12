package com.group.mobileparkingchain.features.auth.di

import android.content.Context
import com.group.mobileparkingchain.features.auth.data.repository.AuthRepository
import com.group.mobileparkingchain.network.RetrofitInstance
import com.group.mobileparkingchain.network.datastore.TokenDataStore

class AuthModule(private val context: Context) {
    val tokenDataStore: TokenDataStore by lazy {
        TokenDataStore(context)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitInstance.authApi, tokenDataStore)
    }
}
