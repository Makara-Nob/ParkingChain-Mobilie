package com.group.mobileparkingchain.features.auth.presentation.components.signin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.group.mobileparkingchain.ui.screens.signin.SignInScreen
import com.group.mobileparkingchain.ui.theme.SmartParkingTheme

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SmartParkingTheme {
        SignInScreen()
    }
}