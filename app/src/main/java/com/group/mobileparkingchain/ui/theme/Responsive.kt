package com.group.mobileparkingchain.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val BASE_WIDTH_DP = 360f

@Composable
fun sdp(value: Int): Dp {
    val width = LocalConfiguration.current.screenWidthDp.toFloat()
    val scale = (width / BASE_WIDTH_DP).coerceIn(0.85f, 1.2f)
    return (value * scale).dp
}

@Composable
fun ssp(value: Int): TextUnit {
    val width = LocalConfiguration.current.screenWidthDp.toFloat()
    val scale = (width / BASE_WIDTH_DP).coerceIn(0.85f, 1.2f)
    return (value * scale).sp
}
