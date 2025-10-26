package com.group.mobileparkingchain.features.payment.presentation.component.reservation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    filled: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val shape = RoundedCornerShape(12.dp)
    if (filled) {
        Button(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = shape
        ) {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = SolidColor(Color.White.copy(alpha = 0.3f))
            ),
            shape = shape
        ) {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
