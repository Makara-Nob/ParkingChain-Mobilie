package com.group.mobileparkingchain.navigation

import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group.mobileparkingchain.ui.screens.booking.CompleteBookingScreen
import com.group.mobileparkingchain.ui.screens.home.HomeScreen
import com.group.mobileparkingchain.ui.screens.signin.SignInScreen
import com.group.mobileparkingchain.ui.screens.signup.SignUpScreen
import com.group.mobileparkingchain.ui.screens.welcome.WelcomeScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("signin")
    object Home : Screen("home")
    object SignUp : Screen("signup")
    object complete_booking : Screen("complete_booking")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

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

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
    }
}