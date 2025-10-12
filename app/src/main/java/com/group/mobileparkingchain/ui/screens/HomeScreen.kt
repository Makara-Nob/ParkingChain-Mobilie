package com.group.mobileparkingchain.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ParkingSpot(
    val id: String,
    val type: String,
    val status: ParkingStatus
)

enum class ParkingStatus {
    AVAILABLE,
    OCCUPIED,
    RESERVED
}

enum class FilterType {
    ALL,
    AVAILABLE,
    CAR,
    MOTORCYCLE,
    LEV
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Sample parking data
    val parkingSpots = remember {
        listOf(
            ParkingSpot("A1", "Car", ParkingStatus.AVAILABLE),
            ParkingSpot("A2", "Car", ParkingStatus.OCCUPIED),
            ParkingSpot("A3", "Motocycle", ParkingStatus.AVAILABLE),
            ParkingSpot("A1", "Car", ParkingStatus.AVAILABLE),
            ParkingSpot("A2", "Car", ParkingStatus.OCCUPIED),
            ParkingSpot("A3", "Motocycle", ParkingStatus.AVAILABLE),
            ParkingSpot("A1", "Car", ParkingStatus.AVAILABLE),
            ParkingSpot("A2", "Car", ParkingStatus.OCCUPIED),
            ParkingSpot("A3", "Motocycle", ParkingStatus.AVAILABLE),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Smart Parking",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, "Search", tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFF1E2836),
                    focusedContainerColor = Color(0xFF1E2836),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF2196F3)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == FilterType.ALL,
                    onClick = { selectedFilter = FilterType.ALL },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E2836),
                        labelColor = Color.Gray
                    )
                )
                FilterChip(
                    selected = selectedFilter == FilterType.AVAILABLE,
                    onClick = { selectedFilter = FilterType.AVAILABLE },
                    label = { Text("Available") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E2836),
                        labelColor = Color.Gray
                    )
                )
                FilterChip(
                    selected = selectedFilter == FilterType.CAR,
                    onClick = { selectedFilter = FilterType.CAR },
                    label = { Text("Car") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E2836),
                        labelColor = Color.Gray
                    )
                )
                FilterChip(
                    selected = selectedFilter == FilterType.MOTORCYCLE,
                    onClick = { selectedFilter = FilterType.MOTORCYCLE },
                    label = { Text("Motocycle") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E2836),
                        labelColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E2836)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Legend",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LegendItem(Color(0xFF4CAF50), "Available")
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(Color(0xFFE53935), "Occupied")
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(Color(0xFF2196F3), "Reserved")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Parking Grid Title
            Text(
                "Parking Grid",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Parking Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(parkingSpots.size) { index ->
                    ParkingSpotCard(parkingSpots[index])
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun ParkingSpotCard(spot: ParkingSpot) {
    val backgroundColor = when (spot.status) {
        ParkingStatus.AVAILABLE -> Color(0xFF1B4D2C)
        ParkingStatus.OCCUPIED -> Color(0xFF4D1B1B)
        ParkingStatus.RESERVED -> Color(0xFF1B2C4D)
    }

    val textColor = when (spot.status) {
        ParkingStatus.AVAILABLE -> Color(0xFF4CAF50)
        ParkingStatus.OCCUPIED -> Color(0xFFE53935)
        ParkingStatus.RESERVED -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                spot.id,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                spot.type,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                "🚗",
                fontSize = 32.sp
            )
        }
    }
}