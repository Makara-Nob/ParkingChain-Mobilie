
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.enumuration.FilterType
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.features.home.data.ParkingSpot
import com.group.mobileparkingchain.features.home.presentation.components.FilterChips
import com.group.mobileparkingchain.features.home.presentation.components.HomeTopBar
import com.group.mobileparkingchain.features.home.presentation.components.Legend
import com.group.mobileparkingchain.features.home.presentation.components.ParkingGrid
import com.group.mobileparkingchain.features.home.presentation.components.ReservationSheet
import com.group.mobileparkingchain.features.home.presentation.components.SearchBar
import com.group.mobileparkingchain.features.payment.data.PaymentInfo
import com.group.mobileparkingchain.features.payment.presentation.view.PaymentScreen
import com.group.mobileparkingchain.features.profile.data.UserProfile
import com.group.mobileparkingchain.ui.components.BottomNavigationBar
import com.group.mobileparkingchain.ui.screens.booking.BookingInfo
import com.group.mobileparkingchain.ui.screens.booking.CompleteBookingScreen

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    parkingSpots: List<ParkingSpot>,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onParkingSpotReserved: (String, ParkingStatus) -> Unit
) {
    // ----- UI State -----
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpot by remember { mutableStateOf<ParkingSpot?>(null) }
    var showReservationSheet by remember { mutableStateOf(false) }
    var showCompleteBooking by remember { mutableStateOf(false) }
    var showPaymentScreen by remember { mutableStateOf(false) }
    var selectedNavIndex by remember { mutableStateOf(0) }

    var bookingDuration by remember { mutableStateOf(0) }
    var bookingStartTime by remember { mutableStateOf(0L) }
    var bookingTotal by remember { mutableStateOf(0.0) }

    val context = LocalContext.current

    // ----- Filtered Spots -----
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
                    spot.id.contains(searchQuery, true) ||
                    spot.type.contains(searchQuery, true)

            matchesFilter && matchesSearch
        }
    }

    // ----- Screen Navigation -----
    when {
        showPaymentScreen && selectedSpot != null -> {
            val currentSpotId = selectedSpot!!.id
            PaymentScreen(
                paymentInfo = PaymentInfo(
                    spotId = currentSpotId.removePrefix("P-"),
                    duration = bookingDuration,
                    startTime = bookingStartTime,
                    total = bookingTotal
                ),
                onBackClick = { showPaymentScreen = false; showCompleteBooking = true },
                onPaymentSuccess = {
                    onParkingSpotReserved(currentSpotId, ParkingStatus.OCCUPIED)

                    Toast.makeText(
                        context,
                        "Payment successful! Spot $currentSpotId is now reserved 🎉",
                        Toast.LENGTH_LONG
                    ).show()

                    showPaymentScreen = false
                    showCompleteBooking = false
                    selectedSpot = null
                    bookingDuration = 0
                    bookingStartTime = 0L
                    bookingTotal = 0.0
                }
            )
        }

        showCompleteBooking && selectedSpot != null -> {
            CompleteBookingScreen(
                bookingInfo = BookingInfo(
                    spotId = selectedSpot!!.id.removePrefix("P-"),
                    spotLocation = "Mair Street Parking Lot",
                    spotType = selectedSpot!!.type,
                    ratePerHour = 5.0
                ),
                onBackClick = { showCompleteBooking = false; selectedSpot = null },
                onContinueToPayment = { duration, startTime, total ->
                    bookingDuration = duration
                    bookingStartTime = startTime
                    bookingTotal = total
                    showCompleteBooking = false
                    showPaymentScreen = true
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
                                2 -> onNavigateToNotification()
                                3 -> onNavigateToProfile()
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
                    Spacer(Modifier.height(16.dp))
                    SearchBar(searchQuery) { searchQuery = it }
                    Spacer(Modifier.height(16.dp))
                    FilterChips(selectedFilter) { selectedFilter = it }
                    Spacer(Modifier.height(16.dp))
                    Legend()
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