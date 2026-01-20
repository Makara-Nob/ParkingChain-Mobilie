package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.R
import com.group.mobileparkingchain.ui.theme.sdp

@Composable
fun AnimatedChatButton(
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f, // Scale up to 110%
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // 1 second pulse
            repeatMode = RepeatMode.Reverse
        )
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.scale(scale),
        containerColor = Color(0xFF2196F3),
        contentColor = Color.White
    ) {
        Image(
            painter = painterResource(id = R.drawable.chatbot),
            contentDescription = "Chat",
            modifier = Modifier.size(sdp(32)) // Slightly larger for better visibility
        )
    }
}
