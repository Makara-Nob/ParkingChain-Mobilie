package com.group.mobileparkingchain.features.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue
import com.group.mobileparkingchain.ui.theme.TextGray

@Composable
fun SignUpRow(onNavigateToSignUp: () -> Unit) {
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Don't have an account? ", color = TextGray, fontSize = 14.sp)
        TextButton(onClick = onNavigateToSignUp) {
            Text(text = "Sign Up", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}