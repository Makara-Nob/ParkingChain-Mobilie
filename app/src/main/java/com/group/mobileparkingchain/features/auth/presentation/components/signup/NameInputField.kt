package com.group.mobileparkingchain.features.auth.presentation.components.signup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue
import com.group.mobileparkingchain.ui.theme.TextGray
import com.group.mobileparkingchain.ui.theme.sdp

@Composable
fun NameInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = TextGray) }, // Changed from label to placeholder
        leadingIcon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Person, contentDescription = label, tint = TextGray) },
        modifier = Modifier.fillMaxWidth().height(sdp(60)),
        shape = RoundedCornerShape(sdp(12)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color(0xFF334155),
            focusedContainerColor = Color(0xFF1E293B),
            unfocusedContainerColor = Color(0xFF1E293B),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = PrimaryBlue
        ),
        singleLine = true
    )
}
