package com.group.mobileparkingchain.features.payment.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.di.PaymentModule
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.TransactionHistoryState
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.TransactionHistoryViewModel
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.TransactionHistoryViewModelFactory
import com.group.mobileparkingchain.network.datastore.TokenDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val paymentModule = remember { PaymentModule(context) }
    val tokenDataStore = remember { TokenDataStore(context) }
    val viewModel: TransactionHistoryViewModel = viewModel(
        factory = TransactionHistoryViewModelFactory(paymentModule)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    val userId by tokenDataStore.userId.collectAsState(initial = null)
    
    androidx.compose.runtime.LaunchedEffect(userId) {
        viewModel.loadTransactions(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is TransactionHistoryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF4A90E2)
                    )
                }
                is TransactionHistoryState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TransactionHistoryState.Success -> {
                    if (state.payments.isEmpty()) {
                        Text(
                            text = "No transactions found",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(sdp(16)),
                            verticalArrangement = Arrangement.spacedBy(sdp(12))
                        ) {
                            items(state.payments) { payment ->
                                TransactionItem(payment)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(payment: Payment) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(sdp(12)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(sdp(16))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment", // Could be booking ID or Description
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = ssp(16)
                )
                Text(
                    text = "${payment.amount} ${payment.currency}",
                    color = Color(0xFF4A90E2),
                    fontWeight = FontWeight.Bold,
                    fontSize = ssp(16)
                )
            }
            Spacer(modifier = Modifier.height(sdp(8)))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatPaymentDate(payment.createdAt),
                    color = Color.Gray,
                    fontSize = ssp(12)
                )
                
                // Status Pill
                val statusColor = when(payment.status.uppercase()) {
                    "PAID" -> Color.Green
                    "PENDING" -> Color.Yellow
                    "FAILED" -> Color.Red
                    else -> Color.Gray
                }
                
                Text(
                    text = payment.status.uppercase(),
                    color = statusColor,
                    fontSize = ssp(12),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatPaymentDate(raw: String): String {
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
        val formatter = java.text.SimpleDateFormat("MMM d, yyyy h:mm a", java.util.Locale.getDefault())
        val date = parser.parse(raw) ?: return raw
        formatter.format(date)
    } catch (e: Exception) {
        raw
    }
}
