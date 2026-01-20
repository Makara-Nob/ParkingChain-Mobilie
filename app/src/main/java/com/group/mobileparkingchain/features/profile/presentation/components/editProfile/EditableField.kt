package com.group.mobileparkingchain.features.profile.presentation.components.editProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = ssp(14),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = sdp(8))
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(sdp(12)),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF1E2836),
                focusedContainerColor = Color(0xFF1E2836),
                disabledContainerColor = Color(0xFF1A1A1A),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF2196F3),
                disabledBorderColor = Color.Transparent,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                disabledTextColor = Color.Gray,
                cursorColor = Color(0xFF2196F3)
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}
