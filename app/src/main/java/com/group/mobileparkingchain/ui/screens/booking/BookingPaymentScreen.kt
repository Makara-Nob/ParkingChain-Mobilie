package com.group.mobileparkingchain.ui.screens.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.group.mobileparkingchain.features.payment.presentation.view.BookingInfo
import java.text.SimpleDateFormat
import com.group.mobileparkingchain.utils.Formatters
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPaymentScreen(
    bookingInfo: BookingInfo,
    duration: Int,
    startTime: Long,
    total: Double,
    onBackClick: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isProcessing: Boolean
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
                .padding(horizontal = sdp(16))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(sdp(16)))

            // Payment Method Selection
            Text(
                "Payment Method",
                fontSize = ssp(20),
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(sdp(12)))
            
            // Vertical List of Payment Methods (Rows)
            Column(
                modifier = Modifier.fillMaxWidth(), 
                verticalArrangement = Arrangement.spacedBy(sdp(12))
            ) {
                // PayWay Option (Only payment method available - always selected)
                PaymentMethodRow(
                    title = "ABA KHQR",
                    description = "Scan to pay with any banking app",
                    logoAssetPath = "file:///android_asset/images/payway-icon.svg",
                    isSelected = true,
                    onClick = { /* No-op: only one option */ }
                )
            }

            val displaySymbol = "$"
            val formattedTotal = Formatters.moneyWithSymbol(total, displaySymbol)

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            Button(
                onClick = {
                    if (!isProcessing) {
                        onConfirm(selectedPaymentMethod, selectedCurrency)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sdp(56)),
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    disabledContainerColor = Color(0xFF2196F3).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(sdp(12))
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = sdp(2),
                        modifier = Modifier
                            .width(sdp(20))
                            .height(sdp(20))
                    )
                } else {
                    Text(
                        text = "Confirm & Pay ($formattedTotal)",
                        fontSize = ssp(16),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(sdp(24)))
        }
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    description: String,
    logoAssetPath: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(sdp(72)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2836)
        ),
        shape = RoundedCornerShape(sdp(12)),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(sdp(2), Color(0xFF2196F3)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = sdp(16)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(logoAssetPath)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = title,
                    modifier = Modifier
                        .width(sdp(40))
                        .height(sdp(40))
                        .padding(end = sdp(16)),
                    contentScale = ContentScale.Fit
                )
                Column {
                    Text(
                        text = title,
                        fontSize = ssp(16),
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(sdp(2)))
                    Text(
                        text = description,
                        fontSize = ssp(12),
                        color = Color(0xFF8A9BAE)
                    )
                }
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
