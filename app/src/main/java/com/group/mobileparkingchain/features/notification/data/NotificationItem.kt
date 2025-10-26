package com.group.mobileparkingchain.features.notification.data

import NotificationType


data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val time: String,
    val isRead: Boolean = false
)
