package com.group.mobileparkingchain.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.model.ParkingSpot

@Composable
fun ParkingGrid(spots: List<ParkingSpot>, onSpotClick: (ParkingSpot) -> Unit) {
    val pageSize = 9
    var currentPage by remember { mutableStateOf(1) }
    val pagedSpots = spots.take(currentPage * pageSize)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pagedSpots.size) { index ->
            ParkingSpotCard(pagedSpots[index]) { onSpotClick(pagedSpots[index]) }
            if (index == pagedSpots.lastIndex && pagedSpots.size < spots.size) {
                LaunchedEffect(Unit) { currentPage += 1 }
            }
        }
    }
}
