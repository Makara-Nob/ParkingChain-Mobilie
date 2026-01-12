package com.group.mobileparkingchain.features.home.presentation.components

import androidx.compose.runtime.Composable
import com.group.mobileparkingchain.features.payment.data.ParkingDetail
import com.group.mobileparkingchain.features.home.data.ParkingSpot
import com.group.mobileparkingchain.features.payment.presentation.view.ReservationDetailSheet

@Composable
fun ReservationSheet(spot: ParkingSpot, onReserve: () -> Unit, onDismiss: () -> Unit) {
    ReservationDetailSheet(
        parkingDetail = ParkingDetail(
            id = spot.id,
            location = null,
            type = "${spot.type} Parking",
            lastUpdated = spot.lastUpdated,
            pricePerHour = spot.pricePerHour,
            status = spot.status.name
        ),
        onDismiss = onDismiss,
        onReserve = onReserve
    )
}
