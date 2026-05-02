package com.example.anganwadiapp.presentation.enrollment

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Gender
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

private val White        = Color(0xFFFFFFFF)
private val Sky50        = Color(0xFFF0F9FF)
private val Sky100       = Color(0xFFE0F2FE)
private val Sky200       = Color(0xFFBAE6FD)
private val Sky300       = Color(0xFF7DD3FC)
private val Sky400       = Color(0xFF38BDF8)
private val Sky500       = Color(0xFF0EA5E9)
private val Sky600       = Color(0xFF0284C7)
private val Gray50       = Color(0xFFF8FAFC)
private val Gray100      = Color(0xFFF1F5F9)
private val Gray200      = Color(0xFFE2E8F0)
private val Gray300      = Color(0xFFCBD5E1)
private val Gray400      = Color(0xFF94A3B8)
private val Gray500      = Color(0xFF64748B)
private val Gray600      = Color(0xFF475569)
private val Gray700      = Color(0xFF334155)
private val Gray800      = Color(0xFF1E293B)
private val Gray900      = Color(0xFF0F172A)
private val Green500     = Color(0xFF22C55E)
private val Red500       = Color(0xFFEF4444)

private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

private fun DrawScope.drawLightBackground(animValue: Float) {
    drawRect(color = Sky50)
    val orb1X = size.width * (0.78f + 0.05f * cos(animValue))
    val orb1Y = size.height * (0.12f + 0.04f * sin(animValue * 0.7f))
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Sky200.copy(alpha = 0.5f), Color.Transparent),
            center = Offset(orb1X, orb1Y), radius = size.width * 0.55f
        ),
        radius = size.width * 0.55f, center = Offset(orb1X, orb1Y)
    )
    val orb2X = size.width * (0.18f + 0.04f * sin(animValue * 0.5f))
    val orb2Y = size.height * (0.75f + 0.05f * cos(animValue * 0.8f))
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Sky100.copy(alpha = 0.4f), Color.Transparent),
            center = Offset(orb2X, orb2Y), radius = size.width * 0.5f
        ),
        radius = size.width * 0.5f, center = Offset(orb2X, orb2Y)
    )
}

@Composable
fun StudentEnrollmentScreen(
    onSuccess: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: EnrollmentViewModel = hiltViewModel()
) {
    val formData by viewModel.formData.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    var currentStep by remember { mutableStateOf(1) }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is Result.Success -> {
                Toast.makeText(context, "Enrollment saved successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetForm()
                currentStep = 1
                onSuccess()
            }
            is Result.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgAnim by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing)),
        label         = "orb"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawLightBackground(bgAnim) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            HeaderCard()

            Spacer(Modifier.height(20.dp))

            StepIndicator(currentStep = currentStep, totalSteps = 3)

            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = White,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Sky200, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (currentStep) {
                        1 -> PersonalDataStep(formData, viewModel)
                        2 -> HealthDataStep(formData, viewModel)
                        3 -> PreviewStep(formData)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Sky600
                        ),
                        border = BorderStroke(1.5.dp, Sky300),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }

                if (currentStep < 3) {
                    Button(
                        onClick = { currentStep++ },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Sky500
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            if (formData.name.isBlank() || formData.fatherName.isBlank()) {
                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.saveEnrollment()
                            }
                        },
                        enabled = saveState !is Result.Loading,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green500
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        AnimatedContent(targetState = saveState is Result.Loading, label = "btn") { loading ->
                            if (loading) {
                                CircularProgressIndicator(
                                    color = White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save Enrollment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderCard() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Sky300, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Sky400, Sky500))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PersonAdd, null, tint = White, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "New Enrollment",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gray900,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Fill all credentials of the child to enroll for the Anganwadi Connect program",
                fontSize = 13.sp,
                color = Gray500,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    val labels = listOf("Personal", "Health", "Preview")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(totalSteps) { index ->
                val active = index + 1 == currentStep
                val done = index + 1 < currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                active -> Sky500
                                done -> Sky300
                                else -> Gray200
                            }
                        )
                )
                if (index < totalSteps - 1) {
                    Spacer(Modifier.width(4.dp))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            labels.forEachIndexed { index, label ->
                val active = index + 1 == currentStep
                Text(
                    label,
                    fontSize = 10.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    color = if (active) Sky600 else Gray400
                )
            }
        }
    }
}

@Composable
private fun PersonalDataStep(
    formData: EnrollmentFormData,
    viewModel: EnrollmentViewModel
) {
    val focusManager = LocalFocusManager.current
    val fr1 = remember { FocusRequester() }
    val fr2 = remember { FocusRequester() }
    val fr3 = remember { FocusRequester() }
    val fr4 = remember { FocusRequester() }
    val fr5 = remember { FocusRequester() }
    val fr6 = remember { FocusRequester() }
    val fr7 = remember { FocusRequester() }
    val fr8 = remember { FocusRequester() }
    val fr9 = remember { FocusRequester() }
    val fr10 = remember { FocusRequester() }

    val onNext: (FocusRequester) -> Unit = { focusManager.moveFocus(FocusDirection.Down); it.requestFocus() }

    SectionLabel("Personal Details")

    EnrollmentField(
        value = formData.name,
        onValueChange = { viewModel.updateField { copy(name = it) } },
        label = "Child Name *",
        icon = Icons.Default.Person,
        focusRequester = fr1,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    DateField(
        label = "Date of Birth *",
        icon = Icons.Default.CalendarToday,
        onDateSelected = { millis, formatted ->
            viewModel.updateField {
                copy(
                    dateOfBirthMillis = millis,
                    dateOfBirth = formatted,
                    age = calculateAge(millis)
                )
            }
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    DateField(
        label = "Admission Date",
        icon = Icons.Default.Event,
        onDateSelected = { millis, formatted ->
            viewModel.updateField {
                copy(
                    admissionDateMillis = millis,
                    admissionDate = formatted
                )
            }
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    EnrollmentField(
        value = formData.age,
        onValueChange = {},
        label = "Age (Auto-calculated)",
        icon = Icons.Default.Info,
        enabled = false,
        focusRequester = fr4,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    GenderSelector(
        selected = formData.gender,
        onSelected = { viewModel.updateField { copy(gender = it) } }
    )

    EnrollmentField(
        value = formData.fatherName,
        onValueChange = { viewModel.updateField { copy(fatherName = it) } },
        label = "Father's Name *",
        icon = Icons.Default.Person,
        focusRequester = fr5,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.motherName,
        onValueChange = { viewModel.updateField { copy(motherName = it) } },
        label = "Mother's Name",
        icon = Icons.Default.Person,
        focusRequester = fr6,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.fatherMobile,
        onValueChange = { if (it.length <= 10) viewModel.updateField { copy(fatherMobile = it) } },
        label = "Father's Mobile",
        icon = Icons.Default.Phone,
        keyboardType = KeyboardType.Phone,
        focusRequester = fr7,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.motherMobile,
        onValueChange = { if (it.length <= 10) viewModel.updateField { copy(motherMobile = it) } },
        label = "Mother's Mobile",
        icon = Icons.Default.Phone,
        keyboardType = KeyboardType.Phone,
        focusRequester = fr8,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.placeOfBirth,
        onValueChange = { viewModel.updateField { copy(placeOfBirth = it) } },
        label = "Place of Birth",
        icon = Icons.Default.LocationOn,
        focusRequester = fr9,
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    BloodGroupSelector(
        selected = formData.bloodGroup,
        onSelected = { viewModel.updateField { copy(bloodGroup = it) } }
    )

    PhysicallyChallengedSelector(
        value = formData.physicallyChallenged,
        onToggle = { viewModel.updateField { copy(physicallyChallenged = it) } }
    )
}

@Composable
private fun HealthDataStep(
    formData: EnrollmentFormData,
    viewModel: EnrollmentViewModel
) {
    val focusManager = LocalFocusManager.current

    SectionLabel("Health Information")

    EnrollmentField(
        value = formData.height,
        onValueChange = { viewModel.updateField { copy(height = it) } },
        label = "Height (cm)",
        icon = Icons.Default.Straighten,
        keyboardType = KeyboardType.Decimal,
        hint = "Enter height in centimeters",
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.weight,
        onValueChange = { viewModel.updateField { copy(weight = it) } },
        label = "Weight (kg)",
        icon = Icons.Default.MonitorWeight,
        keyboardType = KeyboardType.Decimal,
        hint = "Enter weight in kilograms",
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.allergies,
        onValueChange = { viewModel.updateField { copy(allergies = it) } },
        label = "Allergies",
        icon = Icons.Default.Warning,
        hint = "List any known allergies",
        imeAction = ImeAction.Next,
        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
    )

    EnrollmentField(
        value = formData.healthNotes,
        onValueChange = { viewModel.updateField { copy(healthNotes = it) } },
        label = "Health Notes",
        icon = Icons.Default.Note,
        hint = "Any other health-related information",
        maxLines = 4,
        imeAction = ImeAction.Done,
        onImeAction = { focusManager.clearFocus() }
    )
}

@Composable
private fun PreviewStep(formData: EnrollmentFormData) {
    SectionLabel("Enrollment Preview")

    PreviewSection(title = "Personal Information") {
        PreviewRow("Name", formData.name)
        PreviewRow("Date of Birth", formData.dateOfBirth.ifEmpty { "Not set" })
        PreviewRow("Admission Date", formData.admissionDate.ifEmpty { "Not set" })
        PreviewRow("Age", formData.age)
        PreviewRow("Gender", formData.gender.name)
        PreviewRow("Father's Name", formData.fatherName)
        PreviewRow("Mother's Name", formData.motherName.ifEmpty { "Not set" })
        PreviewRow("Father's Mobile", formData.fatherMobile.ifEmpty { "Not set" })
        PreviewRow("Mother's Mobile", formData.motherMobile.ifEmpty { "Not set" })
        PreviewRow("Place of Birth", formData.placeOfBirth.ifEmpty { "Not set" })
        PreviewRow("Blood Group", formData.bloodGroup.ifEmpty { "Not set" })
        PreviewRow("Physically Challenged", if (formData.physicallyChallenged) "Yes" else "No")
    }

    Spacer(Modifier.height(8.dp))

    PreviewSection(title = "Health Information") {
        PreviewRow("Height", if (formData.height.isNotEmpty()) "${formData.height} cm" else "Not set")
        PreviewRow("Weight", if (formData.weight.isNotEmpty()) "${formData.weight} kg" else "Not set")
        PreviewRow("Allergies", formData.allergies.ifEmpty { "None" })
        PreviewRow("Health Notes", formData.healthNotes.ifEmpty { "None" })
    }
}

@Composable
private fun PreviewSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Gray400,
            letterSpacing = 1.2.sp
        )

        Surface(
            color = Sky50,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Gray500, modifier = Modifier.weight(0.4f))
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Gray800,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(3.dp, 14.dp)
                .clip(CircleShape)
                .background(Sky500)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Gray400,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
private fun EnrollmentField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    hint: String? = null,
    maxLines: Int = 1,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Sky400 else Gray200,
        animationSpec = tween(200),
        label = "border"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isFocused) Sky500 else Gray400,
        animationSpec = tween(200),
        label = "label"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = labelColor)
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            enabled = enabled,
            cursorBrush = SolidColor(Sky500),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = if (maxLines == 1) imeAction else ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onNext = { if (maxLines == 1) onImeAction() },
                onDone = { if (maxLines == 1) onImeAction() }
            ),
            textStyle = TextStyle(color = if (enabled) Gray800 else Gray400, fontSize = 15.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (maxLines == 1) 48.dp else 100.dp)
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) Gray50 else Gray100)
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
        ) { inner ->
            Row(
                Modifier.fillMaxSize().padding(horizontal = 12.dp).padding(vertical = if (maxLines == 1) 0.dp else 8.dp),
                verticalAlignment = if (maxLines == 1) Alignment.CenterVertically else Alignment.Top
            ) {
                Icon(icon, null, tint = Gray400, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(label, color = Gray300, fontSize = 14.sp)
                    }
                    inner()
                }
            }
        }

        if (hint != null) {
            Spacer(Modifier.height(4.dp))
            Text(hint, fontSize = 11.sp, color = Gray400, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
private fun DateField(
    label: String,
    icon: ImageVector,
    onDateSelected: (Long, String) -> Unit
) {
    val context = LocalContext.current
    var selectedDateText by remember { mutableStateOf("") }
    var selectedMillis by remember { mutableLongStateOf(0L) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedMillis = calendar.timeInMillis
            selectedDateText = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            onDateSelected(selectedMillis, selectedDateText)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray400)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Gray50)
                .border(1.5.dp, Gray200, RoundedCornerShape(12.dp))
                .clickable { datePickerDialog.show() }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Gray400, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = if (selectedDateText.isNotEmpty()) selectedDateText else label,
                color = if (selectedDateText.isNotEmpty()) Gray800 else Gray300,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun GenderSelector(
    selected: Gender,
    onSelected: (Gender) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("GENDER", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray400)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Gender.entries.forEach { gender ->
                val isSelected = selected == gender
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) Sky500 else White
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) Sky500 else Gray200,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelected(gender) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        gender.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = if (isSelected) White else Gray600,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BloodGroupSelector(
    selected: String,
    onSelected: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("BLOOD GROUP", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray400)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bloodGroups.forEach { group ->
                val isSelected = selected == group
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) Sky500 else White
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) Sky500 else Gray200,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelected(group) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        group,
                        color = if (isSelected) White else Gray600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PhysicallyChallengedSelector(
    value: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PHYSICALLY CHALLENGED", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray400)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(false to "No", true to "Yes").forEach { (isYes, label) ->
                val isSelected = value == isYes
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) if (isYes) Red500 else Green500 else White
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) if (isYes) Red500 else Green500 else Gray200,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onToggle(isYes) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (isSelected) White else Gray600,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun calculateAge(dobMillis: Long): String {
    if (dobMillis <= 0L) return ""
    val dob = Calendar.getInstance().apply { timeInMillis = dobMillis }
    val today = Calendar.getInstance()

    var years = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    var months = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH)

    if (months < 0) {
        years--
        months += 12
    }

    return if (years > 0) {
        "$years year${if (years > 1) "s" else ""}, $months month${if (months != 1) "s" else ""}"
    } else {
        "$months month${if (months != 1) "s" else ""}"
    }
}
