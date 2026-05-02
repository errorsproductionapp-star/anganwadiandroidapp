package com.example.anganwadiapp.presentation.stock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anganwadiapp.presentation.components.AppTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

private val Sky50 = Color(0xFFF0F9FF)
private val Sky100 = Color(0xFFE0F2FE)
private val Sky200 = Color(0xFFBAE6FD)
private val Sky300 = Color(0xFF7DD3FC)
private val Sky400 = Color(0xFF38BDF8)
private val Gray50 = Color(0xFFF9FAFB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray500 = Color(0xFF6B7280)
private val Gray900 = Color(0xFF0F172A)
private val Green50 = Color(0xFFF0FDF4)
private val Green100 = Color(0xFFDCFCE7)
private val Green400 = Color(0xFF4ADE80)
private val Orange50 = Color(0xFFFFF7ED)
private val Orange100 = Color(0xFFFFEDD5)
private val Orange400 = Color(0xFFFB923C)

enum class StockModule {
    RECEIVED, UTILIZED
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    viewModel: StockViewModel = hiltViewModel()
) {
    var selectedModule by remember { mutableStateOf(StockModule.RECEIVED) }
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        AppTopBar(title = "Stock Management")

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ModuleSelector(
                selectedModule = selectedModule,
                onModuleSelected = { selectedModule = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedModule) {
                StockModule.RECEIVED -> ItemReceivedForm(viewModel)
                StockModule.UTILIZED -> ItemUtilizedForm(viewModel)
            }
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetSaveState() },
            title = { Text("Error", color = Gray900) },
            text = { Text(errorMessage!!, color = Gray900) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetSaveState() }) {
                    Text("OK")
                }
            }
        )
    }

    if (saveSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetSaveState() },
            title = { Text("Success", color = Gray900) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedCheckmark()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Item saved successfully!", color = Gray900)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.resetSaveState() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun AnimatedCheckmark() {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500),
        label = "checkmark"
    )

    Canvas(modifier = Modifier.size(60.dp)) {
        drawArc(
            color = Green400,
            startAngle = -90f,
            sweepAngle = 360f * animationProgress,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        if (animationProgress > 0.5f) {
            drawLine(
                color = Green400,
                start = center.copy(x = center.x - 15.dp.toPx(), y = center.y),
                end = center.copy(x = center.x - 5.dp.toPx(), y = center.y + 10.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Green400,
                start = center.copy(x = center.x - 5.dp.toPx(), y = center.y + 10.dp.toPx()),
                end = center.copy(x = center.x + 15.dp.toPx(), y = center.y - 10.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun ModuleSelector(
    selectedModule: StockModule,
    onModuleSelected: (StockModule) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ModuleButton(
            label = "Item Received",
            icon = Icons.Default.AddBox,
            isSelected = selectedModule == StockModule.RECEIVED,
                    selectedColor = Green400,
                    selectedColorLight = Green100,
            onClick = { onModuleSelected(StockModule.RECEIVED) },
            modifier = Modifier.weight(1f)
        )

        ModuleButton(
            label = "Item Utilized",
            icon = Icons.Default.RemoveShoppingCart,
            isSelected = selectedModule == StockModule.UTILIZED,
                    selectedColor = Orange400,
                    selectedColorLight = Orange100,
            onClick = { onModuleSelected(StockModule.UTILIZED) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ModuleButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    selectedColorLight: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) selectedColorLight else Gray50,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Gray200),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) selectedColor else Gray400,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = if (isSelected) selectedColor else Gray500,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemReceivedForm(viewModel: StockViewModel) {
    var itemName by remember { mutableStateOf("") }
    var dateReceived by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) }
    var quantity by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("kg") }
    var selectedSource by remember { mutableStateOf("Government supply") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSourceDropdown by remember { mutableStateOf(false) }

    val clearFields by viewModel.clearFields.collectAsStateWithLifecycle()

    LaunchedEffect(clearFields) {
        if (clearFields) {
            itemName = ""
            quantity = ""
            selectedUnit = "kg"
            selectedSource = "Government supply"
            dateReceived = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            viewModel.clearFieldsHandled()
        }
    }

    val units = listOf("kg", "litres", "pieces")
    val sources = listOf("Government supply", "NGO", "Donation")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(title = "Item Details", icon = Icons.Default.Inventory)
        }

        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name", color = Gray900) },
                        leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Gray900) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Gray900,
                            unfocusedTextColor = Gray900,
                            focusedLabelColor = Gray900,
                            unfocusedLabelColor = Gray500
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dateReceived,
                        onValueChange = { dateReceived = it },
                        label = { Text("Date Received", color = Gray900) },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Gray900) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Gray900,
                            unfocusedTextColor = Gray900,
                            focusedLabelColor = Gray900,
                            unfocusedLabelColor = Gray500
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity", color = Gray900) },
                            leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = Gray900) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Gray900,
                                unfocusedTextColor = Gray900,
                                focusedLabelColor = Gray900,
                                unfocusedLabelColor = Gray500
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Box(modifier = Modifier.width(120.dp)) {
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedUnit,
                                    onValueChange = {},
                                    label = { Text("Unit", color = Gray900) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    readOnly = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Gray900,
                                        unfocusedTextColor = Gray900,
                                        focusedLabelColor = Gray900,
                                        unfocusedLabelColor = Gray500
                                    ),
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    units.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                selectedUnit = unit
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box {
                        ExposedDropdownMenuBox(
                            expanded = showSourceDropdown,
                            onExpandedChange = { showSourceDropdown = !showSourceDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedSource,
                                onValueChange = {},
                                label = { Text("Source / Supplier", color = Gray900) },
                                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Gray900) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSourceDropdown) },
                                readOnly = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Gray900,
                                    unfocusedTextColor = Gray900,
                                    focusedLabelColor = Gray900,
                                    unfocusedLabelColor = Gray500
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = showSourceDropdown,
                                onDismissRequest = { showSourceDropdown = false }
                            ) {
                                sources.forEach { source ->
                                    DropdownMenuItem(
                                        text = { Text(source) },
                                        onClick = {
                                            selectedSource = source
                                            showSourceDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (itemName.isNotBlank() && quantity.isNotBlank()) {
                        viewModel.saveReceivedItem(
                            itemName = itemName,
                            date = dateReceived,
                            quantity = quantity,
                            unit = selectedUnit,
                            source = selectedSource
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green400),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Received Item", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        dateReceived = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    showDatePicker = false
                }) {
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemUtilizedForm(viewModel: StockViewModel) {
    var itemName by remember { mutableStateOf("") }
    var dateUsed by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) }
    var quantity by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("kg") }
    var selectedPurpose by remember { mutableStateOf("Meal Preparation") }
    var usedBy by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val clearFields by viewModel.clearFields.collectAsStateWithLifecycle()

    LaunchedEffect(clearFields) {
        if (clearFields) {
            itemName = ""
            quantity = ""
            selectedUnit = "kg"
            selectedPurpose = "Meal Preparation"
            usedBy = ""
            dateUsed = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            viewModel.clearFieldsHandled()
        }
    }

    val units = listOf("kg", "litres", "pieces")
    val purposes = listOf("Meal Preparation", "Activity", "Hygiene")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(title = "Utilization Details", icon = Icons.Default.TrendingDown)
        }

        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name", color = Gray900) },
                        leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Gray900) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Gray900,
                            unfocusedTextColor = Gray900,
                            focusedLabelColor = Gray900,
                            unfocusedLabelColor = Gray500
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dateUsed,
                        onValueChange = { dateUsed = it },
                        label = { Text("Date of Use", color = Gray900) },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Gray900) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Gray900,
                            unfocusedTextColor = Gray900,
                            focusedLabelColor = Gray900,
                            unfocusedLabelColor = Gray500
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity Used", color = Gray900) },
                            leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = Gray900) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Gray900,
                                unfocusedTextColor = Gray900,
                                focusedLabelColor = Gray900,
                                unfocusedLabelColor = Gray500
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Box(modifier = Modifier.width(120.dp)) {
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedUnit,
                                    onValueChange = {},
                                    label = { Text("Unit", color = Gray900) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    readOnly = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Gray900,
                                        unfocusedTextColor = Gray900,
                                        focusedLabelColor = Gray900,
                                        unfocusedLabelColor = Gray500
                                    ),
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    units.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                selectedUnit = unit
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box {
                        var showPurposeDropdown by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = showPurposeDropdown,
                            onExpandedChange = { showPurposeDropdown = !showPurposeDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedPurpose,
                                onValueChange = {},
                                label = { Text("Purpose", color = Gray900) },
                                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = Gray900) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPurposeDropdown) },
                                readOnly = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Gray900,
                                    unfocusedTextColor = Gray900,
                                    focusedLabelColor = Gray900,
                                    unfocusedLabelColor = Gray500
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = showPurposeDropdown,
                                onDismissRequest = { showPurposeDropdown = false }
                            ) {
                                purposes.forEach { purpose ->
                                    DropdownMenuItem(
                                        text = { Text(purpose) },
                                        onClick = {
                                            selectedPurpose = purpose
                                            showPurposeDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = usedBy,
                        onValueChange = { usedBy = it },
                        label = { Text("Used By", color = Gray900) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gray900) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Gray900,
                            unfocusedTextColor = Gray900,
                            focusedLabelColor = Gray900,
                            unfocusedLabelColor = Gray500
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (itemName.isNotBlank() && quantity.isNotBlank()) {
                        viewModel.saveUtilizedItem(
                            itemName = itemName,
                            date = dateUsed,
                            quantity = quantity,
                            unit = selectedUnit,
                            usedFor = selectedPurpose
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange400),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Utilized Item", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        dateUsed = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    showDatePicker = false
                }) {
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

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Sky100,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(icon, contentDescription = null, tint = Sky400, modifier = Modifier.size(20.dp))
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Gray900
        )
    }
}
