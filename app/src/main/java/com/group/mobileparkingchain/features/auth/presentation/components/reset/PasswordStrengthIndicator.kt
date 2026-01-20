package com.group.mobileparkingchain.features.auth.presentation.components.reset

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.PrimaryBlue
import androidx.compose.foundation.border
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@Composable
fun PasswordStrengthIndicator(password: String) {
    val hasMinLength = password.length >= 8
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

    val criteriaMetCount = listOf(hasMinLength, hasUpperCase, hasNumber, hasSpecialChar).count { it }
    val progress = criteriaMetCount / 4f
    
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Strength Progress")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Password Strength",
            color = Color.White,
            modifier = Modifier.padding(bottom = sdp(8))
        )

        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(sdp(8))
                .clip(RoundedCornerShape(sdp(4))),
            color = PrimaryBlue,
            trackColor = Color(0xFF334155),
            strokeCap = StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.height(sdp(16)))

        StrengthItem(label = "At least 8 characters", isMet = hasMinLength)
        StrengthItem(label = "Contains an uppercase letter", isMet = hasUpperCase)
        StrengthItem(label = "Contains a number", isMet = hasNumber)
        StrengthItem(label = "Contains a special character", isMet = hasSpecialChar)
    }
}

@Composable
private fun StrengthItem(label: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = sdp(4))
    ) {
        Box(
            modifier = Modifier
                .size(sdp(20))
                .clip(CircleShape)
                .background(if (isMet) PrimaryBlue else Color.Transparent)
                .then(
                    if (!isMet) Modifier.border(sdp(1), Color.Gray, CircleShape) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isMet) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(sdp(14))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(sdp(12)))
        
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = ssp(14)
        )
    }
}
