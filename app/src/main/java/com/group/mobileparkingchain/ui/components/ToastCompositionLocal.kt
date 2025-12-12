package com.group.mobileparkingchain.ui.components

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal for providing ToastState throughout the app
 */
val LocalToastState = compositionLocalOf<ToastState> {
    error("No ToastState provided")
}
