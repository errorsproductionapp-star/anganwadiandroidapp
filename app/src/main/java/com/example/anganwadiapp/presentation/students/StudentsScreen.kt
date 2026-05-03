package com.example.anganwadiapp.presentation.students

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

private val Sky50    = Color(0xFFF0F9FF)
private val Sky100   = Color(0xFFE0F2FE)
private val Sky200   = Color(0xFFBAE6FD)
private val Sky300   = Color(0xFF7DD3FC)
private val Sky400   = Color(0xFF38BDF8)
private val Sky500   = Color(0xFF0EA5E9)
private val Sky600   = Color(0xFF0284C7)
private val Sky700   = Color(0xFF0369A1)
private val Gray50   = Color(0xFFF8FAFC)
private val Gray100  = Color(0xFFF1F5F9)
private val Gray200  = Color(0xFFE2E8F0)
private val Gray300  = Color(0xFFCBD5E1)
private val Gray400  = Color(0xFF94A3B8)
private val Gray500  = Color(0xFF64748B)
private val Gray600  = Color(0xFF475569)
private val Gray700  = Color(0xFF334155)
private val Gray800  = Color(0xFF1E293B)
private val Gray900  = Color(0xFF0F172A)
private val Red400   = Color(0xFFF87171)
private val Red500   = Color(0xFFEF4444)
private val Red50    = Color(0xFFFEF2F2)
private val Red100   = Color(0xFFFEE2E2)
private val Green500 = Color(0xFF22C55E)
private val Green50  = Color(0xFFF0FDF4)
private val White    = Color(0xFFFFFFFF)
private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Sky50)) {
        if (state.isLoading && state.students.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Sky500, strokeWidth = 3.dp)
                    Spacer(Modifier.height(12.dp))
                    Text("Loading students...", color = Gray500, fontSize = 14.sp)
                }
            }
        } else if (state.students.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.People, null, tint = Gray300, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No students enrolled yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Gray500)
                    Text("Enroll students to see them here", fontSize = 13.sp, color = Gray400)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Enrolled Students",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Gray900
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${state.students.size} student${if (state.students.size != 1) "s" else ""} registered",
                                    fontSize = 13.sp,
                                    color = Gray400
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Sky100)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    state.centerId.takeLast(6),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Sky700
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.students, key = { it.id }) { student ->
                        StudentCard(
                            student = student,
                            onEdit = { viewModel.startEdit(student) },
                            onDelete = { viewModel.deleteStudent(student.id) },
                            onCardClick = { viewModel.selectStudent(student) }
                        )
                    }
                }
            }
        }

        if (state.editingStudent != null) {
            EditStudentDialog(
                student = state.editingStudent!!,
                onDismiss = { viewModel.cancelEdit() },
                onSave = { updated -> viewModel.saveEdit(updated) }
            )
        }

        AnimatedVisibility(
            visible = state.selectedStudent != null,
            enter = fadeIn() + scaleIn(initialScale = 0.92f),
            exit = fadeOut() + scaleOut(targetScale = 0.92f)
        ) {
            val student = state.selectedStudent ?: return@AnimatedVisibility
            key(student.id) {
                StudentDetailPopup(
                    student = student,
                    onDismiss = { viewModel.clearSelectedStudent() }
                )
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: Child,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCardClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val avatarColors = listOf(Sky400, Sky600)
    val genderColor = when (student.gender) {
        Gender.MALE -> Sky500
        Gender.FEMALE -> Color(0xFFEC4899)
        Gender.OTHER -> Color(0xFF8B5CF6)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = White,
        shadowElevation = if (isPressed) 6.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCardClick
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> isPressed = true
                            PointerEventType.Release -> isPressed = false
                        }
                    }
                }
            }
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 14.dp, 12.dp, 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(avatarColors))
                        .border(2.dp, genderColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        student.name.take(2).uppercase(),
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        student.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = Gray400,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            student.fatherName,
                            fontSize = 12.sp,
                            color = Gray500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            null,
                            tint = Gray400,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            student.fatherMobile.ifEmpty { "N/A" },
                            fontSize = 12.sp,
                            color = Gray500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "ID: ${student.id.takeLast(10)}",
                            fontSize = 12.sp,
                            color = Gray900,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onEdit() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, null, tint = Gray400, modifier = Modifier.size(18.dp))
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { showDeleteDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Delete, null, tint = Gray400, modifier = Modifier.size(18.dp))
                }
            }

            Divider(color = Gray100, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (student.age.isNotEmpty()) {
                    InfoChip(
                        icon = Icons.Default.CalendarToday,
                        text = student.age,
                        tint = Sky600,
                        bg = Sky100
                    )
                }

                if (student.bloodGroup.isNotEmpty()) {
                    InfoChip(
                        icon = Icons.Default.Bloodtype,
                        text = student.bloodGroup,
                        tint = Red500,
                        bg = Red50
                    )
                }

                InfoChip(
                    icon = Icons.Default.Wc,
                    text = student.gender.name.lowercase().replaceFirstChar { it.uppercase() },
                    tint = genderColor,
                    bg = genderColor.copy(alpha = 0.12f)
                )

                if (student.physicallyChallenged) {
                    InfoChip(
                        icon = Icons.Default.Accessibility,
                        text = "PC",
                        tint = Color(0xFF8B5CF6),
                        bg = Color(0xFFF3E8FF)
                    )
                }

                Spacer(Modifier.weight(1f))


            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Student") },
            text = { Text("Do you sure you want to delete ${student.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = Red500, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color,
    bg: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(12.dp))
        Text(
            text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint
        )
    }
}

@Composable
private fun EditStudentDialog(
    student: Child,
    onDismiss: () -> Unit,
    onSave: (Child) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var fatherName by remember { mutableStateOf(student.fatherName) }
    var motherName by remember { mutableStateOf(student.motherName) }
    var fatherMobile by remember { mutableStateOf(student.fatherMobile) }
    var motherMobile by remember { mutableStateOf(student.motherMobile) }
    var dateOfBirth by remember { mutableStateOf(student.dateOfBirth) }
    var admissionDate by remember { mutableStateOf(student.admissionDate) }
    var age by remember { mutableStateOf(student.age) }
    var gender by remember { mutableStateOf(student.gender) }
    var placeOfBirth by remember { mutableStateOf(student.placeOfBirth) }
    var bloodGroup by remember { mutableStateOf(student.bloodGroup) }
    var height by remember { mutableStateOf(student.height?.toString() ?: "") }
    var weight by remember { mutableStateOf(student.weight?.toString() ?: "") }
    var allergies by remember { mutableStateOf(student.allergies) }
    var healthNotes by remember { mutableStateOf(student.healthNotes) }
    var physicallyChallenged by remember { mutableStateOf(student.physicallyChallenged) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Gray50,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Sky400, Sky600))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, null, tint = White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Edit Student", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gray900)
                    Text(student.id, fontSize = 11.sp, color = Gray400, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(scrollState)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionHeader("Personal Information")

                EditField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    icon = Icons.Default.Person,
                    placeholder = "Enter child's full name"
                )

                EditField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = "Father's Name",
                    icon = Icons.Default.Person,
                    placeholder = "Enter father's name"
                )

                EditField(
                    value = motherName,
                    onValueChange = { motherName = it },
                    label = "Mother's Name",
                    icon = Icons.Default.Person,
                    placeholder = "Enter mother's name"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditField(
                        modifier = Modifier.weight(1f),
                        value = fatherMobile,
                        onValueChange = { if (it.length <= 10) fatherMobile = it },
                        label = "Father's Mobile",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        placeholder = "Mobile number"
                    )
                    EditField(
                        modifier = Modifier.weight(1f),
                        value = motherMobile,
                        onValueChange = { if (it.length <= 10) motherMobile = it },
                        label = "Mother's Mobile",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        placeholder = "Mobile number"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditField(
                        modifier = Modifier.weight(1f),
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = "Date of Birth",
                        icon = Icons.Default.CalendarToday,
                        placeholder = "DD/MM/YYYY"
                    )
                    EditField(
                        modifier = Modifier.weight(1f),
                        value = admissionDate,
                        onValueChange = { admissionDate = it },
                        label = "Admission Date",
                        icon = Icons.Default.Event,
                        placeholder = "DD/MM/YYYY"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GENDER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 2.dp, bottom = 6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Gender.entries.forEach { g ->
                                val isSelected = gender == g
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Sky500 else White)
                                        .border(1.dp, if (isSelected) Sky500 else Gray200, RoundedCornerShape(10.dp))
                                        .clickable { gender = g }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        g.name.lowercase().replaceFirstChar { it.uppercase() },
                                        color = if (isSelected) White else Gray600,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("PHYSICALLY CHALLENGED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 2.dp, bottom = 6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(false to "No", true to "Yes").forEach { (isYes, label) ->
                                val isSelected = physicallyChallenged == isYes
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) if (isYes) Red500 else Green500 else White)
                                        .border(1.dp, if (isSelected) if (isYes) Red500 else Green500 else Gray200, RoundedCornerShape(10.dp))
                                        .clickable { physicallyChallenged = isYes }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = if (isSelected) White else Gray600, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                SectionHeader("Health Information")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditField(
                        modifier = Modifier.weight(1f),
                        value = height,
                        onValueChange = { height = it },
                        label = "Height (cm)",
                        icon = Icons.Default.Straighten,
                        keyboardType = KeyboardType.Decimal,
                        placeholder = "e.g., 110.5"
                    )
                    EditField(
                        modifier = Modifier.weight(1f),
                        value = weight,
                        onValueChange = { weight = it },
                        label = "Weight (kg)",
                        icon = Icons.Default.MonitorWeight,
                        keyboardType = KeyboardType.Decimal,
                        placeholder = "e.g., 22.3"
                    )
                }

                EditField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = "Allergies",
                    icon = Icons.Default.Warning,
                    placeholder = "List any known allergies"
                )

                EditField(
                    value = healthNotes,
                    onValueChange = { healthNotes = it },
                    label = "Health Notes",
                    icon = Icons.Default.Note,
                    placeholder = "Any other health-related information",
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        student.copy(
                            name = name,
                            fatherName = fatherName,
                            motherName = motherName,
                            fatherMobile = fatherMobile,
                            motherMobile = motherMobile,
                            dateOfBirth = dateOfBirth,
                            admissionDate = admissionDate,
                            age = age,
                            gender = gender,
                            placeOfBirth = placeOfBirth,
                            bloodGroup = bloodGroup,
                            height = height.toFloatOrNull(),
                            weight = weight.toFloatOrNull(),
                            allergies = allergies,
                            healthNotes = healthNotes,
                            physicallyChallenged = physicallyChallenged
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Sky500),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp).padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = White)
                Spacer(Modifier.width(6.dp))
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Gray200),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Gray600),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    )
}

@Composable
private fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Box(Modifier.height(2.dp).width(20.dp).clip(CircleShape).background(Sky400))
        Spacer(Modifier.width(8.dp))
        Text(
            title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Gray400,
            letterSpacing = 1.2.sp
        )
        Divider(
            modifier = Modifier.weight(1f).padding(start = 10.dp),
            color = Gray200,
            thickness = 1.dp
        )
    }
}

@Composable
private fun EditField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = "",
    maxLines: Int = 1
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = if (isFocused) Sky400 else Gray200
    val bgColor = if (isFocused) White else Gray50
    val iconTint = if (isFocused) Sky500 else Gray400

    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray500, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(color = Gray800, fontSize = 14.sp, fontWeight = FontWeight.Normal),
            cursorBrush = SolidColor(Sky500),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (maxLines == 1) 44.dp else 84.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
        ) { inner ->
            Row(
                Modifier.fillMaxSize().padding(horizontal = 11.dp).padding(vertical = if (maxLines == 1) 0.dp else 8.dp),
                verticalAlignment = if (maxLines == 1) Alignment.CenterVertically else Alignment.Top
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Gray300, fontSize = 13.sp)
                    }
                    inner()
                }
            }
        }
    }
}

@Composable
private fun StudentDetailPopup(
    student: Child,
    onDismiss: () -> Unit
) {
    val genderColor = when (student.gender) {
        Gender.MALE -> Sky500
        Gender.FEMALE -> Color(0xFFEC4899)
        Gender.OTHER -> Color(0xFF8B5CF6)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Morphic glass overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )

        // Morphic popup card
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Sky50.copy(alpha = 0.6f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            Sky200.copy(alpha = 0.7f),
                            Sky300.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header with avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Sky400, Sky600)))
                            .border(2.dp, genderColor.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            student.name.take(2).uppercase(),
                            color = White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            student.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            "ID: ${student.id.takeLast(10)}",
                            fontSize = 11.sp,
                            color = Gray400,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Gray400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Divider(color = Sky100, thickness = 1.dp)

                // Details grid
                Spacer(Modifier.height(16.dp))

                DetailRow("Father's Name", student.fatherName, Icons.Default.Person)
                DetailRow("Mother's Name", student.motherName, Icons.Default.Person)
                DetailRow("Father's Mobile", student.fatherMobile.ifEmpty { "N/A" }, Icons.Default.Phone)
                DetailRow("Mother's Mobile", student.motherMobile.ifEmpty { "N/A" }, Icons.Default.Phone)
                DetailRow("Date of Birth", student.dateOfBirth.ifEmpty { "N/A" }, Icons.Default.CalendarToday)
                DetailRow("Admission Date", student.admissionDate.ifEmpty { "N/A" }, Icons.Default.Event)
                DetailRow("Age", student.age.ifEmpty { "N/A" }, Icons.Default.CalendarToday)
                DetailRow("Blood Group", student.bloodGroup.ifEmpty { "N/A" }, Icons.Default.Bloodtype)
                DetailRow("Place of Birth", student.placeOfBirth.ifEmpty { "N/A" }, Icons.Default.Place)
                DetailRow(
                    "Gender",
                    student.gender.name.lowercase().replaceFirstChar { it.uppercase() },
                    Icons.Default.Wc
                )
                DetailRow(
                    "Physically Challenged",
                    if (student.physicallyChallenged) "Yes" else "No",
                    Icons.Default.Accessibility
                )

                if (student.height != null) {
                    DetailRow("Height", "${student.height} cm", Icons.Default.Straighten)
                }
                if (student.weight != null) {
                    DetailRow("Weight", "${student.weight} kg", Icons.Default.FitnessCenter)
                }
                if (student.allergies.isNotEmpty()) {
                    DetailRow("Allergies", student.allergies, Icons.Default.Warning)
                }
                if (student.healthNotes.isNotEmpty()) {
                    DetailRow("Health Notes", student.healthNotes, Icons.Default.MedicalServices)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Sky50),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Sky600, modifier = Modifier.size(16.dp))
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Gray400,
                letterSpacing = 0.5.sp
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        }
    }
}
