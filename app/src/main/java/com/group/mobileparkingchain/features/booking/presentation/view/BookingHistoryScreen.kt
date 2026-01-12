package com.group.mobileparkingchain.features.booking.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.features.booking.presentation.components.BookingFilterChips
import com.group.mobileparkingchain.features.booking.presentation.viewmodel.BookingViewModel
import com.group.mobileparkingchain.features.home.presentation.components.HomeTopBar
import com.group.mobileparkingchain.features.home.presentation.components.SearchBar
import com.group.mobileparkingchain.features.parking.data.model.Booking
import com.group.mobileparkingchain.features.parking.data.model.BookingStatus
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.PaymentState
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.PaymentViewModel
import com.group.mobileparkingchain.ui.components.BottomNavigationBar

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    bookingViewModel: BookingViewModel,
    paymentViewModel: PaymentViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val bookings by bookingViewModel.bookings.collectAsState()
    val isLoading by bookingViewModel.isLoading.collectAsState()
    val error by bookingViewModel.error.collectAsState()
    val paymentState by paymentViewModel.paymentState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        bookingViewModel.fetchUserBookings()
    }

    // Handle Payment State
    LaunchedEffect(paymentState) {
        when (paymentState) {
            is PaymentState.Error -> {
                // Show error toast or snackbar
                android.widget.Toast.makeText(context, (paymentState as PaymentState.Error).message, android.widget.Toast.LENGTH_LONG).show()
            }
            is PaymentState.Success -> {
                // Handled in ViewModel (opening deep link) but we can show a success message
            }
            else -> {}
        }
    }

    var selectedNavIndex by remember { mutableStateOf(1) } // Booking History tab selected
    var selectedFilter by remember { mutableStateOf<BookingStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showDetailsSheet by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = { index ->
                    selectedNavIndex = index
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> { /* Already on Booking History */ }
                        2 -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Booking History",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = { bookingViewModel.refreshBookings() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SearchBar(searchQuery) { searchQuery = it }
                Spacer(modifier = Modifier.height(12.dp))
                BookingFilterChips(
                    selectedStatus = selectedFilter,
                    onStatusSelected = { status ->
                        selectedFilter = status
                        bookingViewModel.filterByStatus(status?.name)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Booking History List
            // Using bookings directly as filtering is now handled by server
            val filteredBookings = remember(searchQuery, bookings) {
                if (searchQuery.isBlank()) {
                    bookings
                } else {
                    bookings.filter { booking ->
                        val spotName = booking.spot?.spotName ?: ""
                        val spotId = booking.spotId ?: ""
                        spotName.contains(searchQuery, ignoreCase = true) ||
                            spotId.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            if (filteredBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No bookings yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reserve a spot to see it here.",
                            color = Color(0xFF8A9BAE),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingHistoryItem(
                            booking = booking,
                            onClick = {
                                selectedBooking = booking
                                showDetailsSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDetailsSheet && selectedBooking != null) {
        ModalBottomSheet(
            onDismissRequest = { showDetailsSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1B2430),
            contentColor = Color.White,
            tonalElevation = 0.dp
        ) {
            BookingDetailsSheet(
                booking = selectedBooking!!,
                onPayClick = { /* TODO: hook payment action if needed */ }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BookingHistoryItem(
    booking: Booking,
    onClick: () -> Unit
) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    val startDate = try { inputFormat.parse(booking.startTime) } catch (e: Exception) { Date() }
    val endDate = try { booking.endTime?.let { inputFormat.parse(it) } } catch (e: Exception) { null }
    val timeText = if (endDate != null) {
        "${startDate?.let { timeFormat.format(it) } ?: "-"} - ${timeFormat.format(endDate)}"
    } else {
        startDate?.let { timeFormat.format(it) } ?: "-"
    }

    val statusColor = when (booking.status) {
        BookingStatus.ACTIVE -> Color(0xFF4CAF50)
        BookingStatus.COMPLETED -> Color(0xFF2196F3)
        BookingStatus.CANCELLED -> Color(0xFFF44336)
        BookingStatus.RESERVED -> Color(0xFFFFC107)
    }

    val statusBgColor = when (booking.status) {
        BookingStatus.ACTIVE -> Color(0xFF1B5E20).copy(alpha = 0.2f)
        BookingStatus.COMPLETED -> Color(0xFF0D47A1).copy(alpha = 0.2f)
        BookingStatus.CANCELLED -> Color(0xFFB71C1C).copy(alpha = 0.2f)
        BookingStatus.RESERVED -> Color(0xFFFF6F00).copy(alpha = 0.2f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2430)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Spot Info and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.spot?.spotName ?: "Spot ${booking.spotId}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBgColor
                ) {
                    Text(
                        text = booking.status.name,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFF2C3E50), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Date",
                        color = Color(0xFF8A9BAE),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = startDate?.let { dateFormat.format(it) } ?: "-",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        color = Color(0xFF8A9BAE),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val currencySymbol = if (booking.currency == "KHR") "៛" else "$"
                    val formattedPrice = if (booking.currency == "KHR") {
                        "${String.format(Locale.getDefault(), "%,.0f", booking.totalPrice ?: 0.0)}"
                    } else {
                        String.format(Locale.getDefault(), "%.2f", booking.totalPrice ?: 0.0)
                    }

                    Text(
                        text = if (booking.currency == "KHR") "$formattedPrice $currencySymbol" else "$currencySymbol$formattedPrice",
                        color = Color(0xFF4A90E2),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingDetailsSheet(
    booking: Booking,
    onPayClick: () -> Unit
) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val startDate = try { inputFormat.parse(booking.startTime) } catch (e: Exception) { Date() }
    val endDate = try { booking.endTime?.let { inputFormat.parse(it) } } catch (e: Exception) { null }
    val timeText = if (endDate != null) {
        "${startDate?.let { timeFormat.format(it) } ?: "-"} - ${timeFormat.format(endDate)}"
    } else {
        startDate?.let { timeFormat.format(it) } ?: "-"
    }

    val statusColor = when (booking.status) {
        BookingStatus.ACTIVE -> Color(0xFF4CAF50)
        BookingStatus.COMPLETED -> Color(0xFF2196F3)
        BookingStatus.CANCELLED -> Color(0xFFF44336)
        BookingStatus.RESERVED -> Color(0xFFFFC107)
    }

    val statusBgColor = when (booking.status) {
        BookingStatus.ACTIVE -> Color(0xFF1B5E20).copy(alpha = 0.2f)
        BookingStatus.COMPLETED -> Color(0xFF0D47A1).copy(alpha = 0.2f)
        BookingStatus.CANCELLED -> Color(0xFFB71C1C).copy(alpha = 0.2f)
        BookingStatus.RESERVED -> Color(0xFFFF6F00).copy(alpha = 0.2f)
    }

    val currencySymbol = if (booking.currency == "KHR") "៛" else "$"
    val formattedPrice = if (booking.currency == "KHR") {
        "${String.format(Locale.getDefault(), "%,.0f", booking.totalPrice ?: 0.0)}"
    } else {
        String.format(Locale.getDefault(), "%.2f", booking.totalPrice ?: 0.0)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = booking.spot?.spotName ?: "Spot ${booking.spotId}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusBgColor
            ) {
                Text(
                    text = booking.status.name,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFF2C3E50), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Date", color = Color(0xFF8A9BAE), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = startDate?.let { dateFormat.format(it) } ?: "-",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Time", color = Color(0xFF8A9BAE), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Duration", color = Color(0xFF8A9BAE), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${booking.durationHours ?: 0} hrs",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total", color = Color(0xFF8A9BAE), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (booking.currency == "KHR") "$formattedPrice $currencySymbol" else "$currencySymbol$formattedPrice",
                    color = Color(0xFF4A90E2),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (booking.status == BookingStatus.RESERVED) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C84)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay Now", color = Color.White)
            }
        }
    }
}
