package com.example.anganwadiapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.presentation.auth.staff.StaffAuthScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffLoginScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffRegistrationScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffViewModel
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
                var authScreen by remember { mutableStateOf("role_selection") }
                val viewModel: StaffViewModel = hiltViewModel()
                var isLoggedIn by remember { mutableStateOf(false) }
                var splashDone by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    viewModel.preferencesManager.isLoggedIn.collect { loggedIn ->
                        isLoggedIn = loggedIn
                    }
                }

                if (!splashDone) {
                    SplashScreen(onTimeout = {
                        splashDone = true
                    })
                } else {
                    when {
                        isLoggedIn -> {
                            MainScreen(onLogout = {
                                isLoggedIn = false
                                viewModel.logout()
                            })
                        }
                        authScreen == "role_selection" -> {
                            RoleSelectionScreen(onContinue = { role ->
                                if (role == "Staff") {
                                    authScreen = "staff_auth"
                                } else {
                                    authScreen = "main_guest"
                                }
                            })
                        }
                        authScreen == "staff_auth" -> {
                            StaffAuthScreen(
                                onLoginClick = { authScreen = "staff_login" },
                                onRegisterClick = { authScreen = "staff_registration" },
                                onBack = { authScreen = "role_selection" }
                            )
                        }
                        authScreen == "staff_login" -> {
                            StaffLoginScreen(
                                onLoginSuccess = { isLoggedIn = true },
                                onBack = { authScreen = "staff_auth" }
                            )
                        }
                        authScreen == "staff_registration" -> {
                            StaffRegistrationScreen(
                                onRegisterSuccess = { authScreen = "staff_login" },
                                onNavigateToLogin = { authScreen = "staff_login" },
                                onBack = { authScreen = "staff_auth" }
                            )
                        }
                        authScreen == "main_guest" -> {
                            MainScreen(onLogout = {
                                isLoggedIn = false
                                authScreen = "role_selection"
                            })
                        }
                        else -> {
                            RoleSelectionScreen(onContinue = { role ->
                                if (role == "Staff") {
                                    authScreen = "staff_auth"
                                } else {
                                    authScreen = "main_guest"
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}
