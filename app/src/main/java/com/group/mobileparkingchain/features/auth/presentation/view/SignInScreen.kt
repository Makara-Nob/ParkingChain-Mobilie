package com.group.mobileparkingchain.ui.screens.signin

import com.group.mobileparkingchain.core.Resource
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group.mobileparkingchain.features.auth.config.AuthConfig
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.presentation.components.signin.*
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SignInViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SignInViewModelFactory
import com.group.mobileparkingchain.ui.components.ParkingLogo
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onNavigateToOtp: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: SignInViewModel = viewModel(
        factory = SignInViewModelFactory(context)
    )

    // Pre-fill fields when using mock data (feature flag is false)
    var email by remember { 
        mutableStateOf(if (!AuthConfig.USE_API) AuthConfig.MOCK_EMAIL else "") 
    }
    var password by remember { 
        mutableStateOf(if (!AuthConfig.USE_API) AuthConfig.MOCK_PASSWORD else "") 
    }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val loginState by viewModel.loginState.collectAsState<Resource<User>>()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                val userName = state.data.firstName
                Toast.makeText(
                    context,
                    "Welcome back, $userName! 🎉",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearState()
                onSignInSuccess()
            }
            is Resource.Error -> {
                val message = state.message
                errorMessage = message
                // Show toast for immediate feedback (user might not see inline error)
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
                if (message.contains("verify your email", ignoreCase = true)) {
                    onNavigateToOtp(email)
                }
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = sdp(24))
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sdp(40))
        ) {
            ParkingLogo(size = sdp(200))
            Spacer(modifier = Modifier.height(sdp(16)))
            Text(
                text = "Welcome Back!",
                fontSize = ssp(28),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(sdp(32)))

            EmailInput(email) { email = it; errorMessage = null }
            Spacer(modifier = Modifier.height(sdp(16)))

            PasswordInput(
                password = password,
                passwordVisible = passwordVisible,
                onPasswordChange = { password = it; errorMessage = null },
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(sdp(8)))
                ErrorText(errorMessage!!)
            }

            Spacer(modifier = Modifier.height(sdp(8)))
            ForgotPasswordText(onForgotPassword)
            Spacer(modifier = Modifier.height(sdp(24)))

            SignInButton(
                email = email,
                password = password,
                onClick = {
                    viewModel.signIn(email, password)
                },
                isLoading = loginState is Resource.Loading
            )

            Spacer(modifier = Modifier.height(sdp(40)))
            SignUpRow(onNavigateToSignUp)
        }
    }
}
