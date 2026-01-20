package com.group.mobileparkingchain.features.payment.presentation.component.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun MockPaymentInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
        shape = RoundedCornerShape(sdp(12))
    ) {
        Column(modifier = Modifier.padding(sdp(20)), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🧪", fontSize = ssp(48))
            Spacer(modifier = Modifier.height(sdp(16)))
            Text("Mock Payment Mode", fontSize = ssp(18), fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(sdp(8)))
            Text(
                "This is a test payment method. Your booking will be confirmed instantly without any actual payment.",
                fontSize = ssp(14),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}