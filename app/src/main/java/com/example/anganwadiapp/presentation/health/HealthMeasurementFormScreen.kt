package com.example.anganwadiapp.presentation.health

import android.app.DatePickerDialog
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

private val Rose400 = Color(0xFFFB7185)
private val Rose500 = Color(0xFFF43F5E)
private val Gray50 = Color(0xFFF9FAFB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray500 = Color(0xFF6B7280)
private val Gray700 = Color(0xFF374151)
private val Gray900 = Color(0xFF0F172A)
private val Green50 = Color(0xFFF0FDF4)
private val Green100 = Color(0xFFDCFCE7)
private val Green600 = Color(0xFF16A34A)
private val Orange50 = Color(0xFFFFF7ED)
private val Orange100 = Color(0xFFFFEDD5)
private val Orange400 = Color(0xFFFB923C)
private val Sky50 = Color(0xFFF0F9FF)
private val Sky100 = Color(0xFFE0F2FE)
private val Sky400 = Color(0xFF38BDF8)
private val Sky500 = Color(0xFF0EA5E9)
private val Sky600 = Color(0xFF0284C7)
private val Purple50 = Color(0xFFFAF5FF)
private val Purple100 = Color(0xFFF3E8FF)
private val Purple400 = Color(0xFFC084FC)
private val Gray800 = Color(0xFF1F2937) // Add this line
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMeasurementFormScreen(
    onSaveSuccess: () -> Unit = {},
    viewModel: HealthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formData by viewModel.formData.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val heightFocus = remember { FocusRequester() }
    val weightFocus = remember { FocusRequester() }
    val vaccinationNameFocus = remember { FocusRequester() }
    val vaccinationDateFocus = remember { FocusRequester() }
    val nextDueDateFocus = remember { FocusRequester() }
    val actionTakenFocus = remember { FocusRequester() }
    val healthRemarksFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        heightFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        Text(
            formData.studentName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            FormSection(title = "STUDENT INFORMATION")

            ReadOnlyField(
                value = formData.studentName,
                label = "Student Name",
                icon = Icons.Default.Person
            )

            Spacer(Modifier.height(12.dp))

            DateField(
                value = formData.dateOfMeasurement,
                label = "Date of Measurement",
                icon = Icons.Default.CalendarToday,
                onDateSelected = { viewModel.updateFormField { copy(dateOfMeasurement = it) } }
            )

            Spacer(Modifier.height(16.dp))

            FormSection(title = "MEASUREMENTS")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumericField(
                    modifier = Modifier.weight(1f),
                    value = formData.height,
                    label = "Height (cm)",
                    icon = Icons.Default.Straighten,
                    onValueChange = {
                        viewModel.updateFormField { copy(height = it) }
                        viewModel.calculateAndSetBmiStatus()
                    },
                    focusRequester = heightFocus,
                    nextFocus = { weightFocus.requestFocus() }
                )

                NumericField(
                    modifier = Modifier.weight(1f),
                    value = formData.weight,
                    label = "Weight (kg)",
                    icon = Icons.Default.MonitorWeight,
                    onValueChange = {
                        viewModel.updateFormField { copy(weight = it) }
                        viewModel.calculateAndSetBmiStatus()
                    },
                    focusRequester = weightFocus,
                    nextFocus = { vaccinationNameFocus.requestFocus() }
                )
            }

            Spacer(Modifier.height(12.dp))

            BmiStatusField(status = formData.bmiStatus)

            Spacer(Modifier.height(16.dp))

            FormSection(title = "VACCINATION DETAILS")

            FormTextField(
                value = formData.vaccinationName,
                label = "Vaccination Name",
                icon = Icons.Default.Vaccines,
                onValueChange = { viewModel.updateFormField { copy(vaccinationName = it) } },
                focusRequester = vaccinationNameFocus,
                nextFocus = { vaccinationDateFocus.requestFocus() }
            )

            Spacer(Modifier.height(12.dp))

            DateField(
                value = formData.vaccinationDate,
                label = "Vaccination Date",
                icon = Icons.Default.Event,
                onDateSelected = { viewModel.updateFormField { copy(vaccinationDate = it) } },
                focusRequester = vaccinationDateFocus,
                nextFocus = { nextDueDateFocus.requestFocus() }
            )

            Spacer(Modifier.height(12.dp))

            DateField(
                value = formData.nextDueDate,
                label = "Next Due Date",
                icon = Icons.Default.Schedule,
                onDateSelected = { viewModel.updateFormField { copy(nextDueDate = it) } },
                focusRequester = nextDueDateFocus,
                nextFocus = { actionTakenFocus.requestFocus() }
            )

            Spacer(Modifier.height(16.dp))

            FormSection(title = "ACTIONS & REMARKS")

            FormTextField(
                value = formData.actionTaken,
                label = "Action Taken",
                icon = Icons.Default.CheckCircle,
                onValueChange = { viewModel.updateFormField { copy(actionTaken = it) } },
                focusRequester = actionTakenFocus,
                nextFocus = { healthRemarksFocus.requestFocus() }
            )

            Spacer(Modifier.height(12.dp))

            MultiLineField(
                value = formData.healthRemarks,
                label = "Health Remarks",
                icon = Icons.Default.Notes,
                onValueChange = { viewModel.updateFormField { copy(healthRemarks = it) } },
                focusRequester = healthRemarksFocus
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveHealthRecord(
                        onSuccess = {
                            viewModel.resetForm()
                            onSaveSuccess()
                        },
                        onError = { /* Handle error - toast or snackbar */ }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Rose400),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Health Record", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormSection(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Box(Modifier.height(2.dp).width(20.dp).clip(CircleShape).background(Rose400))
        Spacer(Modifier.width(8.dp))
        Text(
            title,
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
private fun ReadOnlyField(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Gray100)
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Gray400, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray700,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NumericField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    nextFocus: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) Sky400 else Gray200
    val bgColor = if (isFocused) Color.White else Gray50
    val iconTint = if (isFocused) Sky500 else Gray400

    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))
        BasicTextField(
            value = value,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() || it == '.' }) {
                    val dotCount = input.count { it == '.' }
                    if (dotCount <= 1) {
                        onValueChange(input)
                    }
                }
            },
            interactionSource = interactionSource,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { nextFocus() }
            ),
            textStyle = TextStyle(color = Gray800, fontSize = 14.sp, fontWeight = FontWeight.Normal),
            cursorBrush = SolidColor(Sky500),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                .focusRequester(focusRequester)
        ) { inner ->
            Row(
                Modifier.fillMaxSize().padding(horizontal = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text("0.0", color = Gray300, fontSize = 13.sp)
                    }
                    inner()
                }
            }
        }
    }
}

@Composable
private fun BmiStatusField(status: String) {
    val (bgColor, textColor, icon) = when (status) {
        "Underweight" -> Triple(Orange50, Orange400, Icons.Default.Warning)
        "Normal" -> Triple(Green50, Green600, Icons.Default.CheckCircle)
        "Overweight" -> Triple(Purple50, Purple400, Icons.Default.Info)
        "Obese" -> Triple(Orange100, Orange400, Icons.Default.Error)
        else -> Triple(Gray50, Gray400, Icons.Default.Favorite)
    }

    Column {
        Text("BMI STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(1.5.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = textColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = if (status.isNotEmpty()) status else "Enter height & weight",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DateField(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onDateSelected: (String) -> Unit,
    focusRequester: FocusRequester? = null,
    nextFocus: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) Sky400 else Gray200
    val bgColor = if (isFocused) Color.White else Gray50
    val iconTint = if (isFocused) Sky500 else Gray400

    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                .clickable { datePickerDialog.show() }
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = if (value.isNotEmpty()) value else "Select date",
                fontSize = 14.sp,
                color = if (value.isNotEmpty()) Gray800 else Gray300,
                fontWeight = if (value.isNotEmpty()) FontWeight.Normal else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.CalendarToday, null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    nextFocus: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) Sky400 else Gray200
    val bgColor = if (isFocused) Color.White else Gray50
    val iconTint = if (isFocused) Sky500 else Gray400

    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { nextFocus() }
            ),
            textStyle = TextStyle(color = Gray800, fontSize = 14.sp, fontWeight = FontWeight.Normal),
            cursorBrush = SolidColor(Sky500),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                .focusRequester(focusRequester)
        ) { inner ->
            Row(
                Modifier.fillMaxSize().padding(horizontal = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(label, color = Gray300, fontSize = 13.sp)
                    }
                    inner()
                }
            }
        }
    }
}

@Composable
private fun MultiLineField(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) Sky400 else Gray200
    val bgColor = if (isFocused) Color.White else Gray50
    val iconTint = if (isFocused) Sky500 else Gray400

    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            singleLine = false,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            textStyle = TextStyle(color = Gray800, fontSize = 14.sp, fontWeight = FontWeight.Normal),
            cursorBrush = SolidColor(Sky500),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 84.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                .focusRequester(focusRequester)
        ) { inner ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(11.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(label, color = Gray300, fontSize = 13.sp)
                        }
                        inner()
                    }
                }
            }
        }
    }
}
