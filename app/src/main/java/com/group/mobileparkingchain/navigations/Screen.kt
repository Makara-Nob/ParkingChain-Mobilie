package com.group.mobileparkingchain.navigations

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object notification : Screen("notification")
}