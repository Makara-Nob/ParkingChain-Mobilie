package com.group.mobileparkingchain.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.components.LoadingSpinner
import com.group.mobileparkingchain.ui.components.ParkingLogo
import com.group.mobileparkingchain.ui.theme.SmartParkingTheme
import com.group.mobileparkingchain.ui.theme.TextGray
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit = {}
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        // Navigate to home after 3 seconds
        delay(3000)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo
            ParkingLogo(size = sdp(200))

            // Tagline
            Text(
                text = "Find. Reserved. Park.",
                fontSize = ssp(18),
                color = TextGray,
                letterSpacing = ssp(1)
            )

            Spacer(modifier = Modifier.height(sdp(200)))

            // Loading Spinner
            LoadingSpinner()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    SmartParkingTheme {
        WelcomeScreen()
    }
}
