package com.group.mobileparkingchain.features.payment.presentation.component.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DigitalWalletOptions() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Digital Wallet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        WalletOption("ABA PayWay", "💳")
        Spacer(modifier = Modifier.height(12.dp))
        WalletOption("Wing Money", "🦅")
        Spacer(modifier = Modifier.height(12.dp))
        WalletOption("Pi Pay", "💰")
        Spacer(modifier = Modifier.height(12.dp))
        WalletOption("True Money", "💵")
    }
}