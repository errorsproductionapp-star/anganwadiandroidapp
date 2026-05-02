package com.example.anganwadiapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.anganwadiapp.presentation.main.MainScreen
import com.example.anganwadiapp.presentation.role_selection.RoleSelectionScreen
import com.example.anganwadiapp.presentation.splash.SplashScreen
import com.example.anganwadiapp.presentation.theme.AnganwadiAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnganwadiAppTheme {
                var currentScreen by remember { mutableStateOf("splash") }

                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(onTimeout = {
                            currentScreen = "role_selection"
                        })
                    }
                    "role_selection" -> {
                        RoleSelectionScreen(onContinue = { role ->
                            currentScreen = "main"
                        })
                    }
                    "main" -> {
                        MainScreen()
                    }
                }
            }
        }
    }
}