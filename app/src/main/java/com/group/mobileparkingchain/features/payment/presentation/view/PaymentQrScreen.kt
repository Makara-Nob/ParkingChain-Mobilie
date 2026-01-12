package com.group.mobileparkingchain.features.payment.presentation.view

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PaymentQrScreen(
    qrCodeBase64: String?,
    total: Double,
    currency: String,
    deeplink: String?,
    onBack: () -> Unit
) {
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
        "${String.format(Locale.getDefault(), "%,.0f", total)} $currency"
    } else {
        "${String.format(Locale.getDefault(), "%,.2f", total)} $currency"
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
                .background(Color(0xFFE0E0E0)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (bitmap != null) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Red header with speech bubble style
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Red background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .background(
                                        Color(0xFFE1232E),
                                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.kh_logo),
                                    contentDescription = "KHQR",
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(35.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            
                            // Speech bubble tail (triangle at bottom right)
                            Canvas(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 24.dp, bottom = 0.dp)
                                    .size(20.dp, 12.dp)
                            ) {
                                val path = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(size.width, 0f)
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = androidx.compose.ui.graphics.Color(0xFFE1232E)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            // Description/Title
                            Text(
                                text = "Mobile Parking Chain",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF1E1E1E),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            
                            // Amount
                            Text(
                                text = formattedTotal,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000),
                                lineHeight = 40.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Dashed divider
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            ) {
                                drawLine(
                                    color = androidx.compose.ui.graphics.Color(0xFFD0D0D0),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // QR Code
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "ABA KHQR",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Red header with speech bubble style
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Red background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .background(
                                        Color(0xFFE1232E),
                                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.kh_logo),
                                    contentDescription = "KHQR",
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(35.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            
                            // Speech bubble tail
                            Canvas(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 24.dp, bottom = 0.dp)
                                    .size(20.dp, 12.dp)
                            ) {
                                val path = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(size.width, 0f)
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = androidx.compose.ui.graphics.Color(0xFFE1232E)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Mobile Parking Chain",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF1E1E1E),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            
                            Text(
                                text = formattedTotal,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000),
                                lineHeight = 40.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            ) {
                                drawLine(
                                    color = androidx.compose.ui.graphics.Color(0xFFD0D0D0),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFE1232E))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            // Additional info shown below the card
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Scan to Pay",
                fontSize = 14.sp,
                color = Color(0xFF1E2A3A),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Use any Mobile Banking app supporting KHQR",
                fontSize = 11.sp,
                color = Color(0xFF8A9BAE)
            )
        }
    }
}
