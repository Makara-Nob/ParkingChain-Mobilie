package com.group.mobileparkingchain.features.payment.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import com.group.mobileparkingchain.features.payment.data.PaymentInfo
import com.group.mobileparkingchain.utils.Formatters
import java.util.Locale
import com.group.mobileparkingchain.features.payment.presentation.component.payment.DigitalWalletOptions
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    paymentInfo: PaymentInfo,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit,
    onInitiatePayment: (String, Double, String) -> Unit,
    isProcessing: Boolean
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Complete",
                        fontSize = ssp(20),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    Text(
                        text = "2 of 3",
                        fontSize = ssp(14),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(end = sdp(16))
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(sdp(16))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = ssp(20), fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text(
                        "${Formatters.moneyWithSymbol(paymentInfo.total, "$")}",
                        fontSize = ssp(24),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(sdp(12)))

                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sdp(56)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = sdp(24))
        ) {
            Spacer(modifier = Modifier.height(sdp(24)))

            // Booking Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
                shape = RoundedCornerShape(sdp(16))
            ) {
                Column(modifier = Modifier.padding(sdp(20))) {
                    Text(
                        "BOOKING DETAILS",
                        fontSize = ssp(12),
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(sdp(12)))
                    BookingSummaryRow("Parking Spot", "P-${paymentInfo.spotId}")
                    Spacer(modifier = Modifier.height(sdp(8)))
                    BookingSummaryRow("Duration", "${paymentInfo.duration} hours")
                    Spacer(modifier = Modifier.height(sdp(8)))
                    BookingSummaryRow("Start Time", dateFormat.format(Date(paymentInfo.startTime)))
                }
            }

            Spacer(modifier = Modifier.height(sdp(24)))

            // Payment Method Section
            Text(
                "Select Payment Method",
                fontSize = ssp(20),
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(sdp(24)))

            // Only PayWay option
            DigitalWalletOptions(
                onOptionSelected = { wallet ->
                    if (wallet == "ABA_PAYWAY") {
                        onInitiatePayment(
                            paymentInfo.bookingId,
                            paymentInfo.total,
                            "payway"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(sdp(80)))
        }
    }
}

@Composable
fun BookingSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = ssp(14), color = Color.Gray)
        Text(value, fontSize = ssp(14), color = Color.White, fontWeight = FontWeight.Medium)
    }
}
