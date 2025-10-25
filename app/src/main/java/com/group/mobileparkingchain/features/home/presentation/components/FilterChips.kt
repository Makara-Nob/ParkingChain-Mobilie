package com.group.mobileparkingchain.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.enumuration.FilterType

@Composable
fun FilterChips(selectedFilter: FilterType, onFilterSelected: (FilterType) -> Unit) {
    val filters = listOf(
        FilterType.ALL to "All",
        FilterType.AVAILABLE to "Available",
        FilterType.CAR to "Car",
        FilterType.MOTORCYCLE to "Motorcycle"
    )

    Row (horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        filters.forEach { (type, label) ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterSelected(type) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2196F3),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1E2836),
                    labelColor = Color.Gray
                )
            )
        }
    }
}
