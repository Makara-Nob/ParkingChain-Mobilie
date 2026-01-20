package com.group.mobileparkingchain.features.payment.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.group.mobileparkingchain.features.parking.data.model.Booking
import com.group.mobileparkingchain.utils.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingReceiptScreen(
    booking: Booking,
    onDoneClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    
    Scaffold(
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(sdp(24)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(sdp(40)))
            
            // Success Icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(sdp(100)),
                tint = Color(0xFF4CAF50)
            )
            
            Spacer(modifier = Modifier.height(sdp(24)))
            
            // Success Title
            Text(
                text = "Booking Successful!",
                fontSize = ssp(28),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(sdp(8)))
            
            Text(
                text = "Your parking spot has been reserved",
                fontSize = ssp(16),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(sdp(32)))
            
            // Booking Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
                shape = RoundedCornerShape(sdp(16))
            ) {
                Column(
                    modifier = Modifier.padding(sdp(24))
                ) {
                    Text(
                        "BOOKING DETAILS",
                        fontSize = ssp(12),
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(sdp(16)))
                    
                    // Booking ID
                    ReceiptRow("Booking ID", booking.id.takeLast(8).uppercase())
                    
                    Spacer(modifier = Modifier.height(sdp(12)))
                    Divider(color = Color(0xFF2C3E50))
                    Spacer(modifier = Modifier.height(sdp(12)))
                    
                    // Spot ID
                    ReceiptRow("Parking Spot", "P-${booking.spotId}")
                    
                    Spacer(modifier = Modifier.height(sdp(12)))
                    
                    // Start Time
                    ReceiptRow("Start Time", dateFormat.format(parseIsoDate(booking.startTime)))
                    
                    Spacer(modifier = Modifier.height(sdp(12)))
                    
                    // Duration
                    ReceiptRow("Duration", "${booking.durationHours ?: 0} hours")
                    
                    Spacer(modifier = Modifier.height(sdp(12)))
                    Divider(color = Color(0xFF2C3E50))
                    Spacer(modifier = Modifier.height(sdp(12)))
                    
                    // Total Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total Paid",
                            fontSize = ssp(16),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${Formatters.moneyWithSymbol(booking.totalPrice ?: 0.0, "$")}",
                            fontSize = ssp(24),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // Done Button
            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sdp(56)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(sdp(12))
            ) {
                Text(
                    "Done",
                    fontSize = ssp(16),
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(sdp(24)))
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = ssp(14), color = Color.Gray)
        Text(value, fontSize = ssp(14), color = Color.White, fontWeight = FontWeight.Medium)
    }
}

fun parseIsoDate(isoString: String?): Date {
    if (isoString == null) return Date()
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        format.parse(isoString) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}
