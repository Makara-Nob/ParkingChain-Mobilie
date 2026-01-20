package com.group.mobileparkingchain.features.payment.presentation.component.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
fun PaymentMethodChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface (
        modifier = modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF2196F3) else Color(0xFF1E2836),
        shape = RoundedCornerShape(sdp(12))
    ) {
        Box(
            modifier = Modifier.padding(vertical = sdp(16)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = ssp(12),
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}