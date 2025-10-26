package com.group.mobileparkingchain.features.auth.presentation.components.signup

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
fun SignUpButton(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    Button(
        onClick = {
            when {
                firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() -> onFailure("All fields are required")
                !email.contains("@") -> onFailure("Invalid email address")
                password.length < 6 -> onFailure("Password must be at least 6 characters")
                password != confirmPassword -> onFailure("Passwords do not match")
                else -> onSuccess()
            }
        },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        enabled = firstName.isNotEmpty() && lastName.isNotEmpty() &&
                  email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
    ) {
        Text(text = "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}