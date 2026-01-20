package com.group.mobileparkingchain.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

// Navigation item data class
data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun BottomNavigationBar(
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomNavItem(Icons.Default.Home, "Home", "home"),
        BottomNavItem(Icons.Default.History, "History", "booking_history"),
        BottomNavItem(Icons.Default.AccountCircle, "Account", "account")
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF1E2A3A),
        shadowElevation = sdp(8)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sdp(12)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                BottomNavItemView(
                    item = item,
                    isSelected = selectedIndex == index,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor = if (isSelected) Color(0xFF4A90E2) else Color(0xFF8A9BAE)
    val textColor = if (isSelected) Color(0xFF4A90E2) else Color(0xFF8A9BAE)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(sdp(80))
            .padding(horizontal = sdp(4))
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = Color(0xFF2C3E50),
                        shape = MaterialTheme.shapes.medium
                    )
                } else {
                    Modifier
                }
            )
            .padding(vertical = sdp(8))
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(sdp(32))
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(sdp(24))
            )
        }
        
        Spacer(modifier = Modifier.height(sdp(4)))
        
        Text(
            text = item.label,
            color = textColor,
            fontSize = ssp(12),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        
        // Underline indicator for selected item
        if (isSelected) {
            Spacer(modifier = Modifier.height(sdp(6)))
            Box(
                modifier = Modifier
                    .width(sdp(40))
                    .height(sdp(2))
                    .background(Color(0xFF4A90E2))
            )
        }
    }
}

// Preview usage example
@Composable
fun BottomNavigationBarPreview() {
    var selectedIndex by remember { mutableStateOf(0) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        BottomNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
