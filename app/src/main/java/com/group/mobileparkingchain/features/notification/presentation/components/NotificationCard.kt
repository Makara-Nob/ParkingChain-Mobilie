package com.group.mobileparkingchain.features.notification.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.features.notification.data.NotificationItem
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp


@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = Color(0xFF1E1E1E),
                shape = MaterialTheme.shapes.medium
            )
            .padding(sdp(16)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(sdp(48))
                .background(
                    color = notification.type.getIconColor(),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = notification.type.getIcon(),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(sdp(24))
            )
        }

        Spacer(modifier = Modifier.width(sdp(16)))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontSize = ssp(15),
                fontWeight = FontWeight.Medium,
                color = if (notification.isRead) Color(0xFF888888) else Color.White,
                lineHeight = ssp(20)
            )
            Spacer(modifier = Modifier.height(sdp(4)))
            Text(
                text = notification.time,
                fontSize = ssp(13),
                color = Color(0xFF666666)
            )
        }

        // Unread dot
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(sdp(8)))
            Box(
                modifier = Modifier
                    .size(sdp(8))
                    .background(
                        color = Color(0xFF4A90E2),
                        shape = CircleShape
                    )
            )
        }
    }
}
