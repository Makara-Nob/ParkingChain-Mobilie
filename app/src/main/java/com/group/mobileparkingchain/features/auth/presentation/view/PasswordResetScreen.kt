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
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

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
                // Show success toast for intermediate steps (OTP sent, OTP verified)
                // Final success (password reset) is shown in success screen
                if (currentStep != ResetStep.SUCCESS) {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                // Don't clear state immediately to allow navigation to complete
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Centered content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = sdp(24))
            ) {
                // Logo
                com.group.mobileparkingchain.ui.components.ParkingLogo(size = sdp(180))
                
                Spacer(modifier = Modifier.height(sdp(16)))
                
                // Title based on step
                Text(
                    text = when (currentStep) {
                        ResetStep.REQUEST_OTP -> "Reset Password"
                        ResetStep.VERIFY_OTP -> "Verify OTP"
                        ResetStep.RESET_PASSWORD -> "New Password"
                        else -> "Reset Password"
                    },
                    fontSize = ssp(28),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(sdp(32)))
                
                when (currentStep) {
                    ResetStep.REQUEST_OTP -> {
                        Text(
                            text = "Enter your email to receive a verification code",
                            color = Color.Gray,
                            fontSize = ssp(14),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = sdp(24))
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = PrimaryBlue,
                                unfocusedLabelColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Spacer(modifier = Modifier.height(sdp(24)))
                        Button(
                            onClick = viewModel::requestOtp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(sdp(56)),
                            enabled = uiState !is PasswordResetState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (uiState is PasswordResetState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(sdp(24)))
                            } else {
                                Text("Send Code", fontSize = ssp(16), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    ResetStep.VERIFY_OTP -> {
                        Text(
                            text = "Enter the 6-digit code sent to",
                            color = Color.Gray,
                            fontSize = ssp(14),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = email,
                            color = PrimaryBlue,
                            fontSize = ssp(14),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = sdp(24))
                        )
                        OtpInputField(
                            otp = otp,
                            onOtpChange = viewModel::onOtpChange,
                            onOtpComplete = { completedOtp ->
                                if (uiState !is PasswordResetState.Loading) {
                                    viewModel.onOtpChange(completedOtp)
                                    viewModel.verifyOtp()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(sdp(24)))
                        Button(
                            onClick = viewModel::verifyOtp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(sdp(56)),
                            enabled = uiState !is PasswordResetState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (uiState is PasswordResetState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(sdp(24)))
                            } else {
                                Text("Verify Code", fontSize = ssp(16), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    ResetStep.RESET_PASSWORD -> {
                        Spacer(modifier = Modifier.height(sdp(24)))
                        
                        PasswordInput(
                            label = "New Password",
                            password = newPassword,
                            passwordVisible = newPasswordVisible,
                            onPasswordChange = { newPassword = it },
                            onPasswordVisibilityToggle = { newPasswordVisible = !newPasswordVisible }
                        )
                        
                        Spacer(modifier = Modifier.height(sdp(16)))
                        
                        PasswordInput(
                            label = "Confirm Password",
                            password = confirmPassword,
                            passwordVisible = confirmPasswordVisible,
                            onPasswordChange = { confirmPassword = it },
                            onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                        )
                        
                        Spacer(modifier = Modifier.height(sdp(16)))
                        Spacer(modifier = Modifier.height(sdp(24)))
                        
                        Button(
                            onClick = {
                                if (newPassword == confirmPassword) {
                                    viewModel.resetPassword(newPassword)
                                } else {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(sdp(56)),
                            enabled = uiState !is PasswordResetState.Loading && newPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (uiState is PasswordResetState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(sdp(24)))
                            } else {
                                Text("Reset Password", fontSize = ssp(16), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    else -> {}
                }
            }
            
            // Back button on top (drawn last = on top of z-index)
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = sdp(16), top = sdp(48)) // Moved down from top
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            modifier = Modifier.padding(sdp(24))
        ) {
            Box(
                modifier = Modifier
                    .size(sdp(100))
                    .padding(bottom = sdp(24)),
                contentAlignment = Alignment.Center
            ) {
                 // Outer circle stroke (optional if needed, but icon is solid blue circle with check)
                 // Assuming we want a style similar to provided image: solid blue circle with check
                 Box(
                     modifier = Modifier
                         .size(sdp(80))
                         .background(PrimaryBlue, CircleShape)
                         .border(sdp(4), PrimaryBlue.copy(alpha = 0.3f), CircleShape), // Optional glow effect
                     contentAlignment = Alignment.Center
                 ) {
                     Icon(
                         imageVector = Icons.Default.Check,
                         contentDescription = "Success",
                         tint = Color.White,
                         modifier = Modifier.size(sdp(40))
                     )
                 }
            }

            Text(
                text = "Password Reset\nSuccessful!",
                color = Color.White,
                fontSize = ssp(24),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = sdp(16))
            )

            Text(
                text = "Your password has been updated. You can now\nlog in with your new password.",
                color = Color.Gray,
                fontSize = ssp(14),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = sdp(32))
            )

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(sdp(50)),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = CircleShape
            ) {
                Text(
                    text = "Back to Login",
                    fontSize = ssp(16),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
