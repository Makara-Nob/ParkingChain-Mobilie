package com.group.mobileparkingchain.features.home.presentation.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.enumuration.FilterType
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.features.home.data.ParkingSpot
import com.group.mobileparkingchain.features.home.presentation.components.AnimatedChatButton
import com.group.mobileparkingchain.features.home.presentation.components.FilterChips
import com.group.mobileparkingchain.features.home.presentation.components.HomeTopBar
import com.group.mobileparkingchain.features.home.presentation.components.ParkingGrid
import com.group.mobileparkingchain.features.home.presentation.components.ReservationSheet
import com.group.mobileparkingchain.features.home.presentation.components.SearchBar
import com.group.mobileparkingchain.features.home.presentation.viewmodel.HomeViewModel
import com.group.mobileparkingchain.features.payment.presentation.view.BookingReceiptScreen
import com.group.mobileparkingchain.features.payment.presentation.view.PaymentQrScreen
import com.group.mobileparkingchain.ui.components.BottomNavigationBar
import com.group.mobileparkingchain.ui.components.LoadingSpinner
import com.group.mobileparkingchain.features.payment.presentation.view.BookingInfo
import com.group.mobileparkingchain.ui.screens.booking.BookingPaymentScreen
import com.group.mobileparkingchain.features.payment.presentation.view.CompleteBookingScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    parkingSpots: List<ParkingSpot>,
    homeViewModel: HomeViewModel,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToBookingHistory: () -> Unit = {},
    onSessionExpired: () -> Unit = {},
    onParkingSpotReserved: (String, ParkingStatus) -> Unit
) {
    // ----- UI State -----
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpot by remember { mutableStateOf<ParkingSpot?>(null) }
    var showReservationSheet by remember { mutableStateOf(false) }
    var showCompleteBooking by remember { mutableStateOf(false) }
    var showBookingPayment by remember { mutableStateOf(false) }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    var bookingDuration by remember { mutableIntStateOf(0) }
    var bookingStartTime by remember { mutableLongStateOf(0L) }
    var bookingTotal by remember { mutableDoubleStateOf(0.0) }
    var showQrScreen by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var lastPaymentCreated by remember { mutableStateOf<HomeViewModel.PaymentState.PaymentCreated?>(null) }
    var pollingPaymentId by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val bookingResult by homeViewModel.bookingResult.collectAsState()
    val paymentState by homeViewModel.paymentState.collectAsState()
    val paymentStatus by homeViewModel.paymentStatus.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val receiptBooking by homeViewModel.receiptBooking.collectAsState()
    // ----- Payment Flow Logic -----
    
    LaunchedEffect(paymentState) {
        when (paymentState) {
            is HomeViewModel.PaymentState.PaymentCreated -> {
                lastPaymentCreated = paymentState as HomeViewModel.PaymentState.PaymentCreated
                // For KHQR, always show QR screen (no deeplink auto-open)
                showCompleteBooking = false
                showBookingPayment = false
                showQrScreen = true
                pollingPaymentId = lastPaymentCreated?.paymentId
            }
            is HomeViewModel.PaymentState.PaymentConfirmed -> {
                // Payment successful - close QR and refresh state
                selectedSpot?.let { spot ->
                    onParkingSpotReserved(spot.id, ParkingStatus.OCCUPIED)
                }
                Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show()
                showQrScreen = false
                pollingPaymentId = null
                showReceipt = true
                homeViewModel.resetPaymentState()
            }
            is HomeViewModel.PaymentState.Error -> {
                Toast.makeText(
                    context,
                    (paymentState as HomeViewModel.PaymentState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                pollingPaymentId = null
                homeViewModel.resetPaymentState()
                showQrScreen = false
                showCompleteBooking = false
                showBookingPayment = false
                showReceipt = false
                homeViewModel.clearReceiptBooking()
                selectedSpot = null
            }
            else -> {}
        }
    }

    LaunchedEffect(showQrScreen, paymentState, lastPaymentCreated) {
        val id = (paymentState as? HomeViewModel.PaymentState.PaymentCreated)?.paymentId
            ?: lastPaymentCreated?.paymentId
        if (showQrScreen && !id.isNullOrBlank() && pollingPaymentId != id) {
            pollingPaymentId = id
        }
    }

    LaunchedEffect(pollingPaymentId) {
        pollingPaymentId?.let { id ->
            homeViewModel.confirmPayment(id)
        }
    }
    
    // ----- Fetch data when filter changes -----
    LaunchedEffect(selectedFilter) {
        homeViewModel.fetchParkingSpots(selectedFilter)
    }
    
    // ----- Handle booking creation result -----
    LaunchedEffect(bookingResult) {
        bookingResult?.onFailure { exception ->
            val message = exception.message.orEmpty()
            if (message.contains("token", ignoreCase = true) ||
                message.contains("unauthorized", ignoreCase = true)
            ) {
                Toast.makeText(
                    context,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
                ).show()
                onSessionExpired()
                return@onFailure
            }
            Toast.makeText(
                context,
                "Booking failed: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ----- Filtered Spots (client-side filtering for search only) -----
    val filteredSpots = remember(searchQuery, parkingSpots) {
        parkingSpots.filter { spot ->
            val matchesSearch = searchQuery.isEmpty() ||
                    spot.id.contains(searchQuery, true) ||
                    spot.type.contains(searchQuery, true)

            matchesSearch
        }
    }

    // ----- Screen Navigation -----
    when {
        showReceipt -> {
            val booking = receiptBooking
            if (booking != null) {
                BookingReceiptScreen(
                    booking = booking,
                    onDoneClick = {
                        showReceipt = false
                        homeViewModel.clearReceiptBooking()
                        onNavigateToBookingHistory()
                    }
                )
            } else {
                LoadingSpinner()
            }
        }

        showQrScreen -> {
            val state = (paymentState as? HomeViewModel.PaymentState.PaymentCreated) ?: lastPaymentCreated
            if (state != null) {
                PaymentQrScreen(
                    qrCodeBase64 = state.qrImage,
                    total = state.amount,
                    currency = state.currency,
                    deeplink = state.deeplink,
                    expiresAt = state.expiresAt,
                    status = paymentStatus,
                    onCancel = {
                        val paymentId = state.paymentId
                        homeViewModel.cancelPayment(paymentId)
                    },
                    onBack = {
                        showQrScreen = false
                        pollingPaymentId = null
                        homeViewModel.resetPaymentState()
                        showBookingPayment = true
                    }
                )
            }
        }
        
        showCompleteBooking && selectedSpot != null -> {
            CompleteBookingScreen(
                bookingInfo = BookingInfo(
                    spotId = selectedSpot!!.id.removePrefix("P-"),
                    spotType = selectedSpot!!.type,
                    ratePerHour = selectedSpot!!.pricePerHour ?: 5.0
                ),
                onBackClick = { showCompleteBooking = false; selectedSpot = null },
                onContinueToPayment = { duration, startTime, total ->
                    bookingDuration = duration
                    bookingStartTime = startTime
                    bookingTotal = total // Keep for now (will be overridden by backend)
                    
                    showCompleteBooking = false
                    showBookingPayment = true
                }
            )
        }

        showBookingPayment && selectedSpot != null -> {
            BookingPaymentScreen(
                bookingInfo = BookingInfo(
                    spotId = selectedSpot!!.id.removePrefix("P-"),
                    spotType = selectedSpot!!.type,
                    ratePerHour = selectedSpot!!.pricePerHour ?: 5.0
                ),
                duration = bookingDuration,
                startTime = bookingStartTime,
                total = bookingTotal, // Placeholder; server will calculate
                isProcessing = isLoading,
                onBackClick = { showBookingPayment = false; showCompleteBooking = true },
                onConfirm = { method, currency ->
                    // Create booking first (server will compute totalPrice based on currency)
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val startTimeIso = isoFormat.format(Date(bookingStartTime))
                    
                    homeViewModel.createBooking(
                        spotId = selectedSpot!!.dbId,
                        startTime = startTimeIso,
                        durationHours = bookingDuration.toDouble(),
                        paymentMethod = method,
                        currency = currency // Server will convert and return correct totalPrice
                    )
                }
             )
        }

        else -> {
            // ----- Home Content (Default State) -----
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
                                0 -> { /* Already on Home */ }
                                1 -> onNavigateToMap()
                                2 -> onNavigateToProfile()
                            }
                        }
                    )
                },
                containerColor = Color(0xFF121212),
                floatingActionButton = {
                    AnimatedChatButton(
                        onClick = onNavigateToChat
                    )
                }
            ) { padding ->
                val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
                
                Column(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(sdp(16))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                ) {
                    Spacer(Modifier.height(sdp(16)))
                    SearchBar(searchQuery) { searchQuery = it }
                    Spacer(Modifier.height(sdp(16)))
                    FilterChips(selectedFilter) { selectedFilter = it }
                    Spacer(Modifier.height(sdp(16)))

                    ParkingGrid(filteredSpots) { spot ->
                        if (spot.status == ParkingStatus.AVAILABLE) {
                            selectedSpot = spot
                            showReservationSheet = true
                        }
                    }
                }
            }

            // ----- Reservation Sheet -----
            if (showReservationSheet && selectedSpot != null) {
                ReservationSheet(
                    spot = selectedSpot!! as ParkingSpot,
                    onReserve = {
                        showReservationSheet = false
                        showCompleteBooking = true
                    },
                    onDismiss = {
                        showReservationSheet = false
                        selectedSpot = null
                    }
                )
            }
        }
    }
}
