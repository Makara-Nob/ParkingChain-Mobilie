package com.group.mobileparkingchain.ui.screens

import NotificationType
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.features.notification.data.NotificationItem
import com.group.mobileparkingchain.features.notification.presentation.components.NotificationCard
import com.group.mobileparkingchain.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPage(
    onNavigateToHome: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedNavIndex by remember { mutableStateOf(2) }

    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem(
                    id = "1",
                    type = NotificationType.PARKING_AVAILABLE,
                    title = "Parking spot A-01 is now available",
                    time = "10:30AM",
                    isRead = false
                ),
                NotificationItem(
                    id = "2",
                    type = NotificationType.PARKING_ENDING,
                    title = "Your parking ends in 30 minutes",
                    time = "11:45AM",
                    isRead = false
                ),
                NotificationItem(
                    id = "3",
                    type = NotificationType.PAYMENT_SUCCESS,
                    title = "Payment successful for booking #1234",
                    time = "12:15 PM",
                    isRead = false
                ),
                NotificationItem(
                    id = "4",
                    type = NotificationType.CAR_SAFE,
                    title = "Your car is safe and sound.",
                    time = "Yesterday",
                    isRead = true
                ),
                NotificationItem(
                    id = "5",
                    type = NotificationType.BOOKING_SUCCESS,
                    title = "Parking B-05 booked successfully",
                    time = "Yesterday",
                    isRead = true
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = { index ->
                    selectedNavIndex = index
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToMap()
                        2 -> { /* Already on Notification */ }
                        3 -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(
                items = notifications,
                key = { it.id }
            ) { notification ->
                NotificationCard (
                    notification = notification,
                    onClick = {
                        notifications = notifications.map {
                            if (it.id == notification.id) it.copy(isRead = true) else it
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

