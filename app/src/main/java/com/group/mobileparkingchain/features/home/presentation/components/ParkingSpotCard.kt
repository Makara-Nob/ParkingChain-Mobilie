package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.features.home.data.ParkingSpot

@Composable
fun ParkingSpotCard(spot: ParkingSpot, onClick: () -> Unit) {
    // Define color schemes for each status
    val (gradientColors, accentColor, statusIcon, statusText) = when (spot.status) {
        ParkingStatus.AVAILABLE -> StatusTheme(
            gradientColors = listOf(Color(0xFF1E3A2B), Color(0xFF2D5F42)),
            accentColor = Color(0xFF4ADE80),
            icon = Icons.Filled.CheckCircle,
            text = "Available"
        )
        ParkingStatus.OCCUPIED -> StatusTheme(
            gradientColors = listOf(Color(0xFF3A1E1E), Color(0xFF5F2D2D)),
            accentColor = Color(0xFFEF4444),
            icon = Icons.Filled.Lock,
            text = "Occupied"
        )
        ParkingStatus.RESERVED -> StatusTheme(
            gradientColors = listOf(Color(0xFF1E2A3A), Color(0xFF2D425F)),
            accentColor = Color(0xFF3B82F6),
            icon = Icons.Filled.Schedule,
            text = "Reserved"
        )
    }

    // Vehicle type icon
    val vehicleIcon = if (spot.type.equals("Motorcycle", true)) {
        Icons.Filled.TwoWheeler
    } else {
        Icons.Filled.DirectionsCar
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = if (spot.status == ParkingStatus.AVAILABLE) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = accentColor.copy(alpha = 0.3f)
            )
            .clickable(enabled = spot.status == ParkingStatus.AVAILABLE, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    )
                )
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Spot ID at top (left-aligned to avoid dot)
                Text(
                    text = spot.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                // Center: Vehicle icon
                Icon(
                    imageVector = vehicleIcon,
                    contentDescription = spot.type,
                    modifier = Modifier.size(40.dp),
                    tint = accentColor
                )

                // Bottom: Vehicle type text
                Text(
                    text = spot.type,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1
                )
            }

            // Overlay for non-available spots
            if (spot.status != ParkingStatus.AVAILABLE) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            }
        }
    }
}

// Helper data class for status theming
private data class StatusTheme(
    val gradientColors: List<Color>,
    val accentColor: Color,
    val icon: ImageVector,
    val text: String
)
