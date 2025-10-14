package com.group.mobileparkingchain.model

import com.group.mobileparkingchain.enumuration.ParkingStatus

data class ParkingSpot(
    val id: String,
    val type: String,
    val status: ParkingStatus
)