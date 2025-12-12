package com.group.mobileparkingchain.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/**
 * Extension function to show modern toast in Composable
 * 
 * Usage:
 * ```
 * val showToast = rememberModernToast()
 * 
 * showToast("Profile updated successfully!", ToastType.SUCCESS)
 * showToast("Failed to update", ToastType.ERROR)
 * showToast("Loading...", ToastType.INFO)
 * ```
 */
@Composable
fun rememberModernToast(): (String, ToastType) -> Unit {
    val toastState = LocalToastState.current
    val scope = rememberCoroutineScope()
    
    return { message, type ->
        scope.launch {
            toastState.showToast(message, type)
        }
    }
}

/**
 * Legacy toast replacement - use same API as Android Toast
 * 
 * Usage:
 * ```
 * showModernToast(context, "Message", ToastType.SUCCESS)
 * ```
 */
fun showModernToast(
    message: String,
    type: ToastType = ToastType.INFO,
    toastState: ToastState
) {
    kotlinx.coroutines.GlobalScope.launch {
        toastState.showToast(message, type)
    }
}
