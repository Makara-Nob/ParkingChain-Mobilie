package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.features.home.data.ParkingSpot

@Composable
fun ParkingSpotCard(spot: ParkingSpot, onClick: () -> Unit) {
    val backgroundColor = when (spot.status) {
        ParkingStatus.AVAILABLE -> Color(0xFF1B4D2C)
        ParkingStatus.OCCUPIED -> Color(0xFF4D1B1B)
        ParkingStatus.RESERVED -> Color(0xFF1B2C4D)
    }
    val textColor = when (spot.status) {
        ParkingStatus.AVAILABLE -> Color(0xFF4CAF50)
        ParkingStatus.OCCUPIED -> Color(0xFFE53935)
        ParkingStatus.RESERVED -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = spot.status == ParkingStatus.AVAILABLE, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(spot.id, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(spot.type, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Text(if (spot.type.equals("Motorcycle", true)) "🏍️" else "🚗", fontSize = 28.sp)
        }
    }
}
