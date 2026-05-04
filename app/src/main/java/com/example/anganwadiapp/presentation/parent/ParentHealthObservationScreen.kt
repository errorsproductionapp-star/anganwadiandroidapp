package com.example.anganwadiapp.presentation.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anganwadiapp.core.preferences.PreferencesManager
import com.example.anganwadiapp.presentation.auth.parent.ParentHealthState
import com.example.anganwadiapp.presentation.auth.parent.ParentViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentHealthObservationScreen(
    onBack: () -> Unit,
    parentViewModel: ParentViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesManager = remember { PreferencesManager(context) }
    val healthState by parentViewModel.healthState.collectAsStateWithLifecycle()

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val displayDate = dateFormatter.format(selectedDate.time)

    LaunchedEffect(selectedDate) {
        scope.launch {
            val childId = preferencesManager.getParentChildId()
            val centerId = preferencesManager.getParentCenterId()
            if (childId.isNotEmpty() && centerId.isNotEmpty()) {
                parentViewModel.fetchHealthRecord(centerId, childId, displayDate)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.timeInMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Calendar.getInstance().apply { timeInMillis = millis }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = ParentBgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Health Observation", fontWeight = FontWeight.Bold, color = ParentTextDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(horizontal = 20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ParentCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Select Date", fontSize = 14.sp, color = ParentTextGray)
                        Text(displayDate, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = ParentPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (healthState) {
                is ParentHealthState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ParentPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading health record...", color = ParentTextGray, fontSize = 14.sp)
                        }
                    }
                }
                is ParentHealthState.Loaded -> {
                    val record = (healthState as ParentHealthState.Loaded).healthRecord
                    HealthRecordContent(record)
                }
                is ParentHealthState.NoData -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, null, tint = ParentTextGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No health record found for $displayDate", color = ParentTextGray, textAlign = TextAlign.Center)
                        }
                    }
                }
                is ParentHealthState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text((healthState as ParentHealthState.Error).message, color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun HealthRecordContent(record: Map<String, Any>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MeasurementSection(record)

        Spacer(modifier = Modifier.height(16.dp))

        VaccinationSection(record)

        Spacer(modifier = Modifier.height(16.dp))

        ActionsSection(record)
    }
}

@Composable
private fun MeasurementSection(record: Map<String, Any>) {
    Text("Measurements", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
    Spacer(modifier = Modifier.height(12.dp))

    val height = (record["height"] as? Number)?.toFloat() ?: 0f
    val weight = (record["weight"] as? Number)?.toFloat() ?: 0f
    val bmiStatus = record["bmiStatus"] as? String ?: ""
    val dateOfMeasurement = record["dateOfMeasurement"] as? String ?: ""

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RecordCard(
            icon = Icons.Default.Straighten,
            label = "Height",
            value = if (height > 0) "$height cm" else "Not recorded",
            color = Color(0xFF38BDF8)
        )

        RecordCard(
            icon = Icons.Default.MonitorWeight,
            label = "Weight",
            value = if (weight > 0) "$weight kg" else "Not recorded",
            color = Color(0xFF4ADE80)
        )

        val (bmiBgColor, bmiTextColor) = when (bmiStatus) {
            "Underweight" -> Pair(Color(0xFFFFF7ED), Color(0xFFFB923C))
            "Normal" -> Pair(Color(0xFFF0FDF4), Color(0xFF16A34A))
            "Overweight" -> Pair(Color(0xFFFAF5FF), Color(0xFFC084FC))
            "Obese" -> Pair(Color(0xFFFFF7ED), Color(0xFFFB923C))
            else -> Pair(ParentCardBg, ParentTextGray)
        }

        RecordCard(
            icon = Icons.Default.Favorite,
            label = "BMI Status",
            value = bmiStatus.ifEmpty { "Not calculated" },
            color = bmiTextColor,
            bgColor = bmiBgColor
        )

        RecordCard(
            icon = Icons.Default.Event,
            label = "Measurement Date",
            value = dateOfMeasurement.ifEmpty { "Not specified" },
            color = ParentPrimary
        )
    }
}

@Composable
private fun VaccinationSection(record: Map<String, Any>) {
    Text("Vaccination Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
    Spacer(modifier = Modifier.height(12.dp))

    val vaccinationName = record["vaccinationName"] as? String ?: ""
    val vaccinationDate = record["vaccinationDate"] as? String ?: ""
    val nextDueDate = record["nextDueDate"] as? String ?: ""

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RecordCard(
            icon = Icons.Default.Vaccines,
            label = "Vaccination",
            value = vaccinationName.ifEmpty { "None" },
            color = Color(0xFF8B5CF6)
        )

        RecordCard(
            icon = Icons.Default.Event,
            label = "Vaccination Date",
            value = vaccinationDate.ifEmpty { "Not specified" },
            color = Color(0xFF38BDF8)
        )

        RecordCard(
            icon = Icons.Default.Schedule,
            label = "Next Due Date",
            value = nextDueDate.ifEmpty { "Not specified" },
            color = Color(0xFFFB923C)
        )
    }
}

@Composable
private fun ActionsSection(record: Map<String, Any>) {
    Text("Actions & Remarks", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
    Spacer(modifier = Modifier.height(12.dp))

    val actionTaken = record["actionTaken"] as? String ?: ""
    val healthRemarks = record["healthRemarks"] as? String ?: ""

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RecordCard(
            icon = Icons.Default.CheckCircle,
            label = "Action Taken",
            value = actionTaken.ifEmpty { "None" },
            color = Color(0xFF16A34A)
        )

        if (healthRemarks.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notes, null, tint = Color(0xFFF43F5E), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Health Remarks", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFF43F5E))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(healthRemarks, fontSize = 14.sp, color = ParentTextDark, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun RecordCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    bgColor: Color = ParentCardBg
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = ParentTextGray, fontWeight = FontWeight.Medium)
                Text(
                    value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ParentTextDark,
                    maxLines = 3
                )
            }
        }
    }
}
