package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Legend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2836)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Status", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Spacer(Modifier.height(12.dp))
            LegendItem(Color(0xFF4CAF50), "Available")
            Spacer(Modifier.height(8.dp))
            LegendItem(Color(0xFFE53935), "Occupied")
            Spacer(Modifier.height(8.dp))
            LegendItem(Color(0xFF2196F3), "Reserved")
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, shape = RoundedCornerShape(6.dp)))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
    }
}
