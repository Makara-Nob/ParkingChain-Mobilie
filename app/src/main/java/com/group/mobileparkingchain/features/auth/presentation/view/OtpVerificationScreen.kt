package com.group.mobileparkingchain.features.auth.presentation.view

import Resource
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.OtpVerificationViewModel
import com.group.mobileparkingchain.features.auth.presentation.viewmodel.OtpVerificationViewModelFactory
import com.group.mobileparkingchain.ui.components.ParkingLogo
import com.group.mobileparkingchain.network.RetrofitInstance
import com.group.mobileparkingchain.features.auth.data.repository.AuthRepository
import com.group.mobileparkingchain.features.auth.domain.VerifyEmailUseCase
import com.group.mobileparkingchain.network.datastore.TokenDataStore

@Composable
fun OtpVerificationScreen(
    email: String,
    onVerificationSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val viewModel: OtpVerificationViewModel = viewModel(
        factory = OtpVerificationViewModelFactory(
            VerifyEmailUseCase(
                AuthRepository(
                    RetrofitInstance.authApi,
                    TokenDataStore(context)
                )
            )
        )
    )

    var otp by remember { mutableStateOf("") }
    val verificationState by viewModel.verificationState.collectAsState()

    LaunchedEffect(verificationState) {
        when (val state = verificationState) {
            is Resource.Success -> {
                Toast.makeText(
                    context,
                    "Email verified successfully! 🎉",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearState()
                onVerificationSuccess()
            }
            is Resource.Error -> {
                Toast.makeText(
                    context,
                    state.message,
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
            ParkingLogo(size = 150)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Verify Your Email",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "We've sent a verification code to",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // OTP Input Field
            OutlinedTextField(
                value = otp,
                onValueChange = { 
                    if (it.length <= 6) otp = it 
                },
                label = { Text("Enter OTP", color = Color.Gray) },
                placeholder = { Text("123456", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00C853),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFF00C853)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Verify Button
            Button(
                onClick = {
                    if (otp.length == 6) {
                        viewModel.verifyEmail(email, otp)
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter a valid 6-digit OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                enabled = verificationState !is Resource.Loading
            ) {
                if (verificationState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Verify Email",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Back to Sign In
            TextButton(onClick = onNavigateBack) {
                Text(
                    text = "Back to Sign In",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
