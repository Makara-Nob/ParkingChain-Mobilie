package com.group.mobileparkingchain.ui.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.R
import com.group.mobileparkingchain.features.payment.presentation.component.payment.PriceRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPaymentScreen(
    bookingInfo: BookingInfo,
    duration: Int,
    startTime: Long,
    total: Double,
    onBackClick: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    // Payment method fixed to PayWay/KHQR, currency fixed to USD (backend will handle)
    val selectedPaymentMethod = "payway"
    val selectedCurrency = "USD" // Backend default (KHQR_CURRENCY=USD)
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Payment Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "BOOKING SUMMARY",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Parking Spot", fontSize = 14.sp, color = Color.Gray)
                        Text("P-${bookingInfo.spotId}", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start Time", fontSize = 14.sp, color = Color.Gray)
                        Text(dateFormat.format(Date(startTime)), fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duration", fontSize = 14.sp, color = Color.Gray)
                        Text("$duration hrs", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Payment Method Selection
            Text(
                "Payment Method",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Vertical List of Payment Methods (Rows)
            Column(
                modifier = Modifier.fillMaxWidth(), 
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // PayWay Option (Only payment method available - always selected)
                PaymentMethodRow(
                    title = "PayWay",
                    logoResId = R.drawable.khqr_logo,
                    isSelected = true,
                    onClick = { /* No-op: only one option */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price Calculation (use backend-provided total)
            val displaySymbol = "$"
            val formattedTotal = "$displaySymbol${String.format("%.2f", total)}"

            Text(
                "Total Amount",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            PriceRow(
                "Total",
                formattedTotal,
                isTotal = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            Button(
                onClick = {
                     onConfirm(selectedPaymentMethod, selectedCurrency)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    disabledContainerColor = Color(0xFF2196F3).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Confirm & Pay",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    logoResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2836)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                 Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = title,
                    modifier = Modifier.width(40.dp).height(40.dp).padding(end = 16.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.Gray
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
