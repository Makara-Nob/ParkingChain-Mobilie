package com.group.mobileparkingchain.ui.screens.payment

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
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
                    onCardNumberChange = { cardNumber = formatCardNumber(it) },
                    expiryDate = expiryDate,
                    onExpiryDateChange = { expiryDate = formatExpiryDate(it) },
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
fun PaymentMethodChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
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
                textAlign = TextAlign.Center
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
        LabeledField("Card Number")
        OutlinedTextField(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            placeholder = { Text("XXXX XXXX XXXX XXXX", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                LabeledField("Expiry Date")
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = onExpiryDateChange,
                    placeholder = { Text("MM/YY", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                LabeledField("CVV")
                OutlinedTextField(
                    value = cvv,
                    onValueChange = onCvvChange,
                    placeholder = { Text("***", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors()
                )
            }
        }
    }
}

@Composable
fun DigitalWalletOptions() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Digital Wallet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
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
            .clickable { /* TODO: handle wallet selection */ },
        color = Color(0xFF1E2836),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
            Text("→", fontSize = 20.sp, color = Color.Gray)
        }
    }
}

@Composable
fun MockPaymentInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🧪", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mock Payment Mode", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This is a test payment method. Your booking will be confirmed instantly without any actual payment.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
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

// ✅ Format helper functions
fun formatCardNumber(input: String): String {
    val digits = input.filter { it.isDigit() }.take(16)
    return digits.chunked(4).joinToString(" ")
}

fun formatExpiryDate(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4)
    return when {
        digits.length >= 3 -> digits.substring(0, 2) + "/" + digits.substring(2)
        digits.length >= 1 -> digits
        else -> ""
    }
}
