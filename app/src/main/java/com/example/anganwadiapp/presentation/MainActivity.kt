package com.example.anganwadiapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.example.anganwadiapp.presentation.auth.staff.StaffAuthScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffLoginScreen
import com.example.anganwadiapp.presentation.auth.staff.StaffRegistrationScreen
import com.example.anganwadiapp.presentation.auth.parent.ParentLoginScreen
import com.example.anganwadiapp.presentation.auth.parent.ParentViewModel
import com.example.anganwadiapp.presentation.parent.ParentMainScreen
import com.example.anganwadiapp.presentation.parent.ParentAttendancePreviewScreen
import com.example.anganwadiapp.presentation.parent.ParentFoodMenuScreen
import com.example.anganwadiapp.presentation.parent.ParentActivityRatingScreen
import com.example.anganwadiapp.presentation.parent.ParentHealthObservationScreen
import com.example.anganwadiapp.presentation.parent.ParentWeeklyHealthSummaryScreen
import com.example.anganwadiapp.presentation.parent.ParentVaccinationUpdatesScreen
import com.example.anganwadiapp.presentation.parent.ParentActivitySummaryScreen
import com.example.anganwadiapp.presentation.parent.ParentWeeklyAttendanceSummaryScreen
import com.example.anganwadiapp.presentation.parent.ParentNutritionReportScreen
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
                val parentViewModel: ParentViewModel = hiltViewModel()
                val scope = rememberCoroutineScope()
                var isLoggedIn by remember { mutableStateOf(false) }
                var splashDone by remember { mutableStateOf(false) }
                var isParentLoggedIn by remember { mutableStateOf(false) }
                var parentChildId by remember { mutableStateOf("") }
                var parentCenterId by remember { mutableStateOf("") }
                var parentChildName by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    viewModel.preferencesManager.isLoggedIn.collect { loggedIn ->
                        isLoggedIn = loggedIn
                    }
                }

                LaunchedEffect(Unit) {
                    parentViewModel.preferencesManager.isParentLoggedIn.collect { loggedIn ->
                        isParentLoggedIn = loggedIn
                        if (loggedIn) {
                            val data = parentViewModel.preferencesManager.getParentData()
                            parentChildId = data.childId
                            parentCenterId = data.centerId
                            parentChildName = data.childName
                            authScreen = "parent_main_${data.childId}_${data.centerId}"
                        }
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
                                onLoginSuccess = { childId, centerId, childName ->
                                    authScreen = "parent_main_${childId}_$centerId"
                                    scope.launch {
                                        parentViewModel.preferencesManager.setParentLoggedIn(
                                            true, childId, centerId, childName
                                        )
                                    }
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
                        authScreen?.startsWith("parent_main") == true -> {
                            val parts = authScreen.removePrefix("parent_main_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            val childDetails by parentViewModel.childDetails.collectAsState()

                            LaunchedEffect(childId, centerId) {
                                if (childId.isNotEmpty() && centerId.isNotEmpty()) {
                                    parentViewModel.fetchChildDetails(centerId, childId)
                                }
                            }

                            val childName = childDetails?.get("name") as? String ?: parentChildName

                            ParentMainScreen(
                                childName = childName,
                                childId = childId,
                                onLogout = {
                                    scope.launch {
                                        parentViewModel.preferencesManager.setParentLoggedIn(false)
                                    }
                                    parentViewModel.resetState()
                                    authScreen = "role_selection"
                                },
                                onDailyUpdateClick = { feature ->
                                    val screen = when (feature) {
                                        "attendance" -> "parent_attendance"
                                        "food_menu" -> "parent_food_menu"
                                        "activity_rating" -> "parent_activity_rating"
                                        "health" -> "parent_health"
                                        else -> ""
                                    }
                                    if (screen.isNotEmpty()) authScreen = "${screen}_${childId}_$centerId"
                                },
                                onWeeklyUpdateClick = { feature ->
                                    val screen = when (feature) {
                                        "health_summary" -> "parent_weekly_health"
                                        "vaccination" -> "parent_vaccination"
                                        "activity_summary" -> "parent_activity_summary"
                                        "attendance_summary" -> "parent_weekly_attendance"
                                        "nutrition" -> "parent_nutrition"
                                        else -> ""
                                    }
                                    if (screen.isNotEmpty()) authScreen = "${screen}_${childId}_$centerId"
                                }
                            )
                        } // End of block
                        authScreen?.startsWith("parent_attendance") == true -> {
                            val parts = authScreen.removePrefix("parent_attendance_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentAttendancePreviewScreen(
                                childId = childId,
                                centerId = centerId,
                                onBack = { authScreen = "parent_main_${childId}_$centerId" }
                            )
                        }
                        authScreen?.startsWith("parent_food_menu") == true -> {
                            val parts = authScreen.removePrefix("parent_food_menu_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentFoodMenuScreen(
                                childId = childId,
                                centerId = centerId,
                                onBack = { authScreen = "parent_main_${childId}_$centerId" }
                            )
                        }
                        authScreen?.startsWith("parent_activity_rating") == true -> {
                            val parts = authScreen.removePrefix("parent_activity_rating_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentActivityRatingScreen(
                                onBack = { authScreen = "parent_main_${childId}_$centerId" },
                                parentViewModel = parentViewModel
                            )
                        }
                        authScreen?.startsWith("parent_health") == true -> {
                            val parts = authScreen.removePrefix("parent_health_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentHealthObservationScreen(
                                onBack = { authScreen = "parent_main_${childId}_$centerId" },
                                parentViewModel = parentViewModel
                            )
                        }
                        authScreen?.startsWith("parent_weekly_health") == true -> {
                            val parts = authScreen.removePrefix("parent_weekly_health_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentWeeklyHealthSummaryScreen(onBack = { authScreen = "parent_main_${childId}_$centerId" })
                        }
                        authScreen?.startsWith("parent_vaccination") == true -> {
                            val parts = authScreen.removePrefix("parent_vaccination_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentVaccinationUpdatesScreen(onBack = { authScreen = "parent_main_${childId}_$centerId" })
                        }
                        authScreen?.startsWith("parent_activity_summary") == true -> {
                            val parts = authScreen.removePrefix("parent_activity_summary_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentActivitySummaryScreen(onBack = { authScreen = "parent_main_${childId}_$centerId" })
                        }
                        authScreen?.startsWith("parent_weekly_attendance") == true -> {
                            val parts = authScreen.removePrefix("parent_weekly_attendance_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentWeeklyAttendanceSummaryScreen(onBack = { authScreen = "parent_main_${childId}_$centerId" })
                        }
                        authScreen?.startsWith("parent_nutrition") == true -> {
                            val parts = authScreen.removePrefix("parent_nutrition_").split("_")
                            val childId = parts.getOrNull(0) ?: ""
                            val centerId = parts.getOrNull(1) ?: ""
                            ParentNutritionReportScreen(onBack = { authScreen = "parent_main_${childId}_$centerId" })
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
