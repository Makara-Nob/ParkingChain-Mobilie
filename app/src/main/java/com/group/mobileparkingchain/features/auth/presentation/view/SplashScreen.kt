package com.group.mobileparkingchain.features.auth.presentation.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group.mobileparkingchain.features.auth.domain.model.AuthState
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SplashViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SplashViewModelFactory
import com.group.mobileparkingchain.ui.components.ParkingLogo

/**
 * Splash screen displayed on app startup.
 * 
 * Responsibilities:
 * - Show branding/logo while checking authentication
 * - Validate stored token with backend
 * - Navigate to appropriate screen based on auth state
 * 
 * Design:
 * - Loading: Logo + spinner (checking credentials)
 * - Error: Logo + error message + retry button (network issues)
 * - Success: Auto-navigate (no UI flash)
 * 
 * Best Practices:
 * - No manual navigation logic here (handled by NavGraph observing authState)
 * - Single responsibility: display loading state
 * - Timeout protection (handled by Retrofit timeout in repository)
 */
@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    onUnverified: (String) -> Unit  // Navigate to OTP with email
) {
    val context = LocalContext.current
    val viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(context)
    )

    val authState by viewModel.authState.collectAsState()

    // Logo pulse animation for loading state
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val logoAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )


    // Navigate based on auth state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                // Token is valid, user is verified
                onAuthenticated()
            }
            is AuthState.Unverified -> {
                // Token valid but email not verified - navigate to OTP
                onUnverified(state.email)
            }
            is AuthState.Unauthenticated -> {
                // No token or token invalid, need to login
                onUnauthenticated()
            }
            // Loading and Error states handled by UI below
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo with optional pulse animation
            ParkingLogo(
                size = 200,
                modifier = Modifier.alpha(if (authState is AuthState.Loading) logoAlpha else 1f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // State-specific UI
            when (val state = authState) {
                is AuthState.Loading -> {
                    // Loading state: checking credentials
                    CircularProgressIndicator(
                        color = Color(0xFF2196F3),
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Checking credentials...",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }

                is AuthState.Error -> {
                    // Error state: show message and retry button
                    Text(
                        text = "⚠️",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = state.message,
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.retry() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Authenticated and Unauthenticated handled by LaunchedEffect navigation
                else -> Unit
            }
        }

        if (authState is AuthState.Loading) {
            RotatingDotsSpinner(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )
        }

        // App version footer (optional)
        Text(
            text = "v1.0.0",
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun RotatingDotsSpinner(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(36.dp)) {
        val dotCount = 8
        val radius = size.minDimension * 0.35f
        val dotRadius = size.minDimension * 0.08f
        rotate(rotation) {
            for (i in 0 until dotCount) {
                val angle = (i * 360f / dotCount)
                val alpha = 0.3f + (i / (dotCount - 1f)) * 0.7f
                val x = center.x + radius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val y = center.y + radius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                drawCircle(
                    color = Color(0xFF2196F3).copy(alpha = alpha),
                    radius = dotRadius,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
    }
}
