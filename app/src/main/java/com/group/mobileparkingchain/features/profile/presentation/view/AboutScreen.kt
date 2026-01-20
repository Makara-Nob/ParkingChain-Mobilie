package com.group.mobileparkingchain.features.profile.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontSize = ssp(20), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
                .padding(horizontal = sdp(24), vertical = sdp(16))
        ) {
            Text(
                text = "SmartParking",
                fontSize = ssp(18),
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(sdp(8)))

            Text(
                text = "Version 0.1.2",
                fontSize = ssp(14),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(sdp(24)))

            Text(
                text = "Developed by",
                fontSize = ssp(16),
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(sdp(12)))

            Text(text = "1. SOKNA Chun", fontSize = ssp(14), color = Color.Gray)
            Spacer(modifier = Modifier.height(sdp(6)))
            Text(text = "2. Nob Makara", fontSize = ssp(14), color = Color.Gray)
            Spacer(modifier = Modifier.height(sdp(6)))
            Text(text = "3. Buot Sreychea", fontSize = ssp(14), color = Color.Gray)
            Spacer(modifier = Modifier.height(sdp(6)))
            Text(text = "4. Doung Chanvattana", fontSize = ssp(14), color = Color.Gray)
            Spacer(modifier = Modifier.height(sdp(6)))
            Text(text = "5. Torng Mengheng", fontSize = ssp(14), color = Color.Gray)
        }
    }
}
