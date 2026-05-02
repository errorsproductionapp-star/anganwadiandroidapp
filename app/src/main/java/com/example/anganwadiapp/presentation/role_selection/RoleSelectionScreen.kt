package com.example.anganwadiapp.presentation.role_selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(onContinue: (String) -> Unit) {
    var selectedRole by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF0056D2)
                        ) {
                            // Placeholder for user avatar if needed
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Anganwadi Connect",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0056D2)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Status",
                            tint = Color(0xFF0056D2)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Welcome",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = "Please select your role to continue",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Text(
                text = "தொடர உங்கள் பாத்திரத்தைத் தேர்ந்தெடுக்கவும்",
                fontSize = 14.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            RoleCard(
                title = "Anganwadi Staff",
                description = "Manage student attendance, health records, and nutrition data.",
                tamilDescription = "மாணவர் வருகை மற்றும் ஆரோக்கியத்தைப் நிர்வகிக்கவும்",
                icon = Icons.Default.School,
                iconContainerColor = Color(0xFFE8F0FE),
                iconColor = Color(0xFF1967D2),
                isSelected = selectedRole == "Staff",
                onClick = { selectedRole = "Staff" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleCard(
                title = "Parent / Guardian",
                description = "Track your child's growth, attendance, and learning progress.",
                tamilDescription = "உங்கள் குழந்தையின் வளர்ச்சி மற்றும் முன்னேற்றத்தைக் கண்காணிக்கவும்",
                icon = Icons.Default.FamilyRestroom,
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF2E7D32),
                isSelected = selectedRole == "Parent",
                onClick = { selectedRole = "Parent" }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder for the illustration
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color(0xFFF0F4F8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "Illustration Placeholder", color = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { if (selectedRole.isNotEmpty()) onContinue(selectedRole) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056D2)),
                enabled = selectedRole.isNotEmpty()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By continuing, you agree to our Terms and Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
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
    iconContainerColor: Color,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0056D2)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconContainerColor,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                Text(
                    text = tamilDescription,
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    RoleSelectionScreen(onContinue = {})
}
