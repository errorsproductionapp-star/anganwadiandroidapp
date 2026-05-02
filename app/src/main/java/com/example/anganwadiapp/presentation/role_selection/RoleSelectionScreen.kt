package com.example.anganwadiapp.presentation.role_selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modern SaaS Color Palette
val PrimaryBlue = Color(0xFF2563EB)
val BgLight = Color(0xFFF8FAFC)
val SuccessGreen = Color(0xFF10B981)
val TextDark = Color(0xFF1E293B)
val TextGray = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(onContinue: (String) -> Unit) {
    var selectedRole by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(PrimaryBlue, Color(0xFF60A5FA))),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudDone, "", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Anganwadi Connect",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            color = TextDark
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = TextDark
            )

            Text(
                text = "Choose your workspace to get started",
                fontSize = 16.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            RoleCard(
                title = "Anganwadi Staff",
                description = "Manage student attendance, health records, and nutrition data.",
                tamilDescription = "வருகை மற்றும் ஆரோக்கியத்தை நிர்வகிக்க",
                icon = Icons.Default.School,
                isSelected = selectedRole == "Staff",
                onClick = { selectedRole = "Staff" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleCard(
                title = "Parent / Guardian",
                description = "Track your child's growth, attendance, and learning progress.",
                tamilDescription = "குழந்தையின் முன்னேற்றத்தைக் கண்காணிக்க",
                icon = Icons.Default.FamilyRestroom,
                isSelected = selectedRole == "Parent",
                onClick = { selectedRole = "Parent" }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Premium SaaS style illustration area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BgLight, modifier = Modifier.size(48.dp))
                    Text("Secure Cloud Sync Enabled", color = Color.LightGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { if (selectedRole.isNotEmpty()) onContinue(selectedRole) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                enabled = selectedRole.isNotEmpty(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Continue to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(20.dp))
            }

            Text(
                text = "Secure Infrastructure • Privacy Protected",
                fontSize = 11.sp,
                color = TextGray,
                modifier = Modifier.padding(vertical = 24.dp),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    tamilDescription: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.02f else 1f)
    val borderColor by animateColorAsState(if (isSelected) PrimaryBlue else Color.Transparent)
    val containerColor by animateColorAsState(if (isSelected) Color.White else Color.White.copy(alpha = 0.7f))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)),
        shadowElevation = if (isSelected) 12.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else BgLight,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, null,
                    tint = if (isSelected) PrimaryBlue else TextGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 17.sp)
                Text(description, fontSize = 13.sp, color = TextGray, lineHeight = 18.sp)
                Text(
                    tamilDescription,
                    fontSize = 12.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.RadioButtonUnchecked, null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(24.dp))
            }
        }
    }
}