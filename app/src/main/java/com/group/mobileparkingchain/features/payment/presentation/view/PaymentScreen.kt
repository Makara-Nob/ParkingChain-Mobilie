package com.group.mobileparkingchain.features.payment.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.enumuration.PaymentMethodType
import com.group.mobileparkingchain.features.payment.presentation.component.payment.CreditCardForm
import com.group.mobileparkingchain.features.payment.presentation.component.payment.DigitalWalletOptions
import com.group.mobileparkingchain.features.payment.presentation.component.payment.MockPaymentInfo
import com.group.mobileparkingchain.features.payment.presentation.component.payment.PaymentMethodChip
import com.group.mobileparkingchain.features.payment.data.PaymentInfo
import com.group.mobileparkingchain.util.PaymentUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    paymentInfo: PaymentInfo,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethodType.CREDIT_CARD) }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Payment Method",
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
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // ✅ Sticky total & pay button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text(
                        "$${String.format("%.2f", paymentInfo.total)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isProcessing = true
                        onPaymentSuccess()
                        isProcessing = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing && isFormValid(selectedPaymentMethod, cardNumber, expiryDate, cvv)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Confirm & Pay",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Booking Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "BOOKING DETAILS",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BookingSummaryRow("Parking Spot", "P-${paymentInfo.spotId}")
                    Spacer(modifier = Modifier.height(8.dp))
                    BookingSummaryRow("Duration", "${paymentInfo.duration} hours")
                    Spacer(modifier = Modifier.height(8.dp))
                    BookingSummaryRow("Start Time", dateFormat.format(Date(paymentInfo.startTime)))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Method Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PaymentMethodChip(
                    label = "Credit/Debit card",
                    isSelected = selectedPaymentMethod == PaymentMethodType.CREDIT_CARD,
                    onClick = { selectedPaymentMethod = PaymentMethodType.CREDIT_CARD },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    label = "Digital wallet",
                    isSelected = selectedPaymentMethod == PaymentMethodType.DIGITAL_WALLET,
                    onClick = { selectedPaymentMethod = PaymentMethodType.DIGITAL_WALLET },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    label = "Mock payment",
                    isSelected = selectedPaymentMethod == PaymentMethodType.MOCK_PAYMENT,
                    onClick = { selectedPaymentMethod = PaymentMethodType.MOCK_PAYMENT },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Form
            when (selectedPaymentMethod) {
                PaymentMethodType.CREDIT_CARD -> CreditCardForm(
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = PaymentUtils.formatCardNumber(it) },
                    expiryDate = expiryDate,
                    onExpiryDateChange = { expiryDate = PaymentUtils.formatExpiryDate(it) },
                    cvv = cvv,
                    onCvvChange = { cvv = it.filter { c -> c.isDigit() }.take(3) }
                )

                PaymentMethodType.DIGITAL_WALLET -> DigitalWalletOptions()
                PaymentMethodType.MOCK_PAYMENT -> MockPaymentInfo()
            }

            Spacer(modifier = Modifier.height(80.dp)) // for bottom padding
        }
    }
}

@Composable
fun BookingSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}


@Composable
fun LabeledField(label: String) {
    Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2196F3))
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color(0xFF1E2836),
    focusedContainerColor = Color(0xFF1E2836),
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = Color(0xFF2196F3),
    unfocusedTextColor = Color.White,
    focusedTextColor = Color.White
)

fun isFormValid(paymentMethod: PaymentMethodType, cardNumber: String, expiryDate: String, cvv: String): Boolean {
    return when (paymentMethod) {
        PaymentMethodType.CREDIT_CARD -> {
            cardNumber.replace(" ", "").length == 16 &&
                    expiryDate.matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}$")) &&
                    cvv.length == 3
        }
        PaymentMethodType.DIGITAL_WALLET,
        PaymentMethodType.MOCK_PAYMENT -> true
    }
}