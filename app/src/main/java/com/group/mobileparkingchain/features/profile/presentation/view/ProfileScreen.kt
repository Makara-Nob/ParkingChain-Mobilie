package com.group.mobileparkingchain.features.profile.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.core.Resource
import com.group.mobileparkingchain.features.profile.data.UserProfile
import com.group.mobileparkingchain.features.profile.presentation.components.profileScreen.ProfileImage
import com.group.mobileparkingchain.features.profile.presentation.components.profileScreen.ProfileInfoCard
import com.group.mobileparkingchain.features.profile.presentation.components.profileScreen.ProfileOptionsCard
import com.group.mobileparkingchain.features.profile.presentation.viewmodel.ProfileViewModel
import com.group.mobileparkingchain.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditClick: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onBookingHistory: () -> Unit = {},
    onTransactionHistory: () -> Unit = {},
    onLogout: () -> Unit = {},
    showSavedToast: Boolean = false
) {
    val context = LocalContext.current
    var selectedNavIndex by remember { mutableStateOf(2) } // Account tab selected
    var showLogoutDialog by remember { mutableStateOf(false) }

    val profileState by viewModel.profileState.collectAsState()

    if (showSavedToast) {
        LaunchedEffect(showSavedToast) {
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Logout", color = Color.White) },
            text = { Text(text = "Are you sure you want to logout?", color = Color.Gray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text(text = "Logout", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = { index ->
                    selectedNavIndex = index
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToMap()
                        2 -> { /* Already on Profile */ }
                    }
                }
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        when (val state = profileState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4A90E2))
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Failed to load profile",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            is Resource.Success -> {
                ProfileContent(
                    userProfile = state.data,
                    onEditClick = onEditClick,
                    onChangePassword = onChangePassword,
                    onBookingHistory = onBookingHistory,
                    onTransactionHistory = onTransactionHistory,
                    onLogout = {
                        showLogoutDialog = true
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun ProfileContent(
    userProfile: UserProfile,
    onEditClick: () -> Unit,
    onChangePassword: () -> Unit,
    onBookingHistory: () -> Unit,
    onTransactionHistory: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Profile Image and Name with Edit Button
        ProfileImage(userProfile.profileImageUrl)
        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
        androidx.compose.material3.TextButton(
            onClick = onEditClick,
            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF4A90E2)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color(0xFF4A90E2),
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "Edit Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${userProfile.firstName} ${userProfile.lastName}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userProfile.email,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Personal Information Section
        Text(
            text = "Personal Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        listOf(
            "First Name" to userProfile.firstName,
            "Last Name" to userProfile.lastName,
            "Email" to userProfile.email,
            "Phone Number" to (userProfile.phoneNumber.takeIf { it.isNotBlank() } ?: "Not set")
        ).forEach {
            ProfileInfoCard(it.first, it.second)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account Management Section
        Text(
            text = "Account Management",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileOptionsCard(
            options = listOf(
                "Change Password" to onChangePassword,
                "Payment History" to onTransactionHistory,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Section
        ProfileOptionsCard(
            options = listOf(
                "Logout" to onLogout
            ),
            isDangerZone = true
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
