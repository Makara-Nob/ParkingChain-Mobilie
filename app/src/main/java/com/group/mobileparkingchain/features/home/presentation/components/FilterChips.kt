package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.enumuration.FilterType

@Composable
fun FilterChips(selectedFilter: FilterType, onFilterSelected: (FilterType) -> Unit) {
    val filters = listOf(
        FilterItem(FilterType.ALL, "All", null, null),
        FilterItem(FilterType.AVAILABLE, "Available", Color(0xFF4ADE80), Icons.Filled.CheckCircle),
        FilterItem(FilterType.OCCUPIED, "Occupied", Color(0xFFEF4444), Icons.Filled.Lock),
        FilterItem(FilterType.RESERVED, "Reserved", Color(0xFF3B82F6), Icons.Filled.Schedule),
        FilterItem(FilterType.CAR, "Car", null, Icons.Filled.DirectionsCar),
        FilterItem(FilterType.MOTORCYCLE, "Motorcycle", null, Icons.Filled.TwoWheeler)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { item ->
            val isSelected = selectedFilter == item.type
            val chipColor = if (isSelected) {
                 item.color ?: Color(0xFF2196F3) 
            } else {
                 Color(0xFF1E2836)
            }
            
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(item.type) },
                label = { Text(item.label) },
                leadingIcon = if (item.icon != null) {
                    { 
                        Icon(
                            imageVector = item.icon, 
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (isSelected) Color.White else (item.color ?: Color.Gray)
                        ) 
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = chipColor,
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

data class FilterItem(
    val type: FilterType,
    val label: String,
    val color: Color?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector?
)
