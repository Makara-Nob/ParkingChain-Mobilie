package com.group.mobileparkingchain.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group.mobileparkingchain.ui.navigations.Screen
import com.group.mobileparkingchain.ui.screens.home.HomeScreen
import com.group.mobileparkingchain.ui.screens.profile.EditProfileScreen
import com.group.mobileparkingchain.ui.screens.profile.ProfileScreen
import com.group.mobileparkingchain.ui.screens.profile.UserProfile
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
