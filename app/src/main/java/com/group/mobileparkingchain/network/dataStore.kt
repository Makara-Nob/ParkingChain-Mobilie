package com.group.mobileparkingchain.network.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class TokenDataStore(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // Save token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Read token as Flow
    val token: Flow<String?>
        get() = context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    // Clear token (logout)
    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
