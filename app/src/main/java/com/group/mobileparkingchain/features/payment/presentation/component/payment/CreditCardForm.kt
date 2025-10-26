package com.group.mobileparkingchain.features.payment.presentation.component.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.features.payment.presentation.view.LabeledField
import com.group.mobileparkingchain.features.payment.presentation.view.textFieldColors

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

        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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