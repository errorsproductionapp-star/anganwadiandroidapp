package com.example.anganwadiapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.anganwadiapp.presentation.auth.staff.StaffAuthScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffLoginScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffRegistrationScreen
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
                            if (role == "Staff") {
                                currentScreen = "staff_auth"
                            } else {
                                currentScreen = "main"
                            }
                        })
                    }
                    "staff_auth" -> {
                        StaffAuthScreen(
                            onLoginClick = { currentScreen = "staff_login" },
                            onRegisterClick = { currentScreen = "staff_registration" },
                            onBack = { currentScreen = "role_selection" }
                        )
                    }
                    "staff_login" -> {
                        StaffLoginScreen(
                            onLoginSuccess = { currentScreen = "main" },
                            onBack = { currentScreen = "staff_auth" }
                        )
                    }
                    "staff_registration" -> {
                        StaffRegistrationScreen(
                            onRegisterSuccess = { currentScreen = "staff_login" },
                            onBack = { currentScreen = "staff_auth" }
                        )
                    }
                    "main" -> {
                        MainScreen()
                    }
                }
            }
        }
    }
}
