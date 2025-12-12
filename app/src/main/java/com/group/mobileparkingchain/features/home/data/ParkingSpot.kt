package com.group.mobileparkingchain.features.home.data

import com.group.mobileparkingchain.enumuration.ParkingStatus

data class ParkingSpot(
    val id: String,
    val dbId: String,
    val type: String,
    val status: ParkingStatus
)