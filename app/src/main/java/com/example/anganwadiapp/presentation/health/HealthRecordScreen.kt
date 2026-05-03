package com.example.anganwadiapp.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Sky50 = Color(0xFFF0F9FF)
private val Sky100 = Color(0xFFE0F2FE)
private val Sky400 = Color(0xFF38BDF8)
private val Sky600 = Color(0xFF0284C7)
private val Sky700 = Color(0xFF0369A1)
private val Gray50 = Color(0xFFF9FAFB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray500 = Color(0xFF6B7280)
private val Gray700 = Color(0xFF374151)
private val Gray900 = Color(0xFF0F172A)
private val Green400 = Color(0xFF4ADE80)
private val Green50 = Color(0xFFF0FDF4)
private val Green100 = Color(0xFFDCFCE7)
private val Green600 = Color(0xFF16A34A)
private val Orange400 = Color(0xFFFB923C)
private val Orange50 = Color(0xFFFFF7ED)
private val Orange100 = Color(0xFFFFEDD5)
private val Rose400 = Color(0xFFFB7185)
private val Rose50 = Color(0xFFFFF1F2)
private val Rose100 = Color(0xFFFFE4E6)
private val Purple400 = Color(0xFFC084FC)
private val Purple50 = Color(0xFFFAF5FF)
private val Purple100 = Color(0xFFF3E8FF)
private val Amber400 = Color(0xFFFBBF24)
private val Amber50 = Color(0xFFFFFBEB)
private val Amber100 = Color(0xFFFFF3C1)

data class HealthRecord(
    val id: String,
    val name: String,
    val initials: String,
    val age: String,
    val bodyHeight: Float,
    val bodyWeight: Float,
    val bloodGroup: String,
    val bmi: Float,
    val bmiCategory: String,
    val bmiColor: Color,
    val lastCheckup: String,
    val immunizationStatus: ImmunizationStatus,
    val allergies: String,
    val notes: String
)

enum class ImmunizationStatus(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val color: Color, val bg: Color) {
    COMPLETE("Complete", Icons.Default.CheckCircle, Green600, Green100),
    PARTIAL("Partial", Icons.Default.Warning, Amber400, Amber100),
    PENDING("Pending", Icons.Default.Schedule, Rose400, Rose100)
}

enum class HealthTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ALL("All", Icons.Default.People),
    CHECKUP("Check-ups", Icons.Default.Medication),
    IMMUNIZATION("Immunization", Icons.Default.Shield),
    NUTRITION("Nutrition", Icons.Default.Restaurant)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(HealthTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRecord by remember { mutableStateOf<HealthRecord?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val records = remember {
        listOf(
            HealthRecord("1", "Priya Sharma", "PS", "4y 2m", 102f, 16.5f, "O+", 15.9f, "Normal", Green600, "15 Jan 2026", ImmunizationStatus.COMPLETE, "None", "Healthy growth pattern"),
            HealthRecord("2", "Rahul Kumar", "RK", "5y 1m", 108f, 18.2f, "B+", 15.6f, "Normal", Green600, "12 Jan 2026", ImmunizationStatus.COMPLETE, "Peanuts", "Regular checkup done"),
            HealthRecord("3", "Anita Devi", "AD", "3y 8m", 95f, 12.8f, "A+", 14.2f, "Underweight", Orange400, "10 Jan 2026", ImmunizationStatus.PARTIAL, "None", "Needs nutritional support"),
            HealthRecord("4", "Arjun Patel", "AP", "4y 6m", 105f, 22.0f, "AB+", 19.9f, "Overweight", Rose400, "08 Jan 2026", ImmunizationStatus.COMPLETE, "Dust", "Monitor diet closely"),
            HealthRecord("5", "Meena Raj", "MR", "5y 3m", 110f, 17.8f, "O-", 14.7f, "Normal", Green600, "05 Jan 2026", ImmunizationStatus.PENDING, "None", "Immunization due next week"),
            HealthRecord("6", "Kavitha M", "KM", "4y 0m", 98f, 14.5f, "B-", 15.1f, "Normal", Green600, "03 Jan 2026", ImmunizationStatus.COMPLETE, "None", "Good health status"),
            HealthRecord("7", "Suresh N", "SN", "3y 5m", 92f, 11.5f, "A-", 13.5f, "Underweight", Orange400, "01 Jan 2026", ImmunizationStatus.PARTIAL, "Milk", "Supplement nutrition advised")
        )
    }

    val filteredRecords = records.filter { r ->
        (selectedTab == HealthTab.ALL ||
         (selectedTab == HealthTab.CHECKUP) ||
         (selectedTab == HealthTab.IMMUNIZATION && r.immunizationStatus != ImmunizationStatus.COMPLETE) ||
         (selectedTab == HealthTab.NUTRITION && (r.bmiCategory == "Underweight" || r.bmiCategory == "Overweight"))) &&
        (searchQuery.isBlank() || r.name.contains(searchQuery, ignoreCase = true))
    }

    val stats = listOf(
        "Total" to "${records.size}",
        "Healthy" to "${records.count { it.bmiCategory == "Normal" }}",
        "Attention" to "${records.count { it.bmiCategory != "Normal" }}",
        "Immunized" to "${records.count { it.immunizationStatus == ImmunizationStatus.COMPLETE }}"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Rose400, Color(0xFFE11D48))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Health Records", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Track growth & immunization", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Stats Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    stats.forEach { (label, value) ->
                        StatChip(label = label, value = value, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name...", color = Gray400) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gray400) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Rose400,
                        unfocusedBorderColor = Gray200
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            // Tab Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HealthTab.values().forEach { tab ->
                        HealthTabChip(
                            tab = tab,
                            isSelected = tab == selectedTab,
                            onClick = { selectedTab = tab }
                        )
                    }
                }
            }

            // Records List
            items(filteredRecords) { record ->
                HealthRecordCard(
                    record = record,
                    onClick = { selectedRecord = record }
                )
                Spacer(Modifier.height(8.dp))
            }

            // Empty State
            item {
                if (filteredRecords.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Gray300, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No records found", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Gray500)
                    }
                }
            }
        }
    }

    if (selectedRecord != null) {
        HealthDetailDialog(
            record = selectedRecord!!,
            onDismiss = { selectedRecord = null }
        )
    }

    if (showAddDialog) {
        AddHealthRecordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { showAddDialog = false }
        )
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gray900)
            Text(label, fontSize = 10.sp, color = Gray500)
        }
    }
}

@Composable
private fun HealthTabChip(
    tab: HealthTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        // CORRECT
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Rose400 else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Gray200) else null,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(tab.icon, contentDescription = null, tint = if (isSelected) Color.White else Gray500, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                tab.label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else Gray500
            )
        }
    }
}

@Composable
private fun HealthRecordCard(
    record: HealthRecord,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(record.bmiColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    record.initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = record.bmiColor
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(record.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Age: ${record.age}", fontSize = 11.sp, color = Gray500)
                    Surface(shape = RoundedCornerShape(6.dp), color = record.bmiColor.copy(alpha = 0.12f)) {
                        Text(
                            record.bloodGroup,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = record.bmiColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${record.bodyHeight}cm / ${record.bodyWeight}kg",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        record.immunizationStatus.icon,
                        contentDescription = null,
                        tint = record.immunizationStatus.color,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        record.immunizationStatus.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = record.immunizationStatus.color
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthDetailDialog(
    record: HealthRecord,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Health Details", color = Gray900) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(record.bmiColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(record.initials, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = record.bmiColor)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(record.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        Text("Age: ${record.age}  •  Blood: ${record.bloodGroup}", fontSize = 12.sp, color = Gray500)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Measurements Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MeasurementCard(label = "Height", value = "${record.bodyHeight} cm", icon = Icons.Default.Accessibility, modifier = Modifier.weight(1f))
                    MeasurementCard(label = "Weight", value = "${record.bodyWeight} kg", icon = Icons.Default.MonitorWeight, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MeasurementCard(label = "BMI", value = String.format("%.1f", record.bmi), icon = Icons.Default.Favorite, valueColor = record.bmiColor, modifier = Modifier.weight(1f))
                    MeasurementCard(label = "Status", value = record.bmiCategory, icon = Icons.Default.Info, valueColor = record.bmiColor, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                Divider(color = Gray100)
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Immunization", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Gray700)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(record.immunizationStatus.icon, contentDescription = null, tint = record.immunizationStatus.color, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(record.immunizationStatus.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = record.immunizationStatus.color)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Last Check-up", fontSize = 13.sp, color = Gray500)
                    Text(record.lastCheckup, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray900)
                }

                if (record.allergies != "None") {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Allergies", fontSize = 13.sp, color = Gray500)
                        Surface(shape = RoundedCornerShape(6.dp), color = Rose50) {
                            Text(
                                record.allergies,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Rose400,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (record.notes.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Notes", fontSize = 13.sp, color = Gray500)
                    Spacer(Modifier.height(4.dp))
                    Text(record.notes, fontSize = 13.sp, color = Gray700)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Rose400)) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun MeasurementCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    valueColor: Color = Gray900
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Gray50
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Gray400, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(label, fontSize = 11.sp, color = Gray500)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHealthRecordDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var allergies by remember { mutableStateOf("") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var showBloodDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Health Record", color = Gray900) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height (cm)") },
                        leadingIcon = { Icon(Icons.Default.Accessibility, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = showBloodDropdown,
                    onExpandedChange = { showBloodDropdown = !showBloodDropdown }
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        leadingIcon = { Icon(Icons.Default.Bloodtype, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBloodDropdown) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showBloodDropdown,
                        onDismissRequest = { showBloodDropdown = false }
                    ) {
                        bloodGroups.forEach { bg ->
                            DropdownMenuItem(
                                text = { Text(bg) },
                                onClick = {
                                    bloodGroup = bg
                                    showBloodDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies (if any)") },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = name.isNotBlank() && heightInput.isNotBlank() && weightInput.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Rose400)
            ) {
                Text("Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
