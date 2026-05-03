package com.example.anganwadiapp.presentation.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anganwadiapp.presentation.auth.parent.ParentViewModel

val ParentPrimary = Color(0xFF7C3AED)
val ParentBgLight = Color(0xFFF5F3FF)
val ParentTextDark = Color(0xFF1E1B4B)
val ParentTextGray = Color(0xFF6B7280)
val ParentCardBg = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentMainScreen(
    childName: String,
    childId: String,
    onLogout: () -> Unit,
    onDailyUpdateClick: (String) -> Unit,
    onWeeklyUpdateClick: (String) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout? You will need to login again.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ParentPrimary)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = ParentBgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Parent Portal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
                        Text(childName, fontSize = 12.sp, color = ParentTextGray)
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = ParentTextDark)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Child Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ParentPrimary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ChildCare, null, tint = ParentPrimary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Welcome Parent", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
                        Text("ID: $childId", fontSize = 12.sp, color = ParentTextGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Daily Updates Section
            Text("Daily Updates", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ParentFeatureButton(
                    icon = Icons.Default.HowToReg,
                    title = "Attendance Status",
                    description = "Check today's attendance",
                    onClick = { onDailyUpdateClick("attendance") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.RestaurantMenu,
                    title = "Today's Food Menu",
                    description = "View daily nutrition plan",
                    onClick = { onDailyUpdateClick("food_menu") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.StarRate,
                    title = "Activity Rating",
                    description = "See today's activity scores",
                    onClick = { onDailyUpdateClick("activity_rating") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.Favorite,
                    title = "Health Observation",
                    description = "Daily health check updates",
                    onClick = { onDailyUpdateClick("health") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Weekly Updates Section
            Text("Weekly Updates", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ParentFeatureButton(
                    icon = Icons.Default.MonitorHeart,
                    title = "Weekly Health Summary",
                    description = "Health trends this week",
                    onClick = { onWeeklyUpdateClick("health_summary") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.Vaccines,
                    title = "Vaccination Updates",
                    description = "Upcoming and past vaccinations",
                    onClick = { onWeeklyUpdateClick("vaccination") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.Assessment,
                    title = "Activity Summary",
                    description = "Weekly activity overview",
                    onClick = { onWeeklyUpdateClick("activity_summary") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.CheckCircle,
                    title = "Weekly Attendance Summary",
                    description = "Attendance record this week",
                    onClick = { onWeeklyUpdateClick("attendance_summary") }
                )
                ParentFeatureButton(
                    icon = Icons.Default.LocalDining,
                    title = "Nutrition Report",
                    description = "Weekly nutrition analysis",
                    onClick = { onWeeklyUpdateClick("nutrition") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ParentFeatureButton(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ParentCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(ParentPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = ParentPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ParentTextDark)
                Text(description, fontSize = 12.sp, color = ParentTextGray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = ParentTextGray)
        }
    }
}
