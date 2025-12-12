package com.group.mobileparkingchain.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.INFO,
    val duration: Long = 3000L
)

@Composable
fun ModernToastHost(
    toastState: ToastState,
    modifier: Modifier = Modifier
) {
    val currentToast by toastState.currentToast.collectAsState(initial = null)
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = currentToast != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            currentToast?.let { toast ->
                ModernToast(
                    message = toast.message,
                    type = toast.type
                )
            }
        }
    }
}

@Composable
private fun ModernToast(
    message: String,
    type: ToastType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, iconColor, icon) = when (type) {
        ToastType.SUCCESS -> Triple(
            Color(0xFF10B981),
            Color.White,
            Icons.Default.CheckCircle
        )
        ToastType.ERROR -> Triple(
            Color(0xFFEF4444),
            Color.White,
            Icons.Default.Error
        )
        ToastType.INFO -> Triple(
            Color(0xFF3B82F6),
            Color.White,
            Icons.Default.Info
        )
    }
    
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = message,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

class ToastState {
    private val _currentToast = MutableStateFlow<ToastData?>(null)
    val currentToast: StateFlow<ToastData?> = _currentToast.asStateFlow()
    
    suspend fun showToast(message: String, type: ToastType = ToastType.INFO, duration: Long = 3000L) {
        val toast = ToastData(message, type, duration)
        _currentToast.value = toast
        delay(duration)
        _currentToast.value = null
    }
}

@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}
