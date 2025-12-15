package com.group.mobileparkingchain.features.auth.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group.mobileparkingchain.features.auth.di.AuthModule
import com.group.mobileparkingchain.features.auth.presentation.components.reset.PasswordStrengthIndicator
import com.group.mobileparkingchain.features.auth.presentation.components.verification.OtpInputField
import com.group.mobileparkingchain.features.auth.presentation.components.signup.PasswordInput
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.PasswordResetState
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.PasswordResetViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.PasswordResetViewModelFactory
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.ResetStep
import com.group.mobileparkingchain.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authModule = remember { AuthModule(context) }
    val viewModel: PasswordResetViewModel = viewModel(
        factory = PasswordResetViewModelFactory(authModule)
    )

    val currentStep by viewModel.currentStep.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val otp by viewModel.otp.collectAsState()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PasswordResetState.Success -> {
                // Removed toast for final success as we have a screen for it now, 
                // but kept for intermediate steps if needed or can just rely on UI change
                if (currentStep != ResetStep.SUCCESS) {
                     // Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                // viewModel.clearState() // Don't clear immediately if we want to show success screen
            }
            is PasswordResetState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.clearState()
            }
            else -> {}
        }
    }

    if (currentStep == ResetStep.SUCCESS) {
        SuccessScreen(onLoginClick = onSuccess)
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = if (currentStep == ResetStep.RESET_PASSWORD) "Set New Password" else "Reset Password", 
                            color = Color.White
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
                )
            },
            containerColor = Color(0xFF121212)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                when (currentStep) {
                    ResetStep.REQUEST_OTP -> {
                        Text(
                            text = "Enter your email to receive an OTP code.",
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = viewModel::requestOtp,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = uiState !is PasswordResetState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (uiState is PasswordResetState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Send OTP")
                            }
                        }
                    }
                    ResetStep.VERIFY_OTP -> {
                        Text(
                            text = "Enter the 6-digit OTP sent to $email",
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OtpInputField(
                            otp = otp,
                            onOtpChange = viewModel::onOtpChange
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = viewModel::verifyOtp,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = uiState !is PasswordResetState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (uiState is PasswordResetState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Verify OTP")
                            }
                        }
                    }
                    ResetStep.RESET_PASSWORD -> {
                        PasswordInput(
                            label = "New Password",
                            password = newPassword,
                            passwordVisible = newPasswordVisible,
                            onPasswordChange = { newPassword = it },
                            onPasswordVisibilityToggle = { newPasswordVisible = !newPasswordVisible }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PasswordInput(
                            label = "Confirm New Password",
                            password = confirmPassword,
                            passwordVisible = confirmPasswordVisible,
                            onPasswordChange = { confirmPassword = it },
                            onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        PasswordStrengthIndicator(password = newPassword)
                        
                        Spacer(modifier = Modifier.weight(1f)) // Push button to bottom
                        
                        Button(
                            onClick = {
                                if (newPassword == confirmPassword) {
                                    viewModel.resetPassword(newPassword)
                                } else {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = uiState !is PasswordResetState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (uiState is PasswordResetState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Set New Password")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun SuccessScreen(onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                 // Outer circle stroke (optional if needed, but icon is solid blue circle with check)
                 // Assuming we want a style similar to provided image: solid blue circle with check
                 Box(
                     modifier = Modifier
                         .size(80.dp)
                         .background(PrimaryBlue, CircleShape)
                         .border(4.dp, PrimaryBlue.copy(alpha = 0.3f), CircleShape), // Optional glow effect
                     contentAlignment = Alignment.Center
                 ) {
                     Icon(
                         imageVector = Icons.Default.Check,
                         contentDescription = "Success",
                         tint = Color.White,
                         modifier = Modifier.size(40.dp)
                     )
                 }
            }

            Text(
                text = "Password Reset\nSuccessful!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Your password has been updated. You can now\nlog in with your new password.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = CircleShape
            ) {
                Text(
                    text = "Back to Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
