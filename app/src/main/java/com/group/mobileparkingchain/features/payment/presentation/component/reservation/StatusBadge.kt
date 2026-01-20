package com.group.mobileparkingchain.features.payment.presentation.component.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun StatusBadge(status: String, color: Color = Color(0xFF4CAF50)) {
    Surface(
        color = Color(0xFF1B4D2C),
        shape = RoundedCornerShape(sdp(20))
    ) {
        Row (
            modifier = Modifier.padding(horizontal = sdp(12), vertical = sdp(6)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(sdp(8))
                    .background(color, shape = RoundedCornerShape(sdp(4)))
            )
            Spacer(modifier = Modifier.width(sdp(8)))
            Text(
                text = status,
                color = color,
                fontSize = ssp(12),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
