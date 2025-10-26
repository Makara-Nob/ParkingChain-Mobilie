package com.group.mobileparkingchain.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.features.auth.presentation.components.signin.EmailInput
import com.group.mobileparkingchain.features.auth.presentation.components.signin.ErrorText
import com.group.mobileparkingchain.features.auth.presentation.components.signup.NameInputField
import com.group.mobileparkingchain.features.auth.presentation.components.signup.PasswordInput
import com.group.mobileparkingchain.features.auth.presentation.components.signup.SignInRow
import com.group.mobileparkingchain.features.auth.presentation.components.signup.SignUpButton
import com.group.mobileparkingchain.ui.components.ParkingLogo

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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

            // ================= Subcomponents =================
            NameInputField(label = "First Name", value = firstName, onValueChange = {
                firstName = it; showError = false
            })

            Spacer(modifier = Modifier.height(16.dp))

            NameInputField(label = "Last Name", value = lastName, onValueChange = {
                lastName = it; showError = false
            })

            Spacer(modifier = Modifier.height(16.dp))

            EmailInput(email = email, onEmailChange = {
                email = it; showError = false
            })

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                label = "Password",
                password = password,
                passwordVisible = passwordVisible,
                onPasswordChange = { password = it; showError = false },
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                label = "Confirm Password",
                password = confirmPassword,
                passwordVisible = confirmPasswordVisible,
                onPasswordChange = { confirmPassword = it; showError = false },
                onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorText(message = errorMessage)
            }

            Spacer(modifier = Modifier.height(32.dp))

            SignUpButton(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onSuccess = onSignUpSuccess,
                onFailure = { showError = true; errorMessage = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            SignInRow(onNavigateToSignIn)
        }
    }
}
