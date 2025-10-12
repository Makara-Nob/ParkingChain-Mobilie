package com.group.mobileparkingchain.ui.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ParkingDetail(
    val id: String,
    val location: String,
    val type: String,
    val lastUpdated: String,
    val pricePerHour: Double,
    val status: String
)

enum class PaymentMethod {
    ABA_PAYWAY,
    CREDIT_CARD,
    WING,
    PI_PAY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailSheet(
    parkingDetail: ParkingDetail,
    onDismiss: () -> Unit,
    onReserve: (PaymentMethod) -> Unit
) {
    var selectedPayment by remember { mutableStateOf<PaymentMethod?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parkingDetail.id,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    color = Color(0xFF1B4D2C),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = parkingDetail.status,
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Location Info
            InfoRow(label = "Location", value = parkingDetail.location)
            Spacer(modifier = Modifier.height(16.dp))

            // Type Info
            InfoRow(label = "Type", value = parkingDetail.type)
            Spacer(modifier = Modifier.height(16.dp))

            // Last Updated Info
            InfoRow(label = "Last Updated", value = parkingDetail.lastUpdated)

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = Color.White.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(24.dp))

            // Pricing
            InfoRow(
                label = "Pricing",
                value = "$${String.format("%.2f", parkingDetail.pricePerHour)}/hr"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Method Section
            Text(
                text = "Payment Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ABA PayWay
            PaymentOption(
                name = "ABA PayWay",
                icon = "💳",
                isSelected = selectedPayment == PaymentMethod.ABA_PAYWAY,
                onClick = { selectedPayment = PaymentMethod.ABA_PAYWAY }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Credit Card
            PaymentOption(
                name = "Credit Card",
                icon = "💳",
                isSelected = selectedPayment == PaymentMethod.CREDIT_CARD,
                onClick = { selectedPayment = PaymentMethod.CREDIT_CARD }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Wing
            PaymentOption(
                name = "Wing",
                icon = "📱",
                isSelected = selectedPayment == PaymentMethod.WING,
                onClick = { selectedPayment = PaymentMethod.WING }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pi Pay
            PaymentOption(
                name = "Pi Pay",
                icon = "💰",
                isSelected = selectedPayment == PaymentMethod.PI_PAY,
                onClick = { selectedPayment = PaymentMethod.PI_PAY }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reserve Button
            Button(
                onClick = {
                    selectedPayment?.let { onReserve(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    disabledContainerColor = Color(0xFF2196F3).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedPayment != null
            ) {
                Text(
                    text = "Reserve Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel Button
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun PaymentOption(
    name: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF2196F3).copy(alpha = 0.2f) else Color(0xFF1E2836),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF2196F3),
                    unselectedColor = Color.Gray
                )
            )
        }
    }
}

// Example usage in HomeScreen - add this to your ParkingSpotCard
@Composable
fun ParkingSpotCardWithReservation(
    spot: com.group.mobileparkingchain.ui.screens.home.ParkingSpot,
    onSpotClick: () -> Unit
) {
    val backgroundColor = when (spot.status) {
        com.group.mobileparkingchain.ui.screens.home.ParkingStatus.AVAILABLE -> Color(0xFF1B4D2C)
        com.group.mobileparkingchain.ui.screens.home.ParkingStatus.OCCUPIED -> Color(0xFF4D1B1B)
        com.group.mobileparkingchain.ui.screens.home.ParkingStatus.RESERVED -> Color(0xFF1B2C4D)
    }

    val textColor = when (spot.status) {
        com.group.mobileparkingchain.ui.screens.home.ParkingStatus.AVAILABLE -> Color(0xFF4CAF50)
        com.group.mobileparkingchain.ui.screens.home.ParkingStatus.OCCUPIED -> Color(0xFFE53935)
        com.group.mobileparkingchain.ui.screens.home.ParkingStatus.RESERVED -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(
                enabled = spot.status == com.group.mobileparkingchain.ui.screens.home.ParkingStatus.AVAILABLE,
                onClick = onSpotClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                spot.id,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                spot.type,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                "🚗",
                fontSize = 32.sp
            )
        }
    }
}