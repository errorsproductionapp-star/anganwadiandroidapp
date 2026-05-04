package com.example.anganwadiapp.presentation.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anganwadiapp.core.preferences.PreferencesManager
import com.example.anganwadiapp.presentation.auth.parent.ParentViewModel
import com.example.anganwadiapp.presentation.auth.parent.ParentRatingState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentActivityRatingScreen(
    onBack: () -> Unit,
    parentViewModel: ParentViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesManager = remember { PreferencesManager(context) }

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val ratingState by parentViewModel.ratingState.collectAsStateWithLifecycle()

    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val displayDate = dateFormatter.format(selectedDate.time)

    LaunchedEffect(selectedDate) {
        scope.launch {
            val childId = preferencesManager.getParentChildId()
            val centerId = preferencesManager.getParentCenterId()
            if (childId.isNotEmpty() && centerId.isNotEmpty()) {
                parentViewModel.fetchProgressRating(centerId, displayDate, childId)
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
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        selectedDate = cal
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
                title = { Text("Activity Rating", fontWeight = FontWeight.Bold, color = ParentTextDark) },
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
                .padding(24.dp)
        ) {
            // Date Selector
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

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = ratingState) {
                is ParentRatingState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ParentPrimary)
                    }
                }
                is ParentRatingState.Loaded -> {
                    val ratings = state.ratingData
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RatingItem("Food", (ratings["food"] as? Number)?.toFloat() ?: 0f)
                        RatingItem("Education", (ratings["education"] as? Number)?.toFloat() ?: 0f)
                        RatingItem("Activity", (ratings["activity"] as? Number)?.toFloat() ?: 0f)
                        RatingItem("Health", (ratings["health"] as? Number)?.toFloat() ?: 0f)
                        RatingItem("Preparedness", (ratings["preparedness"] as? Number)?.toFloat() ?: 0f)

                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ParentPrimary.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, null, tint = ParentPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Overall Rating", fontSize = 14.sp, color = ParentTextGray)
                                    Text(
                                        "${(ratings["overall"] as? Number)?.toFloat() ?: 0f}/5",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ParentTextDark
                                    )
                                }
                            }
                        }
                    }
                }
                is ParentRatingState.NoData -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, null, tint = ParentTextGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No ratings available for $displayDate", color = ParentTextGray)
                        }
                    }
                }
                is ParentRatingState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun RatingItem(label: String, rating: Float) {
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
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ParentTextDark)
            Row {
                repeat(5) { index ->
                    Icon(
                        if (index < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                        null,
                        tint = if (index < rating.toInt()) Color(0xFFFFB800) else ParentTextGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("%.1f".format(rating), fontSize = 14.sp, color = ParentTextGray)
            }
        }
    }
}
