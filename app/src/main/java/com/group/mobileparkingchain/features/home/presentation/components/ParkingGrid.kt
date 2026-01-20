package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.features.home.data.ParkingSpot
import com.group.mobileparkingchain.ui.theme.sdp

@Composable
fun ParkingGrid(spots: List<ParkingSpot>, onSpotClick: (ParkingSpot) -> Unit) {
    val pageSize = 9
    var currentPage by remember { mutableStateOf(1) }
    val pagedSpots = spots.take(currentPage * pageSize)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = when {
        screenWidth < 360 -> 2
        screenWidth < 520 -> 3
        else -> 4
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(sdp(16)),
        verticalArrangement = Arrangement.spacedBy(sdp(16))
    ) {
        items(pagedSpots.size) { index ->
            ParkingSpotCard(pagedSpots[index]) { onSpotClick(pagedSpots[index]) }
            if (index == pagedSpots.lastIndex && pagedSpots.size < spots.size) {
                LaunchedEffect(Unit) { currentPage += 1 }
            }
        }
    }
}
