package com.group.mobileparkingchain.features.notification.presentation.components

import NotificationType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Get icon based on type
fun NotificationType.getIcon(): ImageVector {
    return when (this) {
        NotificationType.PARKING_AVAILABLE -> Icons.Default.LocalParking
        NotificationType.PARKING_ENDING -> Icons.Default.AccessTime
        NotificationType.PAYMENT_SUCCESS -> Icons.Default.CheckCircle
        NotificationType.CAR_SAFE -> Icons.Default.LocalParking
        NotificationType.BOOKING_SUCCESS -> Icons.Default.Person
    }
}

// Get icon background color
fun NotificationType.getIconColor(): Color {
    return when (this) {
        NotificationType.PARKING_AVAILABLE -> Color(0xFF2A5F8F)
        NotificationType.PARKING_ENDING -> Color(0xFF3D5A80)
        NotificationType.PAYMENT_SUCCESS -> Color(0xFF2A5F6F)
        NotificationType.CAR_SAFE -> Color(0xFF2A4F5F)
        NotificationType.BOOKING_SUCCESS -> Color(0xFF3A4F5F)
    }
}

