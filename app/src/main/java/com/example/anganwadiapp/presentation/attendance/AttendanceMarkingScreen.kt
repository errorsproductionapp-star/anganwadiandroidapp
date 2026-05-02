package com.example.anganwadiapp.presentation.attendance

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// ─── Design tokens ────────────────────────────────────────────────────────────

private val PageBg        = Color(0xFFF4F6FA)
private val CardBg        = Color(0xFFFFFFFF)
private val CardBorder    = Color(0xFFEEF2F7)
private val PrimaryBlue   = Color(0xFF185FA5)
private val PrimaryLight  = Color(0xFFEBF3FF)
private val TextPrimary   = Color(0xFF0D1526)
private val TextSecondary = Color(0xFF64748B)
private val TextHint      = Color(0xFF94A3B8)
private val DividerColor  = Color(0xFFF1F5F9)
private val PresentGreen  = Color(0xFF0F6E56)
private val PresentBg     = Color(0xFFE6FAF2)
private val AbsentRed     = Color(0xFFA32D2D)
private val AbsentBg      = Color(0xFFFFECEC)
private val UnmarkedBg    = Color(0xFFF8FAFC)
private val UnmarkedBorder= Color(0xFFE2E8F0)

// ─── Data models ──────────────────────────────────────────────────────────────

enum class AttendanceStatus { PRESENT, ABSENT, UNMARKED }

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun AttendanceMarkingScreen(
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val today = LocalDate.now()
    val uiState by viewModel.uiState.collectAsState()

    var selectedWeekDay by remember { mutableIntStateOf(today.dayOfWeek.value % 7) } // 0=Sun

    // Stats
    val present  = uiState.students.count { it.status == AttendanceStatus.PRESENT }
    val absent   = uiState.students.count { it.status == AttendanceStatus.ABSENT }
    val unmarked = uiState.students.count { it.status == AttendanceStatus.UNMARKED }
    val total    = uiState.students.size

    val filtered = if (uiState.searchQuery.isBlank()) uiState.students
    else uiState.students.filter { it.name.contains(uiState.searchQuery, ignoreCase = true) }

    // Week days for strip
    val weekDays = remember(today) {
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() % 7)
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Date strip ────────────────────────────────────────────────────────
        Surface(color = CardBg, shadowElevation = 0.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            DividerColor,
                            Offset(0f, size.height),
                            Offset(size.width, size.height),
                            1.dp.toPx()
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${today.year}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(weekDays) { index, date ->
                        val isSelected = index == selectedWeekDay
                        val isToday = date == today
                        DayChip(
                            date       = date,
                            isSelected = isSelected,
                            isToday    = isToday,
                            onClick    = { selectedWeekDay = index }
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Stats row ──────────────────────────────────────────────────
                item {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatChip("Present",  present,  PresentGreen, PresentBg,  Modifier.weight(1f))
                        StatChip("Absent",   absent,   AbsentRed,    AbsentBg,   Modifier.weight(1f))
                        StatChip("Unmarked", unmarked, TextSecondary,UnmarkedBg, Modifier.weight(1f))
                    }
                }

                // ── Progress bar ────────────────────────────────────────────────
                item {
                    AttendanceProgressBar(present, absent, total)
                }

                // ── Mark all row ────────────────────────────────────────────────
                item {
                    MarkAllRow(
                        onMarkAllPresent = { viewModel.markAllPresent() },
                        onClearAll = { viewModel.clearAll() }
                    )
                }

                // ── Search ──────────────────────────────────────────────────────
                item {
                    SearchBar(
                        query    = uiState.searchQuery,
                        onChange = { viewModel.onSearchQueryChange(it) }
                    )
                }

                // ── Student list ────────────────────────────────────────────────
                item {
                    Text(
                        text       = "Students (${filtered.size})",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextSecondary
                    )
                }

                items(filtered, key = { it.id }) { student ->
                    StudentAttendanceRow(
                        student  = student,
                        onStatusChange = { newStatus ->
                            viewModel.updateStudentStatus(student.id, newStatus)
                        },
                        onAbsentReasonChange = { reason ->
                            viewModel.updateAbsentReason(student.id, reason)
                        }
                    )
                }

                // ── Save button ─────────────────────────────────────────────────
                item {
                    Spacer(Modifier.height(4.dp))
                    SaveButton(
                        isSaved   = uiState.isSaved,
                        hasUnsaved = unmarked < total,
                        onClick   = { viewModel.saveAttendance(uiState.markedByName) }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ─── Day chip ─────────────────────────────────────────────────────────────────

@Composable
private fun DayChip(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        if (isSelected) PrimaryBlue else Color.Transparent, tween(200), label = "bg"
    )
    val textColor by animateColorAsState(
        if (isSelected) Color.White else if (isToday) PrimaryBlue else TextSecondary,
        tween(200), label = "text"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(
                1.dp,
                if (isToday && !isSelected) PrimaryBlue else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            fontSize   = 10.sp,
            fontWeight = FontWeight.Medium,
            color      = textColor.copy(alpha = if (isSelected) 0.8f else 1f)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text       = date.dayOfMonth.toString(),
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = textColor
        )
    }
}

// ─── Stat chip ────────────────────────────────────────────────────────────────

@Composable
private fun StatChip(
    label: String,
    count: Int,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(label, fontSize = 10.sp, color = textColor.copy(alpha = 0.75f), fontWeight = FontWeight.Medium)
    }
}

// ─── Progress bar ─────────────────────────────────────────────────────────────

@Composable
private fun AttendanceProgressBar(present: Int, absent: Int, total: Int) {
    if (total == 0) return
    val presentFrac = present.toFloat() / total
    val absentFrac  = absent.toFloat()  / total

    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = CardBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Today's Overview", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(
                    "${if (total > 0) (present.toFloat() / total * 100).toInt() else 0}% attendance",
                    fontSize = 12.sp, color = PresentGreen, fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                if (presentFrac > 0f)
                    Box(Modifier.weight(presentFrac).fillMaxHeight().background(PresentGreen))
                if (absentFrac > 0f)
                    Box(Modifier.weight(absentFrac).fillMaxHeight().background(AbsentRed))
                val unmarkedFrac = 1f - presentFrac - absentFrac
                if (unmarkedFrac > 0f)
                    Box(Modifier.weight(unmarkedFrac).fillMaxHeight().background(UnmarkedBorder))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                LegendDot("Present", PresentGreen)
                LegendDot("Absent",  AbsentRed)
                LegendDot("—",       UnmarkedBorder)
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(color))
        Text(label, fontSize = 10.sp, color = TextHint)
    }
}

// ─── Mark all row ─────────────────────────────────────────────────────────────

@Composable
private fun MarkAllRow(onMarkAllPresent: () -> Unit, onClearAll: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Mark all present
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(PresentBg)
                .clickable(onClick = onMarkAllPresent)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = PresentGreen, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Mark All Present", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PresentGreen)
        }
        // Clear all
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(UnmarkedBg)
                .border(1.dp, UnmarkedBorder, RoundedCornerShape(10.dp))
                .clickable(onClick = onClearAll)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Refresh, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        }
    }
}

// ─── Search bar ───────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(query: String, onChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = TextHint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value         = query,
            onValueChange = onChange,
            singleLine    = true,
            textStyle     = androidx.compose.ui.text.TextStyle(
                fontSize  = 14.sp,
                color     = TextPrimary
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) Text("Search students…", fontSize = 14.sp, color = TextHint)
                    inner()
                }
            }
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onChange("") }, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Default.Close, null, tint = TextHint, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── Student attendance row ───────────────────────────────────────────────────

@Composable
private fun StudentAttendanceRow(
    student: StudentAttendance,
    onStatusChange: (AttendanceStatus) -> Unit,
    onAbsentReasonChange: (String) -> Unit
) {
    val statusBg = when (student.status) {
        AttendanceStatus.PRESENT  -> PresentBg
        AttendanceStatus.ABSENT   -> AbsentBg
        AttendanceStatus.UNMARKED -> CardBg
    }
    val statusBorder = when (student.status) {
        AttendanceStatus.PRESENT  -> PresentGreen.copy(alpha = 0.3f)
        AttendanceStatus.ABSENT   -> AbsentRed.copy(alpha = 0.3f)
        AttendanceStatus.UNMARKED -> CardBorder
    }

    val animBg by animateColorAsState(statusBg, tween(250), label = "rowBg")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(animBg)
            .border(1.dp, statusBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(student.initials, student.status)

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(student.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("ID: ${student.id}", fontSize = 11.sp, color = TextHint)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusToggle(
                    isActive  = student.status == AttendanceStatus.PRESENT,
                    activeColor  = PresentGreen,
                    label     = "P",
                    onClick   = { onStatusChange(AttendanceStatus.PRESENT) }
                )
                StatusToggle(
                    isActive  = student.status == AttendanceStatus.ABSENT,
                    activeColor  = AbsentRed,
                    label     = "A",
                    onClick   = { onStatusChange(AttendanceStatus.ABSENT) }
                )
            }
        }

        if (student.status == AttendanceStatus.ABSENT) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AbsentBg.copy(alpha = 0.5f))
                    .border(1.dp, AbsentRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, tint = AbsentRed.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value         = student.absentReason,
                    onValueChange = onAbsentReasonChange,
                    singleLine    = true,
                    textStyle     = androidx.compose.ui.text.TextStyle(
                        fontSize  = 12.sp,
                        color     = TextPrimary
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (student.absentReason.isEmpty()) Text("Enter reason (optional)", fontSize = 12.sp, color = TextHint)
                            inner()
                        }
                    }
                )
            }
        }
    }
}

// ─── Avatar circle ────────────────────────────────────────────────────────────

@Composable
private fun AvatarCircle(initials: String, status: AttendanceStatus) {
    val bg = when (status) {
        AttendanceStatus.PRESENT  -> PresentGreen
        AttendanceStatus.ABSENT   -> AbsentRed
        AttendanceStatus.UNMARKED -> Color(0xFFCBD5E1)
    }
    val animBg by animateColorAsState(bg, tween(300), label = "avatar")

    Box(
        modifier         = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(animBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = initials,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = Color.White
        )
    }
}

// ─── Status toggle button ─────────────────────────────────────────────────────

@Composable
private fun StatusToggle(
    isActive: Boolean,
    activeColor: Color,
    label: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        if (isActive) 1.1f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val bg    by animateColorAsState(if (isActive) activeColor else UnmarkedBg, tween(180), label = "bg")
    val border by animateColorAsState(if (isActive) activeColor else UnmarkedBorder, tween(180), label = "border")

    Box(
        modifier = Modifier
            .size(32.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, border, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = if (isActive) Color.White else TextHint,
            textAlign  = TextAlign.Center
        )
    }
}

// ─── Save button ──────────────────────────────────────────────────────────────

@Composable
private fun SaveButton(isSaved: Boolean, hasUnsaved: Boolean, onClick: () -> Unit) {
    val bgColor  = if (isSaved) PresentBg else PrimaryBlue
    val txtColor = if (isSaved) PresentGreen else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(
                enabled = !isSaved,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        AnimatedContent(isSaved, label = "saveBtn") { saved ->
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    if (saved) Icons.Default.CheckCircle else Icons.Default.Save,
                    null,
                    tint     = txtColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (saved) "Attendance Saved" else "Save Attendance",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = txtColor
                )
            }
        }
    }
}


// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AttendanceMarkingScreenPreview() {
    AttendanceMarkingScreen()
}
