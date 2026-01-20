package com.group.mobileparkingchain.features.booking.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.features.parking.data.model.BookingStatus
import com.group.mobileparkingchain.ui.theme.sdp

@Composable
fun BookingFilterChips(
    selectedStatus: BookingStatus?, 
    onStatusSelected: (BookingStatus?) -> Unit
) {
    val filters = listOf(
        BookingFilterItem(null, "All", null, null),
        BookingFilterItem(BookingStatus.ACTIVE, "Active", Color(0xFF4ADE80), Icons.Filled.CheckCircle),
        BookingFilterItem(BookingStatus.COMPLETED, "Completed", Color(0xFF3B82F6), Icons.Filled.CheckCircle),
        BookingFilterItem(BookingStatus.RESERVED, "Reserved", Color(0xFFFFC107), Icons.Filled.Schedule),
        BookingFilterItem(BookingStatus.CANCELLED, "Cancelled", Color(0xFFEF4444), Icons.Filled.Cancel)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(sdp(8))
    ) {
        filters.forEach { item ->
            val isSelected = selectedStatus == item.status
            // Use item color for background when selected, otherwise dark background
            val containerColor = if (isSelected) {
                 item.color ?: Color(0xFF2196F3) 
            } else {
                 Color(0xFF1E2836)
            }
            
            FilterChip(
                selected = isSelected,
                onClick = { onStatusSelected(item.status) },
                label = { Text(item.label) },
                leadingIcon = if (item.icon != null) {
                    { 
                        Icon(
                            imageVector = item.icon, 
                            contentDescription = null,
                            modifier = Modifier.size(sdp(18)),
                            tint = if (isSelected) Color.White else (item.color ?: Color.Gray)
                        ) 
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = containerColor,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1E2836),
                    labelColor = if (item.color != null && !isSelected) item.color else Color.Gray
                ),
                border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = false,
                    borderColor = if (item.color != null) item.color.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}

data class BookingFilterItem(
    val status: BookingStatus?,
    val label: String,
    val color: Color?,
    val icon: ImageVector?
)
