package com.group.mobileparkingchain.ui.screens.signin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group.mobileparkingchain.core.Resource
import com.group.mobileparkingchain.features.auth.presentation.components.signin.EmailInput
import com.group.mobileparkingchain.features.auth.presentation.components.signin.ErrorText
import com.group.mobileparkingchain.features.auth.presentation.components.signin.ForgotPasswordText
import com.group.mobileparkingchain.features.auth.presentation.components.signin.PasswordInput
import com.group.mobileparkingchain.features.auth.presentation.components.signin.SignInButton
import com.group.mobileparkingchain.features.auth.presentation.components.signin.SignUpRow
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SignInViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SignInViewModelFactory
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import com.group.mobileparkingchain.ui.components.ParkingLogo

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenDataStore = remember { TokenDataStore(context) }
    val viewModel: SignInViewModel = viewModel(
        factory = SignInViewModelFactory(tokenDataStore)
    )

    var email by remember { mutableStateOf("admin@gmail.com") }
    var password by remember { mutableStateOf("88889999") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val loginState by viewModel.loginState.collectAsState()

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                Toast.makeText(
                    context,
                    "Welcome ${state.data.user.firstName}! 🎉",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearState()
                onSignInSuccess()
            }
            is Resource.Error -> {
                errorMessage = state.message
                Toast.makeText(
                    context,
                    state.message ?: "Login failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> { /* Do nothing */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ParkingLogo(size = 250)
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            EmailInput(email, onEmailChange = {
                email = it
                errorMessage = null
            })

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                password = password,
                passwordVisible = passwordVisible,
                onPasswordChange = {
                    password = it
                    errorMessage = null
                },
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorText(errorMessage!!)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ForgotPasswordText(onForgotPassword)

            Spacer(modifier = Modifier.height(24.dp))

            SignInButton(
                email = email,
                password = password,
                onClick = { viewModel.signIn(email, password) },
                isLoading = loginState is Resource.Loading
            )

            Spacer(modifier = Modifier.height(120.dp))

            SignUpRow(onNavigateToSignUp)
        }
    }
}