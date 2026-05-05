package com.example.anganwadiapp.presentation.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.FunctionKeyMeta
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.presentation.activity.WeeklyActivityPlanScreen
import com.example.anganwadiapp.presentation.activity.WeeklyActivityViewModel
import com.example.anganwadiapp.presentation.attendance.AttendanceMarkingScreen
import com.example.anganwadiapp.presentation.diet.DietManagementScreen
import com.example.anganwadiapp.presentation.enrollment.StudentEnrollmentScreen
import com.example.anganwadiapp.presentation.health.HealthMeasurementFormScreen
import com.example.anganwadiapp.presentation.health.HealthRecordScreen
import com.example.anganwadiapp.presentation.health.HealthViewModel
import com.example.anganwadiapp.presentation.main.dashboard.DashboardScreen
import com.example.anganwadiapp.presentation.progress.ProgressRatingScreen
import com.example.anganwadiapp.presentation.progress.ProgressRatingViewModel
import com.example.anganwadiapp.presentation.reports.ReportsScreen
import com.example.anganwadiapp.presentation.settings.SettingsScreen
import com.example.anganwadiapp.presentation.stock.StockManagementScreen
import com.example.anganwadiapp.presentation.stock.StockUpdatesScreen
import com.example.anganwadiapp.presentation.students.StudentsScreen
import com.example.anganwadiapp.presentation.theme.SkyBlue40

// ─── Design tokens ────────────────────────────────────────────────────────────

private val NavBg            = Color(0xFFFFFFFF)  // pure white
private val NavBgSurface     = Color(0xFFF4F6FA)  // off-white surface
private val NavActive        = Color(0xFF185FA5)  // deep blue
private val NavActiveGlow    = Color(0xFF185FA5).copy(alpha = 0.12f)  // soft blue pill
private val NavInactive      = Color(0xFFCBD5E1)  // slate-300
private val NavLabelActive   = Color(0xFF185FA5)  // matches icon
private val NavLabelInactive = Color(0xFF94A3B8)  // slate-400
private val NavDivider       = Color(0xFFEEF2F7)  // barely-there top border

// ─── Navigation item model ────────────────────────────────────────────────────

private data class NavigationItem(
    val label: String,
    val icon: ImageVector
)

// ─── Main screen (unchanged except bottomBar) ─────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var navigationStack by remember { mutableStateOf(listOf<String>()) }
    val currentScreen = navigationStack.lastOrNull()

    val screenTitle = when (currentScreen) {
        "student_enrollment" -> "Student Enrollment"
        "attendance_marking" -> "Attendance Marking"
        "diet_management"    -> "Diet Management"
        "stock_management"   -> "Stock Management"
        "stock_updates"      -> "Stock Updates"
        "health_record"      -> "Health Record"
        "health_measurement_form" -> "Health Measurement"
        "progress_rating"    -> "Progress Rating"
        "weekly_activity"    -> "Weekly Activity Plan"
        "students"           -> "Students"
        "reports"            -> "Reports"
        "settings"           -> "Settings"
        else                 -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (screenTitle != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text       = screenTitle,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
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
                        containerColor         = SkyBlue40,
                        titleContentColor      = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        // ── ENHANCED BOTTOM BAR ───────────────────────────────────────────────
        bottomBar = {
            if (currentScreen == null) {
                ModernBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        navigationStack = listOf()
                    }
                )
            }
        }
    ) { innerPadding ->
        BackHandler(enabled = navigationStack.isNotEmpty()) {
            navigationStack = navigationStack.dropLast(1)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen ?: "dashboard_${selectedTab}",
                transitionSpec = {
                    slideIntoContainer(
                        towards      = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) togetherWith slideOutOfContainer(
                        towards      = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                label = "screen_transition"
            ) { targetScreen ->
                when {
                    targetScreen == "dashboard_0" -> DashboardScreen(
                        onNavigateToStudentEnrollment = {
                            navigationStack = navigationStack + "student_enrollment"
                        },
                        onNavigateToAttendanceMarking = {
                            navigationStack = navigationStack + "attendance_marking"
                        },
                        onNavigateToDietManagement = {
                            navigationStack = navigationStack + "diet_management"
                        },
                        onNavigateToStockManagement = {
                            navigationStack = navigationStack + "stock_management"
                        },
                        onNavigateToHealthRecord = {
                            navigationStack = navigationStack + "health_record"
                        },
                        onNavigateToProgressRating = {
                            navigationStack = navigationStack + "progress_rating"
                        },
                        onNavigateToWeeklyActivity = {
                            navigationStack = navigationStack + "weekly_activity"
                        },
                        onNavigateToReports = {
                            navigationStack = listOf("reports")
                        },
                        viewModel = hiltViewModel()
                    )
                    targetScreen == "dashboard_1" -> StudentsScreen()
                    targetScreen == "dashboard_2" -> ReportsScreen()
                    targetScreen == "dashboard_3" -> SettingsScreen(onLogout = onLogout)
                    else -> when (targetScreen) {
                        "student_enrollment" -> StudentEnrollmentScreen(
                            onSuccess = {
                                navigationStack = navigationStack.dropLast(1)
                            }
                        )
                        "attendance_marking" -> AttendanceMarkingScreen()
                        "diet_management"    -> DietManagementScreen()
                        "stock_management"   -> StockManagementScreen(
                            onViewStocks = {
                                navigationStack = navigationStack + "stock_updates"
                            }
                        )
                        "stock_updates"      -> StockUpdatesScreen()
                        "health_record"      -> {
                            val healthViewModel: HealthViewModel = hiltViewModel()
                            HealthRecordScreen(
                                onStudentClick = { student ->
                                    healthViewModel.selectStudent(student)
                                    navigationStack = navigationStack + "health_measurement_form"
                                }
                            )
                        }
                        "health_measurement_form" -> {
                            val healthViewModel: HealthViewModel = hiltViewModel()
                            HealthMeasurementFormScreen(
                                onSaveSuccess = {
                                    navigationStack = navigationStack.dropLast(1)
                                },
                                viewModel = healthViewModel
                            )
                        }
                        "progress_rating"    -> ProgressRatingScreen(
                            viewModel = hiltViewModel()
                        )
                        "weekly_activity"    -> WeeklyActivityPlanScreen(
                            viewModel = hiltViewModel()
                        )
                        "students"           -> StudentsScreen()
                        "reports"            -> ReportsScreen()
                        "settings"           -> SettingsScreen(onLogout = onLogout)
                    }
                }
            }
        }
    }
}

// ─── Modern bottom navigation bar ─────────────────────────────────────────────

@Composable
private fun ModernBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItem("Home",     Icons.Default.Home),
        NavigationItem("Students", Icons.Default.People),
        NavigationItem("Reports",  Icons.Default.Assessment),
        NavigationItem("Settings", Icons.Default.Settings)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Top divider line
                drawLine(
                    color       = NavDivider,
                    start       = Offset(0f, 0f),
                    end         = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .background(NavBg)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                ModernNavItem(
                    item       = item,
                    isSelected = selectedTab == index,
                    onClick    = { onTabSelected(index) },
                    modifier   = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─── Single nav item ──────────────────────────────────────────────────────────

@Composable
private fun ModernNavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pill width spring
    val pillWidth by animateDpAsState(
        targetValue   = if (isSelected) 56.dp else 32.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "pillWidth"
    )

    // Icon translate (subtle upward nudge when active)
    val iconOffsetY by animateDpAsState(
        targetValue   = if (isSelected) (-2).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "iconY"
    )

    val iconTint by animateColorAsState(
        targetValue   = if (isSelected) NavActive else NavInactive,
        animationSpec = tween(220),
        label         = "tint"
    )
    val labelColor by animateColorAsState(
        targetValue   = if (isSelected) NavLabelActive else Color.Transparent,
        animationSpec = tween(180),
        label         = "labelColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Icon + active pill indicator
        Box(contentAlignment = Alignment.Center) {
            // Glow pill behind icon (active only)
            androidx.compose.animation.AnimatedVisibility(                visible = isSelected,
                enter   = fadeIn(tween(200)) + scaleIn(tween(200)),
                exit    = fadeOut(tween(150)) + scaleOut(tween(150))
            ) {
                Box(
                    modifier = Modifier
                        .width(pillWidth)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NavActiveGlow)
                )
            }

            Icon(
                imageVector        = item.icon,
                contentDescription = item.label,
                tint               = iconTint,
                modifier           = Modifier
                    .size(22.dp)
                    .offset(y = iconOffsetY)
            )
        }

        // Label — fades in/out with selection
        Text(
            text       = item.label,
            fontSize   = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color      = labelColor,
            maxLines   = 1
        )
    }
}
