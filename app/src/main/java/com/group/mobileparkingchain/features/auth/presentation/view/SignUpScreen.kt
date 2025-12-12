package com.group.mobileparkingchain.ui.screens.signup

import Resource
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
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.auth.presentation.components.signin.EmailInput
import com.group.mobileparkingchain.features.auth.presentation.components.signin.ErrorText
import com.group.mobileparkingchain.features.auth.presentation.components.signup.NameInputField
import com.group.mobileparkingchain.features.auth.presentation.components.signup.PasswordInput
import com.group.mobileparkingchain.features.auth.presentation.components.signup.SignInRow
import com.group.mobileparkingchain.features.auth.presentation.components.signup.SignUpButton
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SignUpViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.SignUpViewModelFactory
import com.group.mobileparkingchain.ui.components.ParkingLogo

@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit = {},
    onNavigateToSignIn: () -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(context)
    )

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState.collectAsState<Resource<User>>()

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is Resource.Success -> {
                Toast.makeText(
                    context,
                    "Registration successful! Please check your email for the OTP.",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearState()
                onSignUpSuccess(email)
            }
            is Resource.Error -> {
                val message = state.message
                errorMessage = message
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp)
        ) {
            ParkingLogo(size = 200)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Join Us Now!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            NameInputField("First Name", firstName) { firstName = it; errorMessage = null }
            Spacer(modifier = Modifier.height(16.dp))
            NameInputField("Last Name", lastName) { lastName = it; errorMessage = null }
            Spacer(modifier = Modifier.height(16.dp))

            EmailInput(email) { email = it; errorMessage = null }
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput("Password", password, passwordVisible,
                onPasswordChange = { password = it; errorMessage = null },
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput("Confirm Password", confirmPassword, confirmPasswordVisible,
                onPasswordChange = { confirmPassword = it; errorMessage = null },
                onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorText(errorMessage!!)
            }

            Spacer(modifier = Modifier.height(32.dp))

            SignUpButton(
                firstName, lastName, email, password, confirmPassword,
                onClick = {
                    viewModel.signUp(firstName, lastName, email, password, confirmPassword)
                },
                isLoading = registerState is Resource.Loading
            )

            Spacer(modifier = Modifier.height(40.dp))
            SignInRow(onNavigateToSignIn)
        }
    }
}
