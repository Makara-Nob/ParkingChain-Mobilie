package com.group.mobileparkingchain.navigations


sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object OtpVerification : Screen("otp_verification/{email}") {
        fun createRoute(email: String) = "otp_verification/$email"
    }
    object Home : Screen("home")
    object BookingHistory : Screen("booking_history")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object PasswordReset : Screen("password_reset")
    object TransactionHistory : Screen("transaction_history")
}