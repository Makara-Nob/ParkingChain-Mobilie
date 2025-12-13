package com.group.mobileparkingchain.navigations

import ProfileScreen
import Resource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.group.mobileparkingchain.enumuration.ParkingStatus
import com.group.mobileparkingchain.features.auth.presentation.view.OtpVerificationScreen
import com.group.mobileparkingchain.features.booking.presentation.view.BookingHistoryScreen
import com.group.mobileparkingchain.features.booking.presentation.viewmodel.BookingViewModel
import com.group.mobileparkingchain.features.booking.presentation.viewmodel.BookingViewModelFactory
import com.group.mobileparkingchain.features.home.presentation.view.HomeScreen
import com.group.mobileparkingchain.features.home.presentation.viewmodel.HomeViewModel
import com.group.mobileparkingchain.features.home.presentation.viewmodel.HomeViewModelFactory
import com.group.mobileparkingchain.features.chat.presentation.view.ChatScreen
import com.group.mobileparkingchain.features.chat.presentation.viewmodel.ChatViewModel
import com.group.mobileparkingchain.features.chat.presentation.viewmodel.ChatViewModelFactory
import com.group.mobileparkingchain.features.chat.data.repository.ChatRepository
import com.group.mobileparkingchain.features.parking.data.repository.ParkingRepository
import com.group.mobileparkingchain.features.payment.data.repository.PaymentRepository
import com.group.mobileparkingchain.features.payment.presentation.viewmodel.PaymentViewModel
import com.group.mobileparkingchain.features.profile.presentation.view.EditProfileScreen
import com.group.mobileparkingchain.features.profile.data.UserProfile
import com.group.mobileparkingchain.features.profile.presentation.viewmodel.ProfileViewModel
import com.group.mobileparkingchain.features.profile.presentation.viewmodel.ProfileViewModelFactory
import com.group.mobileparkingchain.network.RetrofitInstance
import com.group.mobileparkingchain.ui.screens.signin.SignInScreen
import com.group.mobileparkingchain.ui.screens.signup.SignUpScreen
import com.group.mobileparkingchain.ui.screens.welcome.WelcomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ----- Dependencies -----
    val parkingRepository = remember { 
        ParkingRepository(RetrofitInstance.parkingApi)
    }

    val paymentRepository = remember {
        PaymentRepository(RetrofitInstance.paymentApi)
    }

    val chatRepository = remember {
        ChatRepository(RetrofitInstance.chatApi)
    }
    
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(parkingRepository, paymentRepository)
    )
    
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(parkingRepository)
    )
    
    val paymentViewModel: PaymentViewModel = viewModel()

    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository)
    )

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context)
    )

    // ----- UI State from ViewModel -----
    val parkingSpots by homeViewModel.parkingSpots.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()
    
    // Function to update parking spot status (Optimistic update)
    val updateParkingSpotStatus: (String, ParkingStatus) -> Unit = { spotId, newStatus ->
        homeViewModel.updateSpotStatus(spotId, newStatus)
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
                    navController.navigate(Screen.PasswordReset.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUpSuccess = { email ->
                    navController.navigate(Screen.OtpVerification.createRoute(email)) {
                        popUpTo(Screen.SignUp.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerificationScreen(
                email = email,
                onVerificationSuccess = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            // Get user profile from ViewModel state
            val userProfileForHome = when (val state = profileState) {
                is Resource.Success -> state.data
                else -> UserProfile("", "", "", "", null)
            }
            
            HomeScreen(
                userProfile = userProfileForHome,
                parkingSpots = parkingSpots,
                homeViewModel = homeViewModel,
                onParkingSpotReserved = updateParkingSpotStatus,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.BookingHistory.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                }
            )
        }

        composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(
                bookingViewModel = bookingViewModel,
                paymentViewModel = paymentViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.BookingHistory.route) // Keep map nav to Booking History for now if intended or fix later
                },
                onChangePassword = {
                    navController.navigate(Screen.PasswordReset.route)
                },
                onBookingHistory = {
                    navController.navigate(Screen.BookingHistory.route)
                },
                onTransactionHistory = {
                    navController.navigate(Screen.TransactionHistory.route)
                },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Edit Profile Screen
        composable(Screen.EditProfile.route) {
            val currentProfile = when (val state = profileState) {
                is Resource.Success -> state.data
                else -> UserProfile("", "", "", "", null)
            }

            EditProfileScreen(
                userProfile = currentProfile,
                viewModel = profileViewModel,
                onBackClick = {
                    profileViewModel.loadUserProfile()
                    navController.popBackStack()
                }
            )
        }

        // Password Reset Screen
        composable(Screen.PasswordReset.route) {
            com.group.mobileparkingchain.features.auth.presentation.view.PasswordResetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = false }
                    }
                }
            )
        }

        // Transaction History Screen
        composable(Screen.TransactionHistory.route) {
            com.group.mobileparkingchain.features.payment.presentation.view.TransactionHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Chat Screen
        composable(Screen.Chat.route) {
            ChatScreen(
                viewModel = chatViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
