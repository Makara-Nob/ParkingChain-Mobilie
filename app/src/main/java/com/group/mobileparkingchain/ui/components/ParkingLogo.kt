package com.group.mobileparkingchain.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue
import com.group.mobileparkingchain.ui.theme.White

@Composable
fun ParkingLogo(
    modifier: Modifier = Modifier,
    size: Int = 160
) {
    Canvas(
        modifier = modifier.size(size.dp)
    ) {
        val width = this.size.width
        val height = this.size.height

        // Location pin path
        val pinPath = Path().apply {
            moveTo(width * 0.5f, 0f)
            cubicTo(
                width * 0.224f, 0f,
                0f, height * 0.224f,
                0f, height * 0.5f
            )
            cubicTo(
                0f, height * 0.95f,
                width * 0.5f, height,
                width * 0.5f, height
            )
            cubicTo(
                width * 0.5f, height,
                width, height * 0.95f,
                width, height * 0.5f
            )
            cubicTo(
                width, height * 0.224f,
                width * 0.776f, 0f,
                width * 0.5f, 0f
            )
            close()
        }

        // Draw pin
        drawPath(
            path = pinPath,
            color = PrimaryBlue
        )

        val centerX = width * 0.5f
        val centerY = height * 0.35f

        // Draw WiFi signals
        drawArc(
            color = White,
            startAngle = 200f,
            sweepAngle = 40f,
            useCenter = false,
            topLeft = Offset(centerX - 40.dp.toPx(), centerY - 40.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(80.dp.toPx(), 80.dp.toPx()),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        drawArc(
            color = White,
            startAngle = -60f,
            sweepAngle = 40f,
            useCenter = false,
            topLeft = Offset(centerX - 40.dp.toPx(), centerY - 40.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(80.dp.toPx(), 80.dp.toPx()),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        // Draw motorcycle wheels
        drawCircle(
            color = White,
            radius = 16.dp.toPx(),
            center = Offset(centerX - 30.dp.toPx(), centerY + 20.dp.toPx()),
            style = Stroke(width = 5f)
        )

        drawCircle(
            color = White,
            radius = 16.dp.toPx(),
            center = Offset(centerX + 30.dp.toPx(), centerY + 20.dp.toPx()),
            style = Stroke(width = 5f)
        )

        // Draw motorcycle body
        drawPath(
            path = Path().apply {
                moveTo(centerX - 14.dp.toPx(), centerY + 20.dp.toPx())
                lineTo(centerX, centerY - 5.dp.toPx())
                lineTo(centerX + 10.dp.toPx(), centerY - 5.dp.toPx())
                lineTo(centerX + 14.dp.toPx(), centerY + 20.dp.toPx())
            },
            color = White,
            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Handle bar
        drawLine(
            color = White,
            start = Offset(centerX, centerY - 5.dp.toPx()),
            end = Offset(centerX, centerY - 15.dp.toPx()),
            strokeWidth = 4f
        )

        drawCircle(
            color = White,
            radius = 4.dp.toPx(),
            center = Offset(centerX, centerY - 17.dp.toPx())
        )
    }
}