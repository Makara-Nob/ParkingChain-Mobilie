package com.group.mobileparkingchain.features.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue

@Composable
fun SignInButton(
    email: String,
    password: String,
    onSignInSuccess: () -> Unit,
    onSignInFailure: () -> Unit
) {
    Button (
        onClick = {
            if (email == "user@example.com" && password == "88889999") {
                onSignInSuccess()
            } else {
                onSignInFailure()
            }
        },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        enabled = email.isNotEmpty() && password.isNotEmpty()
    ) {
        Text(text = "Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}