package com.group.mobileparkingchain.ui.components.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletOption(name: String, icon: String) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: handle wallet selection */ },
        color = Color(0xFF1E2836),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
            Text("→", fontSize = 20.sp, color = Color.Gray)
        }
    }
}