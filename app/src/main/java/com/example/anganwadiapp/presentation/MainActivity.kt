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
import com.example.anganwadiapp.presentation.auth.parent.ParentLoginScreen
import com.example.anganwadiapp.presentation.auth.parent.ParentViewModel
import com.example.anganwadiapp.presentation.auth.parent.ParentWelcomeScreen
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
                                    authScreen = "parent_login"
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
                        authScreen == "parent_login" -> {
                            ParentLoginScreen(
                                onLoginSuccess = { childId, centerId ->
                                    authScreen = "parent_welcome_${childId}_$centerId"
                                },
                                onBack = { authScreen = "role_selection" }
                            )
                        }
                        authScreen == "main_guest" -> {
                            MainScreen(onLogout = {
                                isLoggedIn = false
                                authScreen = "role_selection"
                            })
                        }
                        authScreen?.startsWith("parent_welcome") == true -> {
                            val parts = authScreen!!.removePrefix("parent_welcome_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            val parentViewModel: ParentViewModel = hiltViewModel()
                            val childDetails by parentViewModel.childDetails.collectAsState()

                            LaunchedEffect(childId, centerId) {
                                if (childId.isNotEmpty() && centerId.isNotEmpty()) {
                                    parentViewModel.fetchChildDetails(centerId, childId)
                                }
                            }

                            val childName = childDetails?.get("name") as? String ?: ""
                            val fatherName = childDetails?.get("fatherName") as? String ?: ""
                            val motherName = childDetails?.get("motherName") as? String ?: ""

                            ParentWelcomeScreen(
                                childName = childName,
                                fatherName = fatherName,
                                motherName = motherName,
                                childId = childId,
                                onLogout = {
                                    parentViewModel.resetState()
                                    authScreen = "role_selection"
                                }
                            )
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
