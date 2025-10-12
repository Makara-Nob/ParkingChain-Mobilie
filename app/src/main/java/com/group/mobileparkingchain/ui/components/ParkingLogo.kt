package com.group.mobileparkingchain.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.R

@Composable
fun ParkingLogo(
    modifier: Modifier = Modifier,
    size: Int = 300
) {
    Image(
        painter = painterResource(id = R.drawable.parking_logo), // your image file
        contentDescription = "Parking Logo",
        modifier = modifier.size(size.dp)
    )
}
