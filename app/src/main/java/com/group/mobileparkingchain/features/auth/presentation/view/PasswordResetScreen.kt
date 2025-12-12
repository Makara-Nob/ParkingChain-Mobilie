package com.group.mobileparkingchain.features.auth.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group.mobileparkingchain.features.auth.di.AuthModule
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.PasswordResetState
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.PasswordResetViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.PasswordResetViewModelFactory
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.ResetStep

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

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PasswordResetState.Success -> {
                // Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                if (currentStep == ResetStep.SUCCESS) {
                    Toast.makeText(context, "Password Reset Successfully. Please login again.", Toast.LENGTH_LONG).show()
                    onSuccess()
                }
                viewModel.clearState()
            }
            is PasswordResetState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password", color = Color.White) },
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
            when (currentStep) {
                ResetStep.REQUEST_OTP -> {
                    Text(
                        text = "Enter your email to receive an OTP code.",
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = email, // Use collected state
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4A90E2),
                            unfocusedBorderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = viewModel::requestOtp,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState !is PasswordResetState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
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
                    OutlinedTextField(
                        value = otp, // Use collected state
                        onValueChange = viewModel::onOtpChange,
                        label = { Text("OTP Code") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4A90E2),
                            unfocusedBorderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = viewModel::verifyOtp,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState !is PasswordResetState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        if (uiState is PasswordResetState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Verify OTP")
                        }
                    }
                }
                ResetStep.RESET_PASSWORD -> {
                    Text(
                        text = "Enter your new password.",
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4A90E2),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4A90E2),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        if (uiState is PasswordResetState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Reset Password")
                        }
                    }
                }
                ResetStep.SUCCESS -> {
                    // Start from beginning or close
                }
            }
        }
    }
}
