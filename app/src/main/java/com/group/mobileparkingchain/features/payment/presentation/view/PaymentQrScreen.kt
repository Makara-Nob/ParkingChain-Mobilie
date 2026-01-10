package com.group.mobileparkingchain.features.payment.presentation.view

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.R
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PaymentQrScreen(
    qrCodeBase64: String?,
    total: Double,
    currency: String,
    deeplink: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    // Decode bitmap
    val bitmap = remember(qrCodeBase64) {
        try {
            if (!qrCodeBase64.isNullOrEmpty()) {
                val cleanBase64 = if (qrCodeBase64.contains(",")) {
                    qrCodeBase64.substringAfter(",")
                } else {
                    qrCodeBase64
                }
                val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    val formattedTotal = if (currency == "KHR") {
        "${String.format("%,.0f", total)} $currency"
    } else {
        "${String.format("%,.2f", total)} $currency"
    }

    Scaffold(
        containerColor = Color(0xFFE0E0E0),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "ABA KHQR",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E2A3A)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE0E0E0)) // Light background
                .padding(horizontal = 0.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if (bitmap != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "ABA KHQR",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
            } else {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(Color(0xFFE1232E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.khqr_logo),
                                contentDescription = "KHQR",
                                modifier = Modifier.height(36.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Mobile Parking Chain App",
                                fontSize = 12.sp,
                                color = Color(0xFF6B778C),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = formattedTotal,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2A3A)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            ) {
                                drawLine(
                                    color = Color(0xFFB0B8C4),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier.size(220.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFE1232E))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Scan to Pay",
                fontSize = 14.sp,
                color = Color(0xFF1E2A3A),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "or",
                fontSize = 12.sp,
                color = Color(0xFF8A9BAE)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { /* TODO: implement download if needed */ }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download QR",
                    tint = Color(0xFF4A90E2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download QR",
                    color = Color(0xFF4A90E2),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "and upload to Mobile Banking app supporting KHQR",
                fontSize = 11.sp,
                color = Color(0xFF8A9BAE)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal:",
                    fontSize = 12.sp,
                    color = Color(0xFF8A9BAE)
                )
                Text(
                    text = formattedTotal,
                    fontSize = 12.sp,
                    color = Color(0xFF1E2A3A),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Waiting for payment confirmation...",
                fontSize = 12.sp,
                color = Color(0xFF8A9BAE)
            )
        }
    }
}
