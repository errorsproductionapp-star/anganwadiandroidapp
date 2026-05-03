package com.example.anganwadiapp.presentation.activity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val White = Color(0xFFFFFFFF)
private val OffWhite = Color(0xFFF9FAFB)
private val LightGray = Color(0xFFF1F5F9)
private val SoftGray = Color(0xFFE2E8F0)
private val MidGray = Color(0xFFCBD5E1)
private val TextGray = Color(0xFF64748B)
private val DarkText = Color(0xFF334155)
private val PrimaryText = Color(0xFF1E293B)

private val SoftBlue = Color(0xFF3B82F6)
private val SoftBlueLight = Color(0xFFDBEAFE)
private val SoftBlue50 = Color(0xFFEFF6FF)

private val SoftPink = Color(0xFFF472B6)
private val SoftPinkLight = Color(0xFFFCE7F3)
private val SoftPink50 = Color(0xFFFDF2F8)

private val SoftPurple = Color(0xFFA78BFA)
private val SoftPurpleLight = Color(0xFFEDE9FE)
private val SoftPurple50 = Color(0xFFF5F3FF)

private val SoftGreen = Color(0xFF34D399)
private val SoftGreenLight = Color(0xFFD1FAE5)
private val SoftGreen50 = Color(0xFFECFDF5)

private val SoftOrange = Color(0xFFFB923C)
private val SoftOrangeLight = Color(0xFFFED7AA)
private val SoftOrange50 = Color(0xFFFFF7ED)

private val SoftRose = Color(0xFFFDA4AF)
private val SoftRoseLight = Color(0xFFFFE4E6)
private val SoftRose50 = Color(0xFFFFF1F2)

enum class ActivityType(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val bg: Color,
    val bg50: Color
) {
    EDUCATIONAL("Educational", Icons.Rounded.School, SoftBlue, SoftBlueLight, SoftBlue50),
    PHYSICAL("Physical", Icons.Rounded.Favorite, SoftPink, SoftPinkLight, SoftPink50),
    CREATIVE("Creative", Icons.Rounded.Brush, SoftPurple, SoftPurpleLight, SoftPurple50)
}

data class DayActivity(
    val id: String = "",
    val activityName: String = "",
    val activityType: ActivityType = ActivityType.EDUCATIONAL,
    val duration: String = "",
    val materialsNeeded: String = ""
)

data class DayPlan(
    val dayName: String,
    val date: LocalDate,
    val dateFormatted: String,
    val isToday: Boolean,
    val activities: List<DayActivity> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyActivityPlanScreen(
    onBack: () -> Unit = {},
    viewModel: WeeklyActivityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val dateShortFormatter = DateTimeFormatter.ofPattern("dd MMM")

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }

    var viewDetailActivity by remember { mutableStateOf<DayActivity?>(null) }
    var editActivity by remember { mutableStateOf<DayActivity?>(null) }
    var confirmDeleteActivity by remember { mutableStateOf<DayActivity?>(null) }

    val startDate = selectedStartDate ?: uiState.weekDays.firstOrNull() ?: LocalDate.now()
    val weekDates = (0..4).map { startDate.plusDays(it.toLong()) }

    val weekPlans = remember { mutableStateListOf<DayPlan>() }

    LaunchedEffect(startDate, uiState.anganwadiCenterId) {
        weekPlans.clear()
        weekPlans.addAll(weekDates.map { date ->
            DayPlan(
                dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                date = date,
                dateFormatted = dateShortFormatter.format(date),
                isToday = date == LocalDate.now()
            )
        })

        if (uiState.anganwadiCenterId.isNotEmpty()) {
            weekDates.forEachIndexed { index, date ->
                viewModel.loadDayActivities(date) { activities ->
                    if (activities.isNotEmpty()) {
                        weekPlans[index] = weekPlans[index].copy(activities = activities)
                    }
                }
            }
        }
    }

    val selectedDay = if (weekPlans.isNotEmpty() && selectedDayIndex < weekPlans.size) weekPlans[selectedDayIndex] else return

    val weekRangeDisplay = if (weekPlans.isNotEmpty()) {
        "${dateShortFormatter.format(weekPlans.first().date)} - ${dateShortFormatter.format(weekPlans.last().date)}"
    } else ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Week Range Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (weekPlans.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape).background(SoftBlue)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            val first = weekPlans.first().date
                            val last = weekPlans.last().date
                            val rangeFmt = DateTimeFormatter.ofPattern("EEE, dd MMM")
                            Text(
                                "${rangeFmt.format(first)} – ${rangeFmt.format(last)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Text("5-Day Weekly Plan", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                IconButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(SoftBlue50)
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = "Pick Date", tint = SoftBlue, modifier = Modifier.size(18.dp))
                }
            }
            Divider(color = LightGray)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Day Strip
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(weekPlans.size) { index ->
                            DayChipModern(
                                dayName = weekPlans[index].dayName,
                                dayDate = weekPlans[index].dateFormatted,
                                isToday = weekPlans[index].isToday,
                                activityCount = weekPlans[index].activities.size,
                                isSelected = index == selectedDayIndex,
                                onClick = { selectedDayIndex = index }
                            )
                        }
                    }
                }

                // Day Summary Card
                item {
                    DaySummaryModern(
                        dayName = selectedDay.dayName,
                        date = selectedDay.dateFormatted,
                        activityCount = selectedDay.activities.size
                    )
                }

                // Add Button Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 20.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activities", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Button(
                            onClick = { editActivity = null; showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue50, contentColor = SoftBlue)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }

                // Activity List
                items(selectedDay.activities, key = { it.id }) { activity ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 },
                        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 4 }
                    ) {
                        ActivityCardModern(
                            activity = activity,
                            onClick = { viewDetailActivity = activity },
                            onDelete = { confirmDeleteActivity = activity },
                            onEdit = { editActivity = activity }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Empty State
                item {
                    if (selectedDay.activities.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 50.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(80.dp).clip(CircleShape).background(LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.EventNote, contentDescription = null, tint = MidGray, modifier = Modifier.size(36.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("No activities yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextGray)
                            Spacer(Modifier.height(4.dp))
                            Text("Tap the + button to add your first activity", fontSize = 13.sp, color = MidGray)
                        }
                    }
                }

                // Save Button
                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val allActivities = weekPlans.associate { it.date to it.activities }
                            viewModel.saveWeeklyPlan(allActivities)
                            showLoadingDialog = true
                        },
                        enabled = weekPlans.any { it.activities.isNotEmpty() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftBlue,
                            disabledContainerColor = SoftGray,
                            contentColor = White
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(54.dp)
                    ) {
                        Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Save Weekly Plan", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Dialogs
        if (showDatePicker) {
            DatePickerDialogModern(
                selectedStartDate = startDate,
                onDateSelected = { selectedStartDate = it; selectedDayIndex = 0 },
                onDismiss = { showDatePicker = false }
            )
        }

        if (showAddDialog || editActivity != null) {
            ActivityFormDialog(
                existingActivity = editActivity,
                onDismiss = { showAddDialog = false; editActivity = null },
                onAdd = { name, type, duration, materials ->
                    if (editActivity != null) {
                        val updated = selectedDay.activities.toMutableList().map {
                            if (it.id == editActivity!!.id) {
                                it.copy(activityName = name, activityType = type, duration = duration, materialsNeeded = materials)
                            } else it
                        }
                        weekPlans[selectedDayIndex] = selectedDay.copy(activities = updated)
                    } else {
                        val newActivity = DayActivity(
                            id = System.currentTimeMillis().toString(),
                            activityName = name,
                            activityType = type,
                            duration = duration,
                            materialsNeeded = materials
                        )
                        val updated = selectedDay.activities.toMutableList().apply { add(newActivity) }
                        weekPlans[selectedDayIndex] = selectedDay.copy(activities = updated)
                    }
                    showAddDialog = false
                    editActivity = null
                }
            )
        }

        if (viewDetailActivity != null) {
            ActivityDetailDialog(
                activity = viewDetailActivity!!,
                date = selectedDay.dateFormatted,
                dayName = selectedDay.dayName,
                onClose = { viewDetailActivity = null },
                onEdit = { editActivity = viewDetailActivity; viewDetailActivity = null },
                onDelete = { confirmDeleteActivity = viewDetailActivity; viewDetailActivity = null }
            )
        }

        if (confirmDeleteActivity != null) {
            DeleteConfirmDialog(
                activityName = confirmDeleteActivity!!.activityName,
                onConfirm = {
                    val updated = selectedDay.activities.toMutableList().apply {
                        removeIf { it.id == confirmDeleteActivity!!.id }
                    }
                    weekPlans[selectedDayIndex] = selectedDay.copy(activities = updated)
                    confirmDeleteActivity = null
                },
                onDismiss = { confirmDeleteActivity = null }
            )
        }

        if (showLoadingDialog) {
            LoadingOverlay()
        }

        uiState.errorMessage?.let { error ->
            ErrorDialog(message = error, onDismiss = { viewModel.resetSaveState() })
        }

        if (showSaveSuccess) {
            SuccessDialog(onDismiss = { showSaveSuccess = false })
        }
    }
}

@Composable
private fun DayChipModern(
    dayName: String,
    dayDate: String,
    isToday: Boolean,
    activityCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) SoftBlue else White,
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayName, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextGray)
            Spacer(Modifier.height(4.dp))
            Text(dayDate, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else PrimaryText)
            Spacer(Modifier.height(6.dp))
            if (activityCount > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color.White.copy(alpha = 0.25f) else SoftBlueLight
                ) {
                    Text("$activityCount", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else SoftBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            } else if (isToday && !isSelected) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(SoftBlue))
            }
        }
    }
}

@Composable
private fun DaySummaryModern(
    dayName: String,
    date: String,
    activityCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(SoftBlue50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Event, contentDescription = null, tint = SoftBlue, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text(dayName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(date, fontSize = 12.sp, color = TextGray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$activityCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SoftBlue)
                Text("Activities", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ActivityCardModern(
    activity: DayActivity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(activity.activityType.bg50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(activity.activityType.icon, contentDescription = null, tint = activity.activityType.color, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(activity.activityName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryText)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(shape = RoundedCornerShape(6.dp), color = activity.activityType.bg) {
                                Text(activity.activityType.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = activity.activityType.color, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                            Text("•", fontSize = 10.sp, color = SoftGray)
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = TextGray, modifier = Modifier.size(13.dp))
                            Text(activity.duration, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = TextGray, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = SoftRose, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (activity.materialsNeeded.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Divider(color = LightGray)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = TextGray, modifier = Modifier.size(15.dp))
                    Column {
                        Text("Materials Needed", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextGray)
                        Text(activity.materialsNeeded, fontSize = 13.sp, color = DarkText)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityDetailDialog(
    activity: DayActivity,
    date: String,
    dayName: String,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ModernDialog(onDismiss = onClose) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Activity Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text("$dayName, $date", fontSize = 12.sp, color = TextGray)
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(LightGray)
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(activity.activityType.bg50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(activity.activityType.icon, contentDescription = null, tint = activity.activityType.color, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text(activity.activityName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = activity.activityType.bg) {
                        Text(activity.activityType.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = activity.activityType.color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = LightGray)
            Spacer(Modifier.height(16.dp))

            DetailRow(icon = Icons.Outlined.Schedule, title = "Duration", value = activity.duration, iconColor = SoftBlue, iconBg = SoftBlue50)

            if (activity.materialsNeeded.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                DetailRow(icon = Icons.Outlined.Inventory2, title = "Materials Needed", value = activity.materialsNeeded, iconColor = SoftPurple, iconBg = SoftPurple50)
            }

            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftBlue, containerColor = SoftBlue50)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onDelete,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRose, contentColor = White)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconColor: Color,
    iconBg: Color
) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 14.sp, color = DarkText)
        }
    }
}

@Composable
private fun ModernDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val dialogHeight = screenHeight * 0.6f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.15f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(250, easing = FastOutSlowInEasing)) + slideInVertically(tween(250, easing = FastOutSlowInEasing)) { it / 6 },
            exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 6 }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.85f).height(dialogHeight),
                shape = RoundedCornerShape(28.dp),
                color = White,
                shadowElevation = 12.dp
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityFormDialog(
    existingActivity: DayActivity?,
    onDismiss: () -> Unit,
    onAdd: (String, ActivityType, String, String) -> Unit
) {
    var activityName by remember { mutableStateOf(existingActivity?.activityName ?: "") }
    var selectedType by remember { mutableStateOf(existingActivity?.activityType ?: ActivityType.EDUCATIONAL) }
    var selectedDuration by remember { mutableStateOf(existingActivity?.duration ?: "") }
    var materialsNeeded by remember { mutableStateOf(existingActivity?.materialsNeeded ?: "") }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showDurationDropdown by remember { mutableStateOf(false) }

    val durationOptions = listOf("30 min", "45 min", "1 hour", "1.5 hours", "2 hours")

    ModernDialog(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (existingActivity != null) "Edit Activity" else "Add Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(LightGray)
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    label = { Text("Activity Name", color = TextGray) },
                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = SoftBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftBlue, unfocusedBorderColor = SoftGray, focusedLeadingIconColor = SoftBlue, unfocusedLeadingIconColor = TextGray),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(expanded = showTypeDropdown, onExpandedChange = { showTypeDropdown = !showTypeDropdown }) {
                    OutlinedTextField(
                        value = selectedType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type", color = TextGray) },
                        leadingIcon = { Icon(selectedType.icon, contentDescription = null, tint = selectedType.color) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftBlue, unfocusedBorderColor = SoftGray, focusedLeadingIconColor = selectedType.color, unfocusedLeadingIconColor = TextGray),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }) {
                        ActivityType.values().forEach { type ->
                            DropdownMenuItem(text = { Text(type.label) }, onClick = { selectedType = type; showTypeDropdown = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = showDurationDropdown, onExpandedChange = { showDurationDropdown = !showDurationDropdown }) {
                    OutlinedTextField(
                        value = selectedDuration,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Duration", color = TextGray) },
                        leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null, tint = SoftPurple) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDurationDropdown) },
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("Select duration", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftBlue, unfocusedBorderColor = SoftGray, focusedLeadingIconColor = SoftPurple, unfocusedLeadingIconColor = TextGray),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = showDurationDropdown, onDismissRequest = { showDurationDropdown = false }) {
                        durationOptions.forEach { opt ->
                            DropdownMenuItem(text = { Text(opt) }, onClick = { selectedDuration = opt; showDurationDropdown = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = materialsNeeded,
                    onValueChange = { materialsNeeded = it },
                    label = { Text("Materials Needed", color = TextGray) },
                    leadingIcon = { Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = SoftOrange) },
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftBlue, unfocusedBorderColor = SoftGray, focusedLeadingIconColor = SoftOrange, unfocusedLeadingIconColor = TextGray),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (activityName.isNotBlank() && selectedDuration.isNotBlank()) {
                        onAdd(activityName, selectedType, selectedDuration, materialsNeeded)
                    }
                },
                enabled = activityName.isNotBlank() && selectedDuration.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftBlue, disabledContainerColor = SoftGray),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (existingActivity != null) "Update Activity" else "Add Activity", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogModern(
    selectedStartDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedStartDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.15f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(250, easing = FastOutSlowInEasing)) + slideInVertically(tween(250, easing = FastOutSlowInEasing)) { it / 6 },
            exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 6 }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.88f),
                shape = RoundedCornerShape(28.dp),
                color = White,
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Select Start Date", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("5-day week starts from your choice", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(34.dp).clip(CircleShape).background(LightGray)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(18.dp))
                        }
                    }

                    DatePicker(
                        state = datePickerState,
                        showModeToggle = true,
                        colors = DatePickerDefaults.colors(
                            containerColor = White,
                            titleContentColor = PrimaryText,
                            headlineContentColor = SoftBlue,
                            weekdayContentColor = TextGray,
                            subheadContentColor = PrimaryText,
                            navigationContentColor = TextGray,
                            yearContentColor = TextGray,
                            disabledYearContentColor = SoftGray,
                            currentYearContentColor = SoftBlue,
                            selectedYearContentColor = White,
                            disabledSelectedYearContentColor = SoftGray,
                            selectedYearContainerColor = SoftBlue,
                            disabledSelectedYearContainerColor = SoftGray,
                            dayContentColor = DarkText,
                            disabledDayContentColor = SoftGray,
                            selectedDayContainerColor = SoftBlue,
                            disabledSelectedDayContainerColor = SoftGray,
                            selectedDayContentColor = White,
                            disabledSelectedDayContentColor = SoftGray,
                            todayContentColor = SoftBlue,
                            todayDateBorderColor = SoftBlue,
                            dayInSelectionRangeContainerColor = SoftBlueLight,
                            dayInSelectionRangeContentColor = SoftBlue,
                            dividerColor = LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                onDateSelected(LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000)))
                            }
                            onDismiss()
                        },
                        enabled = datePickerState.selectedDateMillis != null,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftBlue,
                            disabledContainerColor = SoftGray,
                            contentColor = White
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).height(48.dp)
                    ) {
                        Text("Apply", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    activityName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModernDialog(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(SoftRose50), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = SoftRose, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Delete Activity?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(8.dp))
            Text("\"$activityName\" will be removed from this day.", fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray)
                ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRose, contentColor = White)
                ) { Text("Delete", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
        AnimatedVisibility(visible = true, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
            Surface(shape = RoundedCornerShape(20.dp), color = White, shadowElevation = 8.dp) {
                Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = SoftBlue)
                    Text("Saving...", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                }
            }
        }
    }
}

@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    ModernDialog(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(SoftOrange50), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Warning, contentDescription = null, tint = SoftOrange, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Error", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(8.dp))
            Text(message, fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = SoftOrange), modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("OK", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    ModernDialog(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(SoftGreen50), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SoftGreen, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Plan Saved", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(8.dp))
            Text("Your weekly activity plan has been saved successfully!", fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = SoftGreen), modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Done", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
