package com.group.mobileparkingchain.features.auth.presentation.components.signin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue

@Composable
fun ForgotPasswordText(onForgotPassword: () -> Unit) {
    TextButton(onClick = onForgotPassword, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Forgot Password?", color = PrimaryBlue, fontSize = 14.sp)
    }
}