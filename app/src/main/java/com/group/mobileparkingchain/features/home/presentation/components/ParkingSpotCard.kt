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
            // Status indicator badge (top-right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Spot ID
                Text(
                    text = spot.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                // Vehicle icon with background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = vehicleIcon,
                        contentDescription = spot.type,
                        modifier = Modifier.size(32.dp),
                        tint = accentColor
                    )
                }

                // Bottom section with type and status
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Vehicle type
                    Text(
                        text = spot.type,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )

                    // Status indicator with icon
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = statusText,
                            modifier = Modifier.size(12.dp),
                            tint = accentColor
                        )
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
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
