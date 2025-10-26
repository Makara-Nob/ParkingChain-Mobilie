package com.group.mobileparkingchain.ui.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.features.payment.presentation.component.reservation.ActionButton
import com.group.mobileparkingchain.features.payment.presentation.component.reservation.InfoRow
import com.group.mobileparkingchain.features.payment.presentation.component.reservation.SheetHeader
import com.group.mobileparkingchain.features.payment.data.ParkingDetail


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailSheet(
    parkingDetail: ParkingDetail,
    onDismiss: () -> Unit,
    onReserve: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {

            SheetHeader(id = parkingDetail.id, status = parkingDetail.status)
            Spacer(modifier = Modifier.height(24.dp))

            // Info Rows
            InfoRow(label = "Location", value = parkingDetail.location)
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(label = "Type", value = parkingDetail.type)
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(label = "Last Updated", value = parkingDetail.lastUpdated)
            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(24.dp))

            InfoRow(label = "Pricing", value = "$${String.format("%.2f", parkingDetail.pricePerHour)}/hr")
            Spacer(modifier = Modifier.height(24.dp))

            ActionButton("Reserve Now", onClick = onReserve)
            Spacer(modifier = Modifier.height(12.dp))
            ActionButton("Cancel", onClick = onDismiss, filled = false)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



