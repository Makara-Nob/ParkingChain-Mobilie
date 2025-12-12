package com.group.mobileparkingchain.features.booking.presentation.view

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.features.booking.presentation.components.BookingFilterChips
import com.group.mobileparkingchain.features.booking.presentation.viewmodel.BookingViewModel
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking History",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E2A3A)
                )
            )
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
        ) {
            // Filter Chips

            // Filter Chips
            BookingFilterChips(
                selectedStatus = selectedFilter,
                onStatusSelected = { status ->
                    selectedFilter = status
                    bookingViewModel.filterByStatus(status?.name) 
                }
            )

            // Booking History List
            // Using bookings directly as filtering is now handled by server
            val filteredBookings = bookings

            if (filteredBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bookings found",
                        color = Color(0xFF8A9BAE),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingHistoryItem(
                            booking = booking,
                            onPayClick = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingHistoryItem(
    booking: Booking,
    onPayClick: () -> Unit
) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    val startDate = try { inputFormat.parse(booking.startTime) } catch (e: Exception) { Date() }
    val endDate = try { booking.endTime?.let { inputFormat.parse(it) } } catch (e: Exception) { null }

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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2A3A)
        ),
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
                        text = "Spot ${booking.spot?.section ?: booking.spotId}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Level ${booking.spot?.level ?: "-"}",
                        color = Color(0xFF8A9BAE),
                        fontSize = 14.sp
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

            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date",
                        color = Color(0xFF8A9BAE),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = startDate?.let { dateFormat.format(it) } ?: "-",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Time",
                        color = Color(0xFF8A9BAE),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${startDate?.let { timeFormat.format(it) } ?: "-"} - ${endDate?.let { timeFormat.format(it) } ?: "-"}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Duration",
                        color = Color(0xFF8A9BAE),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${booking.durationHours ?: 0} hrs",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFF2C3E50), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Row: Price and Payment Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        color = Color(0xFF8A9BAE),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val currencySymbol = if (booking.currency == "KHR") "៛" else "$"
                    val formattedPrice = if (booking.currency == "KHR") {
                         "${String.format("%,.0f", booking.totalPrice ?: 0.0)}"
                    } else {
                         String.format("%.2f", booking.totalPrice ?: 0.0)
                    }
                    
                    Text(
                        text = if (booking.currency == "KHR") "$formattedPrice $currencySymbol" else "$currencySymbol$formattedPrice",
                        color = Color(0xFF4A90E2),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (booking.status == BookingStatus.RESERVED || booking.status == BookingStatus.ACTIVE) {
                     Button(
                        onClick = onPayClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C84)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pay with ABA", color = Color.White)
                    }
                }
            }
        }
    }
}
