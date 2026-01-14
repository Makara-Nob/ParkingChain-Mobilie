package com.group.mobileparkingchain.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class AuthEvent {
    object Logout : AuthEvent()
}

object AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emitLogout() {
        _events.tryEmit(AuthEvent.Logout)
    }
}
