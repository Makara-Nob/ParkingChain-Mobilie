package com.group.mobileparkingchain.features.auth.presentation.view

import com.group.mobileparkingchain.core.Resource
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
import com.group.mobileparkingchain.features.auth.presentation.components.verification.OtpInputField
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp


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

    // Auto-submit when OTP is complete (6 digits)
    LaunchedEffect(otp) {
        if (otp.length == 6 && verificationState !is Resource.Loading) {
            viewModel.verifyEmail(email, otp)
        }
    }

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
                // Clear OTP on error for retry
                otp = ""
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
            ParkingLogo(size = sdp(150))
            
            Spacer(modifier = Modifier.height(sdp(24)))
            
            Text(
                text = "Verify Your Email",
                fontSize = ssp(28),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(sdp(12)))
            
            Text(
                text = "We've sent a verification code to",
                fontSize = ssp(14),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = email,
                fontSize = ssp(14),
                color = Color.White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(sdp(32)))
            
            // OTP Input Field
            OtpInputField(
                otp = otp,
                onOtpChange = { newOtp ->
                    if (newOtp.length <= 6) {
                        otp = newOtp
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(sdp(32)))
            
            // Verify Button (matching PasswordReset style)
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
                    .height(sdp(56)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), // Changed to blue to match PrimaryBlue
                enabled = verificationState !is Resource.Loading
            ) {
                if (verificationState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(sdp(24))
                    )
                } else {
                    Text(
                        text = "Verify Email",
                        fontSize = ssp(16),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(sdp(24)))
            
            // Resend OTP
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Didn't receive the code? ",
                    color = Color.Gray,
                    fontSize = ssp(14)
                )
                TextButton(
                    onClick = {
                        // TODO: Implement resend OTP logic
                        Toast.makeText(
                            context,
                            "OTP resent to $email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(
                        text = "Resend",
                        color = Color(0xFF2196F3),
                        fontSize = ssp(14),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
