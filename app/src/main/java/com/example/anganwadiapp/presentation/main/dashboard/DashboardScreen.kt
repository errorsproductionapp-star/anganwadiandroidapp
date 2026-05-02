package com.example.anganwadiapp.presentation.main.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val Sky50 = Color(0xFFF0F9FF)
private val Sky100 = Color(0xFFE0F2FE)
private val Sky200 = Color(0xFFBAE6FD)
private val Sky300 = Color(0xFF7DD3FC)
private val Sky600 = Color(0xFF0284C7)
private val Sky700 = Color(0xFF0369A1)
private val Gray50 = Color(0xFFF8FAFC)
private val Gray100 = Color(0xFFF1F5F9)
private val Gray200 = Color(0xFFE2E8F0)
private val Gray300 = Color(0xFFCBD5E1)
private val Gray500 = Color(0xFF64748B)
private val Gray800 = Color(0xFF1E293B)
private val Gray900 = Color(0xFF0F172A)
private val Green600 = Color(0xFF16A34A)
private val Orange500 = Color(0xFFF97316)
private val Red500 = Color(0xFFEF4444)

data class DashboardStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val tint: Color,
    val bgColor: Color
)

data class DashboardAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val bgGradient: List<Color>,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    userName: String = "Anganwadi Worker",
    onNavigateToStudentEnrollment: () -> Unit,
    onNavigateToAttendanceMarking: () -> Unit,
    onNavigateToDietManagement: () -> Unit,
    onNavigateToStockManagement: () -> Unit,
    onNavigateToHealthRecord: () -> Unit,
    onNavigateToProgressRating: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val anganwadiNumber by viewModel.anganwadiCenterId.collectAsState()

//    val stats = listOf(
//        DashboardStat("Students", "128", Icons.Default.Groups, Sky600, Sky100),
//        DashboardStat("Present", "112", Icons.Default.CheckCircle, Green600, Color(0xFFDCFFE4)),
//        DashboardStat("Absent", "16", Icons.Default.Cancel, Red500, Color(0xFFFFE4E6)),
//        DashboardStat("Attendance", "87%", Icons.AutoMirrored.Filled.TrendingUp, Orange500, Color(0xFFFFE8D6))
//    )

    val actions = listOf(
        DashboardAction("Student Enrollment", "Manage enrollments", Icons.Default.PersonAdd, Sky600, listOf(Sky50, Sky100), onNavigateToStudentEnrollment),
        DashboardAction("Attendance", "Mark attendance", Icons.Default.HowToReg, Green600, listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7)), onNavigateToAttendanceMarking),
        DashboardAction("Diet Management", "Track nutrition", Icons.Default.Restaurant, Color(0xFFE65100), listOf(Color(0xFFFFF7ED), Color(0xFFFFEDD5)), onNavigateToDietManagement),
        DashboardAction("Stock Management", "Manage inventory", Icons.Default.Inventory2, Color(0xFF6A1B9A), listOf(Color(0xFFF3E8FF), Color(0xFFEDE9FE)), onNavigateToStockManagement),
        DashboardAction("Health Record", "Health tracking", Icons.Default.Favorite, Color(0xFFC62828), listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6)), onNavigateToHealthRecord),
        DashboardAction("Progress Rating", "Track progress", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF00838F), listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE)), onNavigateToProgressRating)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .verticalScroll(rememberScrollState())
    ) {
        // Reduced Height Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Sky600, Sky700)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Welcome back,",
                        fontSize = 12.sp,
                        color = Sky200
                    )
                    Text(
                        userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Anganwadi Center Dashboard",
                        fontSize = 11.sp,
                        color = Sky300
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF60A5FA)))
                    Spacer(Modifier.width(8.dp))
                    Text("$anganwadiNumber", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White, letterSpacing = 0.3.sp)
                }
            }
        }

        // Stats Section - 2x2 grid to prevent overlapping
//        Spacer(Modifier.height(16.dp))
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            stats.chunked(2).forEach { rowStats ->
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    rowStats.forEach { stat ->
//                        StatCard(
//                            stat = stat,
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//            }
//        }

        Spacer(Modifier.height(24.dp))

        // Quick Actions Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Quick Actions",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            Text(
                "View All",
                fontSize = 13.sp,
                color = Sky600,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Action Cards Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowActions.forEach { action ->
                        ActionCard(
                            action = action,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowActions.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Management Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Management",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
        }

        // Activity & Report Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ManagementCard(
                title = "Weekly Activity Plan",
                subtitle = "View and manage weekly activities",
                icon = Icons.Default.CalendarMonth,
                tint = Sky600,
                bgGradient = listOf(Sky50, Sky100)
            )
            ManagementCard(
                title = "Reports & Analytics",
                subtitle = "Generate and view reports",
                icon = Icons.Default.Assessment,
                tint = Gray500,
                bgGradient = listOf(Gray100, Gray200)
            )
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun StatCard(
    stat: DashboardStat,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(stat.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                stat.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )
            Text(
                stat.title,
                fontSize = 12.sp,
                color = Gray500,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActionCard(
    action: DashboardAction,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = action.bgGradient + listOf(Color.White),
                        startY = 0f,
                        endY = 500f
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = action.onClick
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(action.bgGradient[0]),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = action.tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        action.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray800,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        action.subtitle,
                        fontSize = 12.sp,
                        color = Gray500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ManagementCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    bgGradient: List<Color>,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(bgGradient[0], Color.White),
                        startX = 0f,
                        endX = 600f
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgGradient[0]),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray800,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        subtitle,
                        fontSize = 13.sp,
                        color = Gray500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Gray300,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
