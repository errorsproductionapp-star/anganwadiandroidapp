package com.example.anganwadiapp.presentation.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anganwadiapp.presentation.attendance.AttendanceMarkingScreen
import com.example.anganwadiapp.presentation.diet.DietManagementScreen
import com.example.anganwadiapp.presentation.enrollment.StudentEnrollmentScreen
import com.example.anganwadiapp.presentation.health.HealthRecordScreen
import com.example.anganwadiapp.presentation.main.dashboard.DashboardScreen
import com.example.anganwadiapp.presentation.progress.ProgressRatingScreen
import com.example.anganwadiapp.presentation.reports.ReportsScreen
import com.example.anganwadiapp.presentation.settings.SettingsScreen
import com.example.anganwadiapp.presentation.stock.StockManagementScreen
import com.example.anganwadiapp.presentation.students.StudentsScreen
import com.example.anganwadiapp.presentation.theme.SkyBlue40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var navigationStack by remember { mutableStateOf(listOf<String>()) }
    var previousScreen by remember { mutableStateOf<String?>(null) }

    val currentScreen = navigationStack.lastOrNull()
    val screenTitle = when (currentScreen) {
        "student_enrollment" -> "Student Enrollment"
        "attendance_marking" -> "Attendance Marking"
        "diet_management" -> "Diet Management"
        "stock_management" -> "Stock Management"
        "health_record" -> "Health Record"
        "progress_rating" -> "Progress Rating"
        "students" -> "Students"
        "reports" -> "Reports"
        "settings" -> "Settings"
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (screenTitle != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = screenTitle,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            previousScreen = currentScreen
                            navigationStack = navigationStack.dropLast(1)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = SkyBlue40,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen == null) {
                NavigationBar {
                    val items = listOf(
                        NavigationItem("Dashboard", Icons.Default.Home),
                        NavigationItem("Students", Icons.Default.People),
                        NavigationItem("Reports", Icons.Default.Assessment),
                        NavigationItem("Settings", Icons.Default.Settings)
                    )
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                navigationStack = listOf()
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen ?: "dashboard_${selectedTab}",
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                label = "screen_transition"
            ) { targetScreen ->
                when {
                    targetScreen == "dashboard_0" -> DashboardScreen(
                        onNavigateToStudentEnrollment = {
                            previousScreen = "dashboard_0"
                            navigationStack = navigationStack + "student_enrollment"
                        },
                        onNavigateToAttendanceMarking = {
                            previousScreen = "dashboard_0"
                            navigationStack = navigationStack + "attendance_marking"
                        },
                        onNavigateToDietManagement = {
                            previousScreen = "dashboard_0"
                            navigationStack = navigationStack + "diet_management"
                        },
                        onNavigateToStockManagement = {
                            previousScreen = "dashboard_0"
                            navigationStack = navigationStack + "stock_management"
                        },
                        onNavigateToHealthRecord = {
                            previousScreen = "dashboard_0"
                            navigationStack = navigationStack + "health_record"
                        },
                        onNavigateToProgressRating = {
                            previousScreen = "dashboard_0"
                            navigationStack = navigationStack + "progress_rating"
                        }
                    )
                    targetScreen == "dashboard_1" -> StudentsScreen()
                    targetScreen == "dashboard_2" -> ReportsScreen()
                    targetScreen == "dashboard_3" -> SettingsScreen(onLogout = onLogout)
                    else -> {
                        when (targetScreen) {
                            "student_enrollment" -> StudentEnrollmentScreen(
                                onSuccess = {
                                    previousScreen = "dashboard_0"
                                    navigationStack = navigationStack.dropLast(1)
                                }
                            )
                            "attendance_marking" -> AttendanceMarkingScreen()
                            "diet_management" -> DietManagementScreen()
                            "stock_management" -> StockManagementScreen()
                            "health_record" -> HealthRecordScreen()
                            "progress_rating" -> ProgressRatingScreen()
                            "students" -> StudentsScreen()
                            "reports" -> ReportsScreen()
                            "settings" -> SettingsScreen(onLogout = onLogout)
                        }
                    }
                }
            }
        }
    }
}

private data class NavigationItem(
    val label: String,
    val icon: ImageVector
)
