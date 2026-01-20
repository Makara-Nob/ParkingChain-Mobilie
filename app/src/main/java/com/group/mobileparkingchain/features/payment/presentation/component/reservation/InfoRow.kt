package com.group.mobileparkingchain.features.payment.presentation.component.reservation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = ssp(14),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(sdp(4)))
        Text(
            text = value,
            fontSize = ssp(16),
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

