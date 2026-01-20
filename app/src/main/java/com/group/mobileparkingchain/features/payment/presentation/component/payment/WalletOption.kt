package com.group.mobileparkingchain.features.payment.presentation.component.payment

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
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun WalletOption(name: String, icon: String) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: handle wallet selection */ },
        color = Color(0xFF1E2836),
        shape = RoundedCornerShape(sdp(12))
    ) {
        Row (
            modifier = Modifier.padding(sdp(16)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = ssp(24))
                Spacer(modifier = Modifier.width(sdp(12)))
                Text(name, fontSize = ssp(16), fontWeight = FontWeight.Medium, color = Color.White)
            }
            Text("→", fontSize = ssp(20), color = Color.Gray)
        }
    }
}