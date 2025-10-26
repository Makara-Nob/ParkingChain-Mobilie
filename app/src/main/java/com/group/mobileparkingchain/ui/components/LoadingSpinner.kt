package com.group.mobileparkingchain.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(40.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2 - 4.dp.toPx()
        val dashCount = 8
        val dashLength = 8.dp.toPx()
        val strokeWidth = 3.dp.toPx()

        for (i in 0 until dashCount) {
            val angle = (i * 360f / dashCount + rotation) * PI / 180f

            // Calculate outer point (end of dash)
            val outerX = centerX + radius * cos(angle).toFloat()
            val outerY = centerY + radius * sin(angle).toFloat()

            // Calculate inner point (start of dash)
            val innerRadius = radius - dashLength
            val innerX = centerX + innerRadius * cos(angle).toFloat()
            val innerY = centerY + innerRadius * sin(angle).toFloat()

            drawLine(
                color = color,
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}