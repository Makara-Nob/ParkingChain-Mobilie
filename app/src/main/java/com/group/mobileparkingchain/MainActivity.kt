package com.group.mobileparkingchain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.group.mobileparkingchain.navigations.NavGraph
import com.group.mobileparkingchain.ui.theme.SmartParkingTheme
import android.graphics.Color as AndroidColor
import androidx.core.graphics.toColorInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Retrofit with context for AuthInterceptor
        com.group.mobileparkingchain.network.RetrofitInstance.initialize(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = "#1E2A3A".toColorInt()
        window.navigationBarColor = "#E0E0E0".toColorInt()
        
        setContent {
            SmartParkingTheme {
                NavGraph()
            }
        }
    }
}
