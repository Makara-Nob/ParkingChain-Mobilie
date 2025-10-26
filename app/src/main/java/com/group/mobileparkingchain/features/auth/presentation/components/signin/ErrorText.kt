package com.group.mobileparkingchain.features.auth.presentation.components.signin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun ErrorText(message: String) {
    Text(text = message, color = Color.Red, fontSize = 14.sp)
}