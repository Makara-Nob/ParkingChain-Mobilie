package com.group.mobileparkingchain.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class PaymentInfo(
    val spotId: String,
    val duration: Int,
    val startTime: Long,
    val total: Double
)

enum class PaymentMethodType {
    CREDIT_CARD,
    DIGITAL_WALLET,
    MOCK_PAYMENT
}

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
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E2836)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
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
                    BookingSummaryRow(
                        "Start Time",
                        dateFormat.format(Date(paymentInfo.startTime))
                    )
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

            // Payment Form based on selected method
            when (selectedPaymentMethod) {
                PaymentMethodType.CREDIT_CARD -> {
                    CreditCardForm(
                        cardNumber = cardNumber,
                        onCardNumberChange = { cardNumber = it },
                        expiryDate = expiryDate,
                        onExpiryDateChange = { expiryDate = it },
                        cvv = cvv,
                        onCvvChange = { cvv = it }
                    )
                }
                PaymentMethodType.DIGITAL_WALLET -> {
                    DigitalWalletOptions()
                }
                PaymentMethodType.MOCK_PAYMENT -> {
                    MockPaymentInfo()
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Total and Pay Button
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    "$${String.format("%.2f", paymentInfo.total)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isProcessing = true
                    // Simulate payment processing
                    // In real app, call payment API here
                    onPaymentSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
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
                        text = "Confirm & pay",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BookingSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PaymentMethodChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF2196F3) else Color(0xFF1E2836),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Gray,
                maxLines = 2
            )
        }
    }
}

@Composable
fun CreditCardForm(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit
) {
    Column {
        // Card Number
        Text(
            "Card Number",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2196F3)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { if (it.length <= 16) onCardNumberChange(it.filter { char -> char.isDigit() }) },
            placeholder = { Text("Enter Card number", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF1E2836),
                focusedContainerColor = Color(0xFF1E2836),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Expiry Date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Expiry Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        if (it.length <= 5) {
                            val filtered = it.filter { char -> char.isDigit() || char == '/' }
                            onExpiryDateChange(filtered)
                        }
                    },
                    placeholder = { Text("MM/YY", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF1E2836),
                        focusedContainerColor = Color(0xFF1E2836),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // CVV
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CVV",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) onCvvChange(it.filter { char -> char.isDigit() }) },
                    placeholder = { Text("123", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF1E2836),
                        focusedContainerColor = Color(0xFF1E2836),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun DigitalWalletOptions() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Select Digital Wallet",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        WalletOption("ABA PayWay", "💳")
        Spacer(modifier = Modifier.height(12.dp))
        WalletOption("Wing Money", "🦅")
        Spacer(modifier = Modifier.height(12.dp))
        WalletOption("Pi Pay", "💰")
        Spacer(modifier = Modifier.height(12.dp))
        WalletOption("True Money", "💵")
    }
}

@Composable
fun WalletOption(name: String, icon: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle wallet selection */ },
        color = Color(0xFF1E2836),
        shape = RoundedCornerShape(12.dp)
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
                Text(text = icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Text(
                text = "→",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MockPaymentInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2836)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🧪",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Mock Payment Mode",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This is a test payment method. Your booking will be confirmed instantly without any actual payment.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun isFormValid(
    paymentMethod: PaymentMethodType,
    cardNumber: String,
    expiryDate: String,
    cvv: String
): Boolean {
    return when (paymentMethod) {
        PaymentMethodType.CREDIT_CARD -> {
            cardNumber.length == 16 && expiryDate.isNotEmpty() && cvv.length == 3
        }
        PaymentMethodType.DIGITAL_WALLET,
        PaymentMethodType.MOCK_PAYMENT -> true
    }
}