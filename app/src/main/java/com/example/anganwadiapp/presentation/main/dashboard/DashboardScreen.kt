package com.example.anganwadiapp.presentation.main.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryBlue = Color(0xFF0056D2)
private val PrimaryBlueDark = Color(0xFF003FA3)
private val AccentGreen = Color(0xFF2E7D32)
private val SurfaceBg = Color(0xFFF7F9FC)
private val CardBg = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF0D1B2A)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String = "Anganwadi Worker",
    onNavigateToStudentEnrollment: () -> Unit,
    onNavigateToAttendanceMarking: () -> Unit,
    onNavigateToDietManagement: () -> Unit,
    onNavigateToStockManagement: () -> Unit,
    onNavigateToHealthRecord: () -> Unit,
    onNavigateToProgressRating: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Greeting Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Greetings, $userName!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Here is your Center's summary for today",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        // Attendance Summary Container (Placeholder)
        ContainerCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            backgroundColor = Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryBlueDark)),
            textColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attendance Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Today's attendance data",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Split Row: Student Enrollment + Attendance Marking
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardTile(
                modifier = Modifier.weight(1f),
                title = "Student Enrollment",
                subtitle = "Manage enrollments",
                icon = Icons.Default.PersonAdd,
                tint = PrimaryBlue,
                onClick = onNavigateToStudentEnrollment
            )
            DashboardTile(
                modifier = Modifier.weight(1f),
                title = "Attendance",
                subtitle = "Mark attendance",
                icon = Icons.Default.CheckCircle,
                tint = AccentGreen,
                onClick = onNavigateToAttendanceMarking
            )
        }

        // Weekly Activity Plan Container
        ContainerCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Activity Plan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "View and manage weekly activities",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 2x2 Grid: Diet, Stock, Health, Progress
        // Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardTile(
                modifier = Modifier.weight(1f),
                title = "Diet Management",
                subtitle = "Track nutrition",
                icon = Icons.Default.Restaurant,
                tint = Color(0xFFE65100),
                onClick = onNavigateToDietManagement
            )
            DashboardTile(
                modifier = Modifier.weight(1f),
                title = "Stock Management",
                subtitle = "Manage inventory",
                icon = Icons.Default.Inventory2,
                tint = Color(0xFF6A1B9A),
                onClick = onNavigateToStockManagement
            )
        }

        // Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardTile(
                modifier = Modifier.weight(1f),
                title = "Health Record",
                subtitle = "Health tracking",
                icon = Icons.Default.Favorite,
                tint = Color(0xFFC62828),
                onClick = onNavigateToHealthRecord
            )
            DashboardTile(
                modifier = Modifier.weight(1f),
                title = "Progress Rating",
                subtitle = "Track progress",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                tint = Color(0xFF00838F),
                onClick = onNavigateToProgressRating
            )
        }

        // Report Container
        ContainerCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reports",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generate and view reports",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Bottom padding for nav bar
        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
private fun ContainerCard(
    modifier: Modifier = Modifier,
    backgroundColor: Brush = Brush.linearGradient(listOf(CardBg, CardBg)),
    textColor: Color = TextPrimary,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = CardBg
    ) {
        content()
    }
}

@Composable
private fun DashboardTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = CardBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
