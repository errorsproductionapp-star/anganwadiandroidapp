package com.example.anganwadiapp.presentation.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.presentation.auth.parent.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAttendancePreviewScreen(
    childId: String,
    centerId: String,
    onBack: () -> Unit,
    viewModel: ParentViewModel = hiltViewModel()
) {
    val attendanceState by viewModel.attendanceState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val selectedDateString = dateFormatter.format(Date(selectedDateMillis))

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDateMillis) {
        viewModel.fetchAttendance(centerId, childId, selectedDateString)
    }

    Scaffold(
        containerColor = ParentBgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Attendance Status", fontWeight = FontWeight.Bold, color = ParentTextDark) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetAttendanceState()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ParentTextDark)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        null,
                        tint = ParentPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        selectedDateString,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ParentTextDark,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { showDatePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ParentPrimary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Date", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (attendanceState) {
                is ParentAttendanceState.Idle,
                is ParentAttendanceState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ParentPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading attendance...", color = ParentTextGray, fontSize = 14.sp)
                        }
                    }
                }
                is ParentAttendanceState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Error", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                (attendanceState as ParentAttendanceState.Error).message,
                                textAlign = TextAlign.Center,
                                color = ParentTextDark,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchAttendance(centerId, childId, selectedDateString) },
                                colors = ButtonDefaults.buttonColors(containerColor = ParentPrimary)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is ParentAttendanceState.NoData -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Info, null, tint = ParentTextGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No attendance data",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ParentTextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Attendance has not been marked for $selectedDateString",
                                textAlign = TextAlign.Center,
                                color = ParentTextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                is ParentAttendanceState.Loaded -> {
                    val status = (attendanceState as ParentAttendanceState.Loaded).status
                    if (status.isPresent) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Present",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Your child was marked present on $selectedDateString",
                                    textAlign = TextAlign.Center,
                                    color = ParentTextDark,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Cancel,
                                    null,
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Absent",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = Color(0xFFEF9A9A), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Reason for Absence",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ParentTextGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (status.absentReason.isNotEmpty()) status.absentReason else "No reason provided",
                                    textAlign = TextAlign.Center,
                                    color = ParentTextDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Select a date to view attendance status",
                fontSize = 12.sp,
                color = ParentTextGray,
                textAlign = TextAlign.Center
            )
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDateMillis
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDateMillis = millis
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
