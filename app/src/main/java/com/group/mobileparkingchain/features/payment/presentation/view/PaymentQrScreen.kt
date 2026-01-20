package com.group.mobileparkingchain.features.payment.presentation.view

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import com.group.mobileparkingchain.utils.Formatters
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlinx.coroutines.delay

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentQrScreen(
    qrCodeBase64: String?,
    total: Double,
    currency: String,
    deeplink: String?,
    expiresAt: String?,
    status: String,
    onCancel: () -> Unit,
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
        Formatters.currency(total, currency, 0)
    } else {
        Formatters.currency(total, currency, 2)
    }

    val expiresAtMillis = remember(expiresAt) {
        try {
            if (expiresAt.isNullOrBlank()) {
                null
            } else {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                formatter.parse(expiresAt)?.time
            }
        } catch (e: Exception) {
            null
        }
    }

    var remainingSeconds by remember(expiresAtMillis) {
        mutableLongStateOf(
            if (expiresAtMillis != null) {
                ((expiresAtMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            } else {
                0L
            }
        )
    }

    LaunchedEffect(expiresAtMillis) {
        if (expiresAtMillis == null) {
            return@LaunchedEffect
        }

        while (true) {
            val secondsLeft = ((expiresAtMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            remainingSeconds = secondsLeft
            if (secondsLeft <= 0) {
                break
            }
            delay(1000)
        }
    }

    val statusLabel = status.trim().uppercase()
    val statusColor = when (statusLabel) {
        "PAID", "COMPLETED" -> Color(0xFF1B5E20)
        "CANCELLED", "EXPIRED", "FAILED" -> Color(0xFFB71C1C)
        else -> Color(0xFF7A6000)
    }

    Scaffold(
        containerColor = Color(0xFFF2F2F2),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Complete",
                        fontSize = ssp(20),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                actions = {
                    Text(
                        text = "3 of 3",
                        fontSize = ssp(14),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(end = sdp(16))
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
                .padding(horizontal = sdp(20), vertical = sdp(16)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(sdp(8)))

            Text(
                text = "Total: $formattedTotal",
                fontSize = ssp(18),
                color = Color(0xFF444444),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(sdp(6)))

            Text(
                text = "Payment Status: $statusLabel",
                fontSize = ssp(18),
                color = statusColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (expiresAtMillis != null) {
                Spacer(modifier = Modifier.height(sdp(6)))

                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                val countdown = String.format(Locale.US, "%02d:%02d", minutes, seconds)

                Text(
                    text = "Expires in $countdown",
                    fontSize = ssp(14),
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(sdp(18)))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White, RoundedCornerShape(sdp(12)))
                    .padding(sdp(16)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "ABA KHQR",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    CircularProgressIndicator(color = Color(0xFFE1232E))
                }
            }

            Spacer(modifier = Modifier.height(sdp(16)))

            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0534E)),
                shape = RoundedCornerShape(sdp(8)),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(
                    text = "Cancel Payment",
                    color = Color.White,
                    fontSize = ssp(16),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
