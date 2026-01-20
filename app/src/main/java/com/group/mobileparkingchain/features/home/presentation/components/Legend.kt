package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun Legend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
        shape = RoundedCornerShape(sdp(16))
    ) {
        Column(modifier = Modifier.padding(sdp(18))) {
            Text(
                text = "Parking Status",
                fontSize = ssp(16),
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.height(sdp(14)))
            LegendItem(
                color = Color(0xFF4ADE80),
                icon = Icons.Filled.CheckCircle,
                label = "Available"
            )
            Spacer(Modifier.height(sdp(10)))
            LegendItem(
                color = Color(0xFFEF4444),
                icon = Icons.Filled.Lock,
                label = "Occupied"
            )
            Spacer(Modifier.height(sdp(10)))
            LegendItem(
                color = Color(0xFF3B82F6),
                icon = Icons.Filled.Schedule,
                label = "Reserved"
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(sdp(32))
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(sdp(18)),
                tint = color
            )
        }
        Spacer(modifier = Modifier.width(sdp(12)))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = ssp(14),
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.sp
        )
    }
}
