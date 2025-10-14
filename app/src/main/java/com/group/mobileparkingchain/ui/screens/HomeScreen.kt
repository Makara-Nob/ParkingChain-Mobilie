package com.group.mobileparkingchain.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group.mobileparkingchain.enumuration.FilterType
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.model.ParkingDetail
import com.group.mobileparkingchain.model.ParkingSpot
import com.group.mobileparkingchain.model.PaymentInfo
import com.group.mobileparkingchain.ui.screens.booking.BookingInfo
import com.group.mobileparkingchain.ui.screens.booking.CompleteBookingScreen
import com.group.mobileparkingchain.ui.screens.payment.PaymentScreen
import com.group.mobileparkingchain.ui.screens.profile.UserProfile
import com.group.mobileparkingchain.ui.screens.reservation.ReservationDetailSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    userProfile: UserProfile,
) {
    var selectedFilter by remember {
        mutableStateOf(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showReservationSheet by remember { mutableStateOf(false) }
    var selectedSpot by remember { mutableStateOf<ParkingSpot?>(null) }
    var showCompleteBooking by remember { mutableStateOf(false) }
    var showPaymentScreen by remember { mutableStateOf(false) }

    // Booking details state
    var bookingDuration by remember { mutableStateOf(0) }
    var bookingStartTime by remember { mutableStateOf(0L) }
    var bookingTotal by remember { mutableStateOf(0.0) }
    val context = LocalContext.current

    // Sample parking data
    val parkingSpots = remember {
        listOf(
            ParkingSpot("P-123", "Car", ParkingStatus.AVAILABLE),
            ParkingSpot("P-124", "Car", ParkingStatus.OCCUPIED),
            ParkingSpot("P-125", "Motorcycle", ParkingStatus.AVAILABLE),
            ParkingSpot("P-126", "Car", ParkingStatus.AVAILABLE),
            ParkingSpot("P-127", "Car", ParkingStatus.RESERVED),
            ParkingSpot("P-128", "Motorcycle", ParkingStatus.AVAILABLE),
            ParkingSpot("P-129", "Car", ParkingStatus.AVAILABLE),
            ParkingSpot("P-130", "Car", ParkingStatus.OCCUPIED),
            ParkingSpot("P-131", "Motorcycle", ParkingStatus.AVAILABLE),
            ParkingSpot("P-132", "Motorcycle", ParkingStatus.AVAILABLE),
            ParkingSpot("P-133", "Motorcycle", ParkingStatus.AVAILABLE),
            ParkingSpot("P-134", "Motorcycle", ParkingStatus.AVAILABLE),
        )
    }

    // Filter parking spots based on selected filter and search query
    val filteredSpots = remember(selectedFilter, searchQuery, parkingSpots) {
        parkingSpots.filter { spot ->
            val matchesFilter = when (selectedFilter) {
                FilterType.ALL -> true
                FilterType.AVAILABLE -> spot.status == ParkingStatus.AVAILABLE
                FilterType.CAR -> spot.type.equals("Car", ignoreCase = true)
                FilterType.MOTORCYCLE -> spot.type.equals("Motorcycle", ignoreCase = true)
                FilterType.LEV -> false
            }
            val matchesSearch = searchQuery.isEmpty() ||
                    spot.id.contains(searchQuery, ignoreCase = true) ||
                    spot.type.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    // Show Payment Screen
    if (showPaymentScreen && selectedSpot != null) {
        PaymentScreen(
            paymentInfo = PaymentInfo(
                spotId = selectedSpot!!.id.removePrefix("P-"),
                duration = bookingDuration,
                startTime = bookingStartTime,
                total = bookingTotal
            ),
            onBackClick = {
                showPaymentScreen = false
                showCompleteBooking = true
            },
            onPaymentSuccess = {
                // Payment successful - reset everything
                showPaymentScreen = false
                showCompleteBooking = false
                selectedSpot = null

                // ✅ Show success toast
                Toast.makeText(context, "Payment successful! Booking confirmed 🎉", Toast.LENGTH_LONG).show()

                // Optional: Print for debug
                println("=== PAYMENT SUCCESSFUL ===")
                println("Booking confirmed!")
            }

        )
    }
    // Show Complete Booking Screen
    else if (showCompleteBooking && selectedSpot != null) {
        CompleteBookingScreen(
            bookingInfo = BookingInfo(
                spotId = selectedSpot!!.id.removePrefix("P-"),
                spotLocation = "Mair Street Parking Lot",
                spotType = selectedSpot!!.type,
                ratePerHour = 5.0
            ),
            onBackClick = {
                showCompleteBooking = false
                selectedSpot = null
            },
            onContinueToPayment = { duration, startTime, total ->
                // Store the booking details
                bookingDuration = duration
                bookingStartTime = startTime
                bookingTotal = total

                // Close complete booking and show payment screen
                showCompleteBooking = false
                showPaymentScreen = true
            }
        )
    }
    // Show Home Screen
    else {
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
                        IconButton(onClick = { /* TODO: Open drawer */ }) {
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Open notifications */ }) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = Color.White)
                        }

                        // Profile Image
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C2C2C))
                                .border(2.dp, Color(0xFF2196F3), CircleShape)
                                .clickable(onClick = onNavigateToProfile),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (userProfile.profileImageUrl != null) {
                                AsyncImage(
                                    model = userProfile.profileImageUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A1A),
                        titleContentColor = Color.White
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
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
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
                        label = { Text("Motorcycle") },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Parking Grid",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        "${filteredSpots.size} spots",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val pageSize = 9
                var currentPage by remember { mutableStateOf(1) }

                val pagedSpots = remember(filteredSpots, currentPage) {
                    filteredSpots.take(currentPage * pageSize)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pagedSpots.size) { index ->
                        ParkingSpotCard(
                            spot = pagedSpots[index],
                            onClick = {
                                if (pagedSpots[index].status == ParkingStatus.AVAILABLE) {
                                    selectedSpot = pagedSpots[index]
                                    showReservationSheet = true
                                }
                            }
                        )

                        // Detect when scrolled to bottom
                        if (index == pagedSpots.lastIndex && pagedSpots.size < filteredSpots.size) {
                            LaunchedEffect(Unit) {
                                currentPage += 1
                            }
                        }
                    }

                    // Optional loading footer
                    if (pagedSpots.size < filteredSpots.size) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF2196F3))
                            }
                        }
                    }
                }

            }
        }

        // Reservation Bottom Sheet
        if (showReservationSheet && selectedSpot != null) {
            ReservationDetailSheet(
                parkingDetail = ParkingDetail(
                    id = selectedSpot!!.id,
                    location = "Level 1, Section A",
                    type = selectedSpot!!.type + " Parking",
                    lastUpdated = "2 minutes ago",
                    pricePerHour = 2.00,
                    status = "Available"
                ),
                onDismiss = {
                    showReservationSheet = false
                    selectedSpot = null
                },
                onReserve = {
                    showReservationSheet = false
                    showCompleteBooking = true
                }
            )
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
fun ParkingSpotCard(
    spot: ParkingSpot,
    onClick: () -> Unit
) {
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
            .aspectRatio(1f)
            .clickable(
                enabled = spot.status == ParkingStatus.AVAILABLE,
                onClick = onClick
            ),
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                spot.type,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                if (spot.type.equals("Motorcycle", ignoreCase = true)) "🏍️" else "🚗",
                fontSize = 28.sp
            )
        }
    }
}