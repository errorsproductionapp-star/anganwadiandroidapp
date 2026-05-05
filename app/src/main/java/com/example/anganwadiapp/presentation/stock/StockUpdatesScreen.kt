package com.example.anganwadiapp.presentation.stock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
private val Green400 = Color(0xFF4ADE80)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockUpdatesScreen(
    viewModel: StockViewModel = hiltViewModel()
) {
    val stockDates by viewModel.stockDates.collectAsStateWithLifecycle()
    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle()
    val isLoading by viewModel.isStocksLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()

    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatesList by remember { mutableStateOf(true) }
    var editingItem by remember { mutableStateOf<StockItem?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        if (selectedDate != null) {
            viewModel.loadStockItemsForDate(selectedDate!!)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadStockDates()
    }

    if (showEditDialog && editingItem != null) {
        EditStockDialog(
            item = editingItem!!,
            onSave = { updatedItem ->
                viewModel.updateStockItem(updatedItem) {
                    showEditDialog = false
                    editingItem = null
                }
            },
            onCancel = {
                showEditDialog = false
                editingItem = null
            }
        )
    }

    if (saveSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetSaveState() },
            title = { Text("Success", color = Gray900) },
            text = { Text("Item updated successfully!", color = Gray900) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetSaveState() }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        if (isLoading && (showDatesList || stockItems.isEmpty())) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Sky400)
            }
        } else if (showDatesList) {
            if (stockDates.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Gray400,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No stock records found",
                            color = Gray500,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Sky100,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = Sky400, modifier = Modifier.size(20.dp))
                            }
                        }
                        Text(
                            text = "Select Date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(stockDates.size) { index ->
                            Surface(
                                onClick = {
                                    selectedDate = stockDates[index]
                                    showDatesList = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = Sky400
                                        )
                                        Text(
                                            text = stockDates[index],
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = Gray900
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Gray400
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Gray500,
                        modifier = Modifier.clickable {
                            selectedDate = null
                            showDatesList = true
                        }
                    )
                    Text(
                        text = "Stocks - ${selectedDate ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gray900
                    )
                }

                if (stockItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items found for this date",
                            color = Gray500,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(stockItems.size) { index ->
                            StockItemCard(
                                item = stockItems[index],
                                onEdit = {
                                    editingItem = it
                                    showEditDialog = true
                                }
                            )
                        }
                    }
                }
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
}

@Composable
fun StockItemCard(
    item: StockItem,
    onEdit: (StockItem) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.itemName.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
                IconButton(onClick = { onEdit(item) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Sky400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = Gray100, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.Numbers,
                    label = "Quantity",
                    value = "${item.quantity} ${item.unit}"
                )
                InfoRow(
                    icon = Icons.Default.Business,
                    label = "Source",
                    value = item.source
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStockDialog(
    item: StockItem,
    onSave: (StockItem) -> Unit,
    onCancel: () -> Unit
) {
    var itemName by remember { mutableStateOf(item.itemName) }
    var quantity by remember { mutableStateOf(item.quantity) }
    var selectedUnit by remember { mutableStateOf(item.unit) }
    var selectedSource by remember { mutableStateOf(item.source) }

    val units = listOf("kg", "litres", "pieces")
    val sources = listOf("Government supply", "NGO", "Donation")

    var showUnitDropdown by remember { mutableStateOf(false) }
    var showSourceDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Edit Stock Item", color = Color(0xFFF9FAFB)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name", color = Color(0xFFF9FAFB)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFFF9FAFB),      // very light (almost white)
                        unfocusedTextColor = Color(0xFFE5E7EB),    // light gray
                        focusedLabelColor = Color(0xFFF9FAFB),     // same as focused text
                        unfocusedLabelColor = Color(0xFFD1D5DB)    // soft light gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity", color = Color(0xFFF9FAFB)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFFF9FAFB),      // very light (almost white)
                            unfocusedTextColor = Color(0xFFE5E7EB),    // light gray
                            focusedLabelColor = Color(0xFFF9FAFB),     // same as focused text
                            unfocusedLabelColor = Color(0xFFD1D5DB)    // soft light gray
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Box(modifier = Modifier.width(120.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = showUnitDropdown,
                            onExpandedChange = { showUnitDropdown = !showUnitDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedUnit,
                                onValueChange = {},
                                label = { Text("Unit", color = Color(0xFFF9FAFB)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUnitDropdown) },
                                readOnly = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFFF9FAFB),      // very light (almost white)
                                    unfocusedTextColor = Color(0xFFE5E7EB),    // light gray
                                    focusedLabelColor = Color(0xFFF9FAFB),     // same as focused text
                                    unfocusedLabelColor = Color(0xFFD1D5DB)    // soft light gray
                                ),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = showUnitDropdown,
                                onDismissRequest = { showUnitDropdown = false }
                            ) {
                                units.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            selectedUnit = unit
                                            showUnitDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = showSourceDropdown,
                        onExpandedChange = { showSourceDropdown = !showSourceDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedSource,
                            onValueChange = {},
                            label = { Text("Source", color = Color(0xFFF9FAFB)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSourceDropdown) },
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFFF9FAFB),      // very light (almost white)
                                unfocusedTextColor = Color(0xFFE5E7EB),    // light gray
                                focusedLabelColor = Color(0xFFF9FAFB),     // same as focused text
                                unfocusedLabelColor = Color(0xFFD1D5DB)    // soft light gray
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
        },
        confirmButton = {
            Button(
                onClick = {
                    if (itemName.isNotBlank() && quantity.isNotBlank()) {
                        onSave(
                            StockItem(
                                documentId = item.documentId,
                                dateReceived = item.dateReceived,
                                itemName = itemName,
                                quantity = quantity,
                                source = selectedSource,
                                unit = selectedUnit
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Gray400
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray900,
            fontWeight = FontWeight.Medium
        )
    }
}
