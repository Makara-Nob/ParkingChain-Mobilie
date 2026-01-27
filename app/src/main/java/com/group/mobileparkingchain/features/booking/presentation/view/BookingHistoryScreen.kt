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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import com.group.mobileparkingchain.features.booking.presentation.components.BookingFilterChips
import com.group.mobileparkingchain.features.booking.presentation.viewmodel.BookingViewModel
import com.group.mobileparkingchain.features.home.presentation.components.HomeTopBar
import com.group.mobileparkingchain.features.home.presentation.components.SearchBar
import com.group.mobileparkingchain.features.parking.data.model.Booking
import com.group.mobileparkingchain.features.parking.data.model.BookingStatus
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.presentation.view.PaymentQrScreen
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.PaymentState
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.PaymentViewModel
import com.group.mobileparkingchain.ui.components.BottomNavigationBar
import com.group.mobileparkingchain.utils.Formatters

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

    var selectedNavIndex by remember { mutableIntStateOf(1) } // Booking History tab selected
    var selectedFilter by remember { mutableStateOf<BookingStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showDetailsSheet by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var showQrScreen by remember { mutableStateOf(false) }
    var lastPayment by remember { mutableStateOf<Payment?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                lastPayment = (paymentState as PaymentState.Success).response
                showQrScreen = true
            }
            else -> {}
        }
    }

    if (showQrScreen && lastPayment != null) {
        val payment = lastPayment!!
        PaymentQrScreen(
            qrCodeBase64 = payment.qrImage,
            total = payment.amount,
            currency = payment.currency,
            deeplink = payment.deeplinkUrl,
            expiresAt = payment.expiresAt,
            status = payment.status,
            onCancel = {
                showQrScreen = false
                paymentViewModel.resetPaymentState()
                lastPayment = null
            },
            onBack = {
                showQrScreen = false
                paymentViewModel.resetPaymentState()
                lastPayment = null
            }
        )
        return
    }

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
                .padding(sdp(16))
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
                        fontSize = ssp(18)
                    )
                    IconButton(onClick = { bookingViewModel.refreshBookings() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(sdp(12)))
                SearchBar(searchQuery) { searchQuery = it }
                Spacer(modifier = Modifier.height(sdp(12)))
                BookingFilterChips(
                    selectedStatus = selectedFilter,
                    onStatusSelected = { status ->
                        selectedFilter = status
                        bookingViewModel.filterByStatus(status?.name)
                    }
                )
                Spacer(modifier = Modifier.height(sdp(12)))
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
                        .padding(sdp(32)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No bookings yet",
                            color = Color.White,
                            fontSize = ssp(18),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(sdp(8)))
                        Text(
                            text = "Reserve a spot to see it here.",
                            color = Color(0xFF8A9BAE),
                            fontSize = ssp(14)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = sdp(0), vertical = sdp(8)),
                    verticalArrangement = Arrangement.spacedBy(sdp(12))
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
            tonalElevation = sdp(0)
        ) {
            BookingDetailsSheet(
                booking = selectedBooking!!,
                onPayClick = {
                    val booking = selectedBooking
                    if (booking == null) {
                        android.widget.Toast.makeText(context, "Booking not found", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        val total = booking.totalPrice ?: 0.0
                        if (total <= 0.0) {
                            android.widget.Toast.makeText(context, "Invalid booking total", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            paymentViewModel.initiatePayment(
                                bookingId = booking.id,
                                amount = total,
                                currency = booking.currency ?: "USD"
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(sdp(24)))
        }
    }
}

@Composable
fun BookingHistoryItem(
    booking: Booking,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val startDate = parseUtcDate(booking.startTime)
    val endDate = parseUtcDate(booking.endTime)
        ?: calculateEndDateFromDuration(startDate, booking.durationHours)
    val timeText = if (endDate != null) {
        "${startDate?.let { timeFormat.format(it) } ?: "-"} - ${timeFormat.format(endDate)}"
    } else {
        startDate?.let { timeFormat.format(it) } ?: "-"
    }

    val autoCompleted = booking.status == BookingStatus.ACTIVE &&
        endDate != null &&
        endDate.before(Date())

    val effectiveStatus = if (autoCompleted) {
        BookingStatus.COMPLETED
    } else {
        booking.status
    }

    val statusColor = when (effectiveStatus) {
        BookingStatus.ACTIVE -> Color(0xFF4CAF50)
        BookingStatus.COMPLETED -> Color(0xFF2196F3)
        BookingStatus.CANCELLED -> Color(0xFFF44336)
        BookingStatus.RESERVED -> Color(0xFFFFC107)
    }

    val statusBgColor = when (effectiveStatus) {
        BookingStatus.ACTIVE -> Color(0xFF1B5E20).copy(alpha = 0.2f)
        BookingStatus.COMPLETED -> Color(0xFF0D47A1).copy(alpha = 0.2f)
        BookingStatus.CANCELLED -> Color(0xFFB71C1C).copy(alpha = 0.2f)
        BookingStatus.RESERVED -> Color(0xFFFF6F00).copy(alpha = 0.2f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(sdp(12)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2430)),
        elevation = CardDefaults.cardElevation(defaultElevation = sdp(4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(sdp(16))
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
                        fontSize = ssp(18),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(sdp(8)),
                        color = statusBgColor
                    ) {
                        Text(
                            text = effectiveStatus.name,
                            color = statusColor,
                            fontSize = ssp(12),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = sdp(12), vertical = sdp(6))
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(sdp(12)))

            Divider(color = Color(0xFF2C3E50), thickness = sdp(1))

            Spacer(modifier = Modifier.height(sdp(12)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Date",
                        color = Color(0xFF8A9BAE),
                        fontSize = ssp(12)
                    )
                    Spacer(modifier = Modifier.height(sdp(6)))
                    Text(
                        text = startDate?.let { dateFormat.format(it) } ?: "-",
                        color = Color.White,
                        fontSize = ssp(14),
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        color = Color(0xFF8A9BAE),
                        fontSize = ssp(12)
                    )
                    Spacer(modifier = Modifier.height(sdp(6)))
                    val currencySymbol = if (booking.currency == "KHR") "៛" else "$"
                    val formattedPrice = if (booking.currency == "KHR") {
                        Formatters.moneyWithSymbol(booking.totalPrice ?: 0.0, "", 0)
                    } else {
                        Formatters.moneyWithSymbol(booking.totalPrice ?: 0.0, "", 2)
                    }

                    Text(
                        text = if (booking.currency == "KHR") "$formattedPrice $currencySymbol" else "$currencySymbol$formattedPrice",
                        color = Color(0xFF4A90E2),
                        fontSize = ssp(18),
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
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val startDate = parseUtcDate(booking.startTime)
    val endDate = parseUtcDate(booking.endTime)
        ?: calculateEndDateFromDuration(startDate, booking.durationHours)
    val timeText = if (endDate != null) {
        "${startDate?.let { timeFormat.format(it) } ?: "-"} - ${timeFormat.format(endDate)}"
    } else {
        startDate?.let { timeFormat.format(it) } ?: "-"
    }

    val autoCompleted = booking.status == BookingStatus.ACTIVE &&
        endDate != null &&
        endDate.before(Date())

    val effectiveStatus = if (autoCompleted) {
        BookingStatus.COMPLETED
    } else {
        booking.status
    }

    val statusColor = when (effectiveStatus) {
        BookingStatus.ACTIVE -> Color(0xFF4CAF50)
        BookingStatus.COMPLETED -> Color(0xFF2196F3)
        BookingStatus.CANCELLED -> Color(0xFFF44336)
        BookingStatus.RESERVED -> Color(0xFFFFC107)
    }

    val statusBgColor = when (effectiveStatus) {
        BookingStatus.ACTIVE -> Color(0xFF1B5E20).copy(alpha = 0.2f)
        BookingStatus.COMPLETED -> Color(0xFF0D47A1).copy(alpha = 0.2f)
        BookingStatus.CANCELLED -> Color(0xFFB71C1C).copy(alpha = 0.2f)
        BookingStatus.RESERVED -> Color(0xFFFF6F00).copy(alpha = 0.2f)
    }

    val currencySymbol = if (booking.currency == "KHR") "៛" else "$"
    val formattedPrice = if (booking.currency == "KHR") {
        Formatters.moneyWithSymbol(booking.totalPrice ?: 0.0, "", 0)
    } else {
        Formatters.moneyWithSymbol(booking.totalPrice ?: 0.0, "", 2)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = sdp(20), vertical = sdp(12))
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
                    fontSize = ssp(18),
                    fontWeight = FontWeight.SemiBold
                )
            }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(sdp(8)),
                        color = statusBgColor
                    ) {
                        Text(
                            text = effectiveStatus.name,
                            color = statusColor,
                            fontSize = ssp(12),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = sdp(12), vertical = sdp(6))
                        )
                    }

                }
        }

        Spacer(modifier = Modifier.height(sdp(16)))
        Divider(color = Color(0xFF2C3E50), thickness = sdp(1))
        Spacer(modifier = Modifier.height(sdp(12)))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Date", color = Color(0xFF8A9BAE), fontSize = ssp(12))
                Spacer(modifier = Modifier.height(sdp(6)))
                Text(
                    text = startDate?.let { dateFormat.format(it) } ?: "-",
                    color = Color.White,
                    fontSize = ssp(14),
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Time", color = Color(0xFF8A9BAE), fontSize = ssp(12))
                Spacer(modifier = Modifier.height(sdp(6)))
                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = ssp(14),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(sdp(16)))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Duration", color = Color(0xFF8A9BAE), fontSize = ssp(12))
                Spacer(modifier = Modifier.height(sdp(6)))
                Text(
                    text = "${booking.durationHours ?: 0} hrs",
                    color = Color.White,
                    fontSize = ssp(14),
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total", color = Color(0xFF8A9BAE), fontSize = ssp(12))
                Spacer(modifier = Modifier.height(sdp(6)))
                Text(
                    text = if (booking.currency == "KHR") "$formattedPrice $currencySymbol" else "$currencySymbol$formattedPrice",
                    color = Color(0xFF4A90E2),
                    fontSize = ssp(18),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (booking.status == BookingStatus.RESERVED) {
            Spacer(modifier = Modifier.height(sdp(16)))
            Button(
                onClick = onPayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C84)),
                shape = RoundedCornerShape(sdp(10)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay Now", color = Color.White)
            }
        }
    }
}

private fun parseUtcDate(dateString: String?): Date? {
    if (dateString.isNullOrBlank()) return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    for (pattern in patterns) {
        val format = SimpleDateFormat(pattern, Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            return format.parse(dateString)
        } catch (e: Exception) {
            // Try next format
        }
    }
    return null
}

private fun calculateEndDateFromDuration(startDate: Date?, durationHours: Double?): Date? {
    if (startDate == null || durationHours == null) return null
    val durationMillis = (durationHours * 60 * 60 * 1000).toLong()
    return Date(startDate.time + durationMillis)
}
