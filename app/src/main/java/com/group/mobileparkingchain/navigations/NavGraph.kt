package com.group.mobileparkingchain.navigation

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.model.ParkingSpot
import com.group.mobileparkingchain.ui.navigations.Screen
import com.group.mobileparkingchain.features.profile.presentation.view.EditProfileScreen
import com.group.mobileparkingchain.features.profile.presentation.view.ProfileScreen
import com.group.mobileparkingchain.ui.screens.view.UserProfile
import com.group.mobileparkingchain.ui.screens.signin.SignInScreen
import com.group.mobileparkingchain.ui.screens.signup.SignUpScreen
import com.group.mobileparkingchain.ui.screens.welcome.WelcomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    var showSavedToast by remember { mutableStateOf(false) }

    // Store user profile in a state that persists across navigation
    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                firstName = "Sophia",
                lastName = "Carter",
                email = "Sophia.carter@gmail.com",
                phoneNumber = "+1 123 334 4434",
                profileImageUrl = null
            )
        )
    }

    // 🔥 LIFT PARKING SPOTS STATE HERE
    var parkingSpots by remember {
        mutableStateOf(
            listOf(
                ParkingSpot("P-123", "Car", ParkingStatus.AVAILABLE),
                ParkingSpot("P-124", "Car", ParkingStatus.OCCUPIED),
                ParkingSpot("P-125", "Motorcycle", ParkingStatus.AVAILABLE),
                ParkingSpot("P-126", "Car", ParkingStatus.AVAILABLE),
                ParkingSpot("P-127", "Car", ParkingStatus.RESERVED),
                ParkingSpot("P-128", "Motorcycle", ParkingStatus.AVAILABLE),
                ParkingSpot("P-129", "Car", ParkingStatus.AVAILABLE),
                ParkingSpot("P-130", "Car", ParkingStatus.OCCUPIED),
                ParkingSpot("P-131", "Motorcycle", ParkingStatus.AVAILABLE),
                ParkingSpot("P-132", "Motorcycle", ParkingStatus.AVAILABLE),
                ParkingSpot("P-133", "Motorcycle", ParkingStatus.AVAILABLE),
                ParkingSpot("P-134", "Motorcycle", ParkingStatus.AVAILABLE),
            )
        )
    }

    // Function to update parking spot status
    val updateParkingSpotStatus: (String, ParkingStatus) -> Unit = { spotId, newStatus ->
        println("🔄 Updating spot $spotId to status: $newStatus")

        parkingSpots = parkingSpots.map { spot ->
            if (spot.id == spotId) {
                println("✅ Found spot $spotId, changing status from ${spot.status} to $newStatus")
                spot.copy(status = newStatus)
            } else {
                spot
            }
        }

        println("📊 Updated parking spots list:")
        parkingSpots.forEach { spot ->
            println("   ${spot.id}: ${spot.status}")
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onForgotPassword = {
                    // TODO: Navigate to forgot password screen
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                userProfile = userProfile,
                parkingSpots = parkingSpots,
                onParkingSpotReserved = updateParkingSpotStatus,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Profile Screen (View Only)
        composable(Screen.Profile.route) {
            ProfileScreen(
                userProfile = userProfile,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

        // Edit Profile Screen
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                userProfile = userProfile,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveChanges = { updatedProfile ->
                    // Update the user profile
                    userProfile = updatedProfile

                    // TODO: Save to backend/database
                    println("Profile updated: $updatedProfile")

                    // Navigate back to profile screen
                    navController.popBackStack()
                }
            )
        }
    }
}