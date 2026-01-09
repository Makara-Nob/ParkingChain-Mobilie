package com.group.mobileparkingchain.features.payment.presentation.component.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.R

@Composable
fun DigitalWalletOptions(onOptionSelected: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf("KHQR") }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // KHQR (Bakong) Option Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectedOption = "KHQR"
                    onOptionSelected("KHQR")
                },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedOption == "KHQR") 
                    Color(0xFF2196F3).copy(alpha = 0.1f) 
                else 
                    Color(0xFF1E2836)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // KHQR Logo
                    Image(
                        painter = painterResource(id = R.drawable.khqr_logo),
                        contentDescription = "KHQR Logo",
                        modifier = Modifier
                            .size(48.dp),
                        contentScale = ContentScale.Fit
                    )
                    
                    Column {
                        Text(
                            text = "KHQR Payment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pay with Bakong KHQR",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // Radio Button
                RadioButton(
                    selected = selectedOption == "KHQR",
                    onClick = {
                        selectedOption = "KHQR"
                        onOptionSelected("KHQR")
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF2196F3),
                        unselectedColor = Color.Gray
                    )
                )
            }
        }
    }
}
