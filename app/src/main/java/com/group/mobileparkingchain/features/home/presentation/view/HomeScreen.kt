package com.group.mobileparkingchain.features.home.presentation.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.group.mobileparkingchain.features.payment.data.PaymentInfo
import com.group.mobileparkingchain.features.payment.presentation.view.BookingReceiptScreen
import com.group.mobileparkingchain.features.payment.presentation.view.PaymentQrScreen
import com.group.mobileparkingchain.features.payment.presentation.view.PaymentScreen
import com.group.mobileparkingchain.features.profile.data.UserProfile
import com.group.mobileparkingchain.ui.components.BottomNavigationBar
import com.group.mobileparkingchain.ui.screens.booking.BookingInfo
import com.group.mobileparkingchain.ui.screens.booking.BookingPaymentScreen
import com.group.mobileparkingchain.ui.screens.booking.CompleteBookingScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    parkingSpots: List<ParkingSpot>,
    homeViewModel: HomeViewModel,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onParkingSpotReserved: (String, ParkingStatus) -> Unit
) {
    // ----- UI State -----
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpot by remember { mutableStateOf<ParkingSpot?>(null) }
    var showReservationSheet by remember { mutableStateOf(false) }
    var showCompleteBooking by remember { mutableStateOf(false) }
    var showBookingPayment by remember { mutableStateOf(false) }
    var showPaymentScreen by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var selectedNavIndex by remember { mutableStateOf(0) }

    var bookingDuration by remember { mutableStateOf(0) }
    var bookingStartTime by remember { mutableStateOf(0L) }
    var bookingTotal by remember { mutableStateOf(0.0) }
    var bookingCurrency by remember { mutableStateOf("USD") }
    var bookingPaymentMethod by remember { mutableStateOf("khqr") }
    var showQrScreen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val bookingResult by homeViewModel.bookingResult.collectAsState()
    val paymentState by homeViewModel.paymentState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // ----- Payment Flow Logic -----
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // If we have a payment created (deeplink opened), confirm it on resume
                if (paymentState is HomeViewModel.PaymentState.PaymentCreated) {
                    // Only auto-confirm if method is ABA (since it requires app switch)
                    // For KHQR, user stays in app or scans elsewhere, so no auto-confirm on resume
                    if (bookingPaymentMethod == "aba") {
                        val paymentId = (paymentState as HomeViewModel.PaymentState.PaymentCreated).paymentId
                        homeViewModel.confirmPayment(paymentId)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(paymentState) {
        when (paymentState) {
            is HomeViewModel.PaymentState.PaymentCreated -> {
                val state = paymentState as HomeViewModel.PaymentState.PaymentCreated
                val deeplink = state.deeplink
                
                // Prioritize based on selected method
                if (bookingPaymentMethod == "aba" && !deeplink.isNullOrEmpty()) {
                     try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open payment app", Toast.LENGTH_SHORT).show()
                    }
                } else if (bookingPaymentMethod == "khqr") {
                    // For Bakong/KHQR prefer showing QR code (even if deeplink exists). Ensure QR is displayed.
                    showCompleteBooking = false
                    showBookingPayment = false
                    showQrScreen = true
                }
            }
            is HomeViewModel.PaymentState.PaymentConfirmed -> {
                // Payment successful - close QR and show receipt with animation
                selectedSpot?.let { spot ->
                    onParkingSpotReserved(spot.id, ParkingStatus.OCCUPIED)
                }
                showQrScreen = false
                showPaymentScreen = false
                showReceipt = true
                homeViewModel.resetPaymentState()
            }
            is HomeViewModel.PaymentState.Error -> {
                Toast.makeText(context, (paymentState as HomeViewModel.PaymentState.Error).message, Toast.LENGTH_LONG).show()
                homeViewModel.resetPaymentState()
            }
            else -> {}
        }
    }
    
    // ----- Fetch data when filter changes -----
    LaunchedEffect(selectedFilter) {
        homeViewModel.fetchParkingSpots(selectedFilter)
    }
    
    // ----- Handle booking creation result -----
    LaunchedEffect(bookingResult) {
        bookingResult?.onSuccess { bookingResponse ->
            // Booking created successfully, payment state will handle navigation
            showCompleteBooking = false
            showBookingPayment = false
            // Wait for PaymentState to trigger nav
        }?.onFailure { exception ->
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
        showReceipt && bookingResult?.isSuccess == true -> {
            bookingResult?.getOrNull()?.let { bookingResponse ->
                AnimatedVisibility(
                    visible = showReceipt,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(500)
                    ),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    BookingReceiptScreen(
                        booking = bookingResponse.booking,
                        onDoneClick = {
                            homeViewModel.clearBookingResult()
                            showReceipt = false
                            showQrScreen = false
                            selectedSpot = null
                            bookingDuration = 0
                            bookingStartTime = 0L
                            bookingTotal = 0.0
                            bookingCurrency = "USD"
                            bookingPaymentMethod = "khqr"
                        }
                    )
                }
            }
        }
        
        showQrScreen -> {
            val state = paymentState as? HomeViewModel.PaymentState.PaymentCreated
            if (state != null) {
                PaymentQrScreen(
                    qrCodeBase64 = state.qrImage,
                    total = state.amount,
                    currency = state.currency,
                    onDone = {
                        homeViewModel.confirmPayment(state.paymentId)
                        // showQrScreen = false // Keep showing until success or manual close? 
                        // Usually wait for conf. We can close and show loading/receipt.
                        // For now let's assume it confirms.
                    },
                    onBack = {
                        showQrScreen = false
                        homeViewModel.resetPaymentState() // Reset state when manually backing out
                        showBookingPayment = true 
                    }
                )
            }
        }
        
        showPaymentScreen && selectedSpot != null -> {
            val bookingResponse = bookingResult?.getOrNull()
            if (bookingResponse != null) {
                val dbSpotId = selectedSpot!!.dbId
                PaymentScreen(
                    paymentInfo = PaymentInfo(
                        bookingId = bookingResponse.booking.id,
                        spotId = dbSpotId,
                        duration = bookingDuration,
                        startTime = bookingStartTime,
                        total = bookingTotal
                    ),
                    onBackClick = { showPaymentScreen = false; showCompleteBooking = true },
                    onPaymentSuccess = {
                        // Handled by state observation
                    },
                    onInitiatePayment = { bookingId, amount, _ ->
                        homeViewModel.createPayment(bookingId, amount)
                    },
                    isProcessing = paymentState is HomeViewModel.PaymentState.Loading
                )
            }
        }

        showBookingPayment && selectedSpot != null -> {
             BookingPaymentScreen(
                 bookingInfo = BookingInfo(
                    spotId = selectedSpot!!.id.removePrefix("P-"),
                    spotLocation = "Mair Street Parking Lot",
                    spotType = selectedSpot!!.type,
                    ratePerHour = selectedSpot!!.pricePerHour ?: 5.0
                ),
                duration = bookingDuration,
                startTime = bookingStartTime,
                total = bookingTotal,
                onBackClick = { showBookingPayment = false; showCompleteBooking = true },
                onConfirm = { method, currency ->
                    bookingCurrency = currency
                    bookingPaymentMethod = method
                    
                    // Create booking first
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val startTimeIso = isoFormat.format(Date(bookingStartTime))
                    
                    homeViewModel.createBooking(
                        spotId = selectedSpot!!.dbId,
                        startTime = startTimeIso,
                        durationHours = bookingDuration.toDouble(),
                        paymentMethod = method,
                        currency = currency
                    )
                }
             )
        }

        showCompleteBooking && selectedSpot != null -> {
            CompleteBookingScreen(
                bookingInfo = BookingInfo(
                    spotId = selectedSpot!!.id.removePrefix("P-"),
                    spotLocation = "Mair Street Parking Lot",
                    spotType = selectedSpot!!.type,
                    ratePerHour = selectedSpot!!.pricePerHour ?: 5.0
                ),
                onBackClick = { showCompleteBooking = false; selectedSpot = null },
                onContinueToPayment = { duration, startTime, total ->
                    bookingDuration = duration
                    bookingStartTime = startTime
                    bookingTotal = total
                    
                    showCompleteBooking = false
                    showBookingPayment = true
                }
            )
        }

        else -> {
            // ----- Home Content -----
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
                Column(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    SearchBar(searchQuery) { searchQuery = it }
                    Spacer(Modifier.height(16.dp))
                    FilterChips(selectedFilter) { selectedFilter = it }
                    Spacer(Modifier.height(16.dp))

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