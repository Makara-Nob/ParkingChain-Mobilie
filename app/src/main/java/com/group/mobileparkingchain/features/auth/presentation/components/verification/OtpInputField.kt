package com.group.mobileparkingchain.features.auth.presentation.components.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    length: Int = 6
) {
    BasicTextField(
        value = otp,
        onValueChange = {
            if (it.length <= length && it.all { char -> char.isDigit() }) {
                onOtpChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(length) { index ->
                    val char = if (index < otp.length) otp[index].toString() else ""
                    val isFocused = index == otp.length
                    val isFilled = index < otp.length

                    val borderColor = when {
                        isFocused -> Color(0xFF00C853)
                        isFilled -> Color(0xFF4A90E2)
                        else -> Color(0xFF2C3E50)
                    }
                    
                    val borderWidth = if (isFocused) 2.dp else 1.dp

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color(0xFF1E2A3A),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = borderWidth,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}
