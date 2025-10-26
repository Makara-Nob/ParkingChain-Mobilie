package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.runtime.Composable
import com.group.mobileparkingchain.features.payment.data.ParkingDetail
import com.group.mobileparkingchain.features.home.data.ParkingSpot
import com.group.mobileparkingchain.ui.screens.reservation.ReservationDetailSheet

@Composable
fun ReservationSheet(spot: ParkingSpot, onReserve: () -> Unit, onDismiss: () -> Unit) {
    ReservationDetailSheet(
        parkingDetail = ParkingDetail(
            id = spot.id,
            location = "Level 1, Section A",
            type = spot.type + " Parking",
            lastUpdated = "2 minutes ago",
            pricePerHour = 2.0,
            status = "Available"
        ),
        onDismiss = onDismiss,
        onReserve = onReserve
    )
}
