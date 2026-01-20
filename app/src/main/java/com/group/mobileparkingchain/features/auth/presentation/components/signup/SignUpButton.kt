package com.group.mobileparkingchain.features.auth.presentation.components.signup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun SignUpButton(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(sdp(56)),
        shape = RoundedCornerShape(sdp(12)),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        enabled = !isLoading && firstName.isNotEmpty() && lastName.isNotEmpty() &&
                email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(sdp(20))
            )
        } else {
            Text(text = "Sign Up", fontSize = ssp(18), fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
