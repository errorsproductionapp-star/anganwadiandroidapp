package com.example.anganwadiapp.presentation.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
private val Purple400 = Color(0xFFC084FC)
private val Purple50 = Color(0xFFFAF5FF)
private val Purple100 = Color(0xFFF3E8FF)
private val Rose400 = Color(0xFFFB7185)
private val Rose50 = Color(0xFFFFF1F2)
private val Rose100 = Color(0xFFFFE4E6)

data class ProgressCategory(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val bg: Color,
    val rating: Float,
    val maxRating: Float
)

data class StudentProgress(
    val id: String,
    val name: String,
    val initials: String,
    val age: String,
    val overallScore: Float,
    val categories: List<ProgressCategory>
)

enum class RatingLevel(val label: String, val color: Color, val bg: Color, val range: String) {
    EXCELLENT("Excellent", Green600, Green100, "4.0 - 5.0"),
    GOOD("Good", Sky600, Sky100, "3.0 - 3.9"),
    AVERAGE("Average", Orange400, Orange100, "2.0 - 2.9"),
    NEEDS_SUPPORT("Needs Support", Rose400, Rose100, "0.0 - 1.9")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressRatingScreen(
    onBack: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf<RatingLevel?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf<StudentProgress?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }

    val students = remember {
        listOf(
            StudentProgress("1", "Priya Sharma", "PS", "4y 2m", 4.2f, listOf(
                ProgressCategory("Language", Icons.Default.RecordVoiceOver, Sky600, Sky100, 4.5f, 5f),
                ProgressCategory("Numeracy", Icons.Default.Calculate, Green600, Green100, 4.0f, 5f),
                ProgressCategory("Motor Skills", Icons.Default.Accessibility, Purple400, Purple100, 4.8f, 5f),
                ProgressCategory("Social", Icons.Default.Groups, Orange400, Orange100, 3.5f, 5f)
            )),
            StudentProgress("2", "Rahul Kumar", "RK", "5y 1m", 3.6f, listOf(
                ProgressCategory("Language", Icons.Default.RecordVoiceOver, Sky600, Sky100, 3.8f, 5f),
                ProgressCategory("Numeracy", Icons.Default.Calculate, Green600, Green100, 3.2f, 5f),
                ProgressCategory("Motor Skills", Icons.Default.Accessibility, Purple400, Purple100, 4.0f, 5f),
                ProgressCategory("Social", Icons.Default.Groups, Orange400, Orange100, 3.4f, 5f)
            )),
            StudentProgress("3", "Anita Devi", "AD", "3y 8m", 2.8f, listOf(
                ProgressCategory("Language", Icons.Default.RecordVoiceOver, Sky600, Sky100, 3.0f, 5f),
                ProgressCategory("Numeracy", Icons.Default.Calculate, Green600, Green100, 2.5f, 5f),
                ProgressCategory("Motor Skills", Icons.Default.Accessibility, Purple400, Purple100, 3.2f, 5f),
                ProgressCategory("Social", Icons.Default.Groups, Orange400, Orange100, 2.5f, 5f)
            )),
            StudentProgress("4", "Arjun Patel", "AP", "4y 6m", 4.7f, listOf(
                ProgressCategory("Language", Icons.Default.RecordVoiceOver, Sky600, Sky100, 4.8f, 5f),
                ProgressCategory("Numeracy", Icons.Default.Calculate, Green600, Green100, 4.5f, 5f),
                ProgressCategory("Motor Skills", Icons.Default.Accessibility, Purple400, Purple100, 5.0f, 5f),
                ProgressCategory("Social", Icons.Default.Groups, Orange400, Orange100, 4.5f, 5f)
            )),
            StudentProgress("5", "Meena Raj", "MR", "5y 3m", 1.9f, listOf(
                ProgressCategory("Language", Icons.Default.RecordVoiceOver, Sky600, Sky100, 2.0f, 5f),
                ProgressCategory("Numeracy", Icons.Default.Calculate, Green600, Green100, 1.8f, 5f),
                ProgressCategory("Motor Skills", Icons.Default.Accessibility, Purple400, Purple100, 2.2f, 5f),
                ProgressCategory("Social", Icons.Default.Groups, Orange400, Orange100, 1.5f, 5f)
            )),
            StudentProgress("6", "Kavitha M", "KM", "4y 0m", 3.3f, listOf(
                ProgressCategory("Language", Icons.Default.RecordVoiceOver, Sky600, Sky100, 3.5f, 5f),
                ProgressCategory("Numeracy", Icons.Default.Calculate, Green600, Green100, 3.0f, 5f),
                ProgressCategory("Motor Skills", Icons.Default.Accessibility, Purple400, Purple100, 3.8f, 5f),
                ProgressCategory("Social", Icons.Default.Groups, Orange400, Orange100, 3.0f, 5f)
            ))
        )
    }

    val filteredStudents = students.filter { s ->
        (selectedFilter == null || getRatingLevelForScore(s.overallScore) == selectedFilter) &&
        (searchQuery.isBlank() || s.name.contains(searchQuery, ignoreCase = true))
    }

    val distribution = listOf(
        RatingLevel.EXCELLENT to students.count { getRatingLevelForScore(it.overallScore) == RatingLevel.EXCELLENT },
        RatingLevel.GOOD to students.count { getRatingLevelForScore(it.overallScore) == RatingLevel.GOOD },
        RatingLevel.AVERAGE to students.count { getRatingLevelForScore(it.overallScore) == RatingLevel.AVERAGE },
        RatingLevel.NEEDS_SUPPORT to students.count { getRatingLevelForScore(it.overallScore) == RatingLevel.NEEDS_SUPPORT }
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
                        colors = listOf(Purple400, Color(0xFF9333EA))
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
                    Text("Progress Rating", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${students.size} students tracked", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                }
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.width(12.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search students...", color = Gray400) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gray400) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple400,
                        unfocusedBorderColor = Gray200
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Rating Distribution Chart
            item {
                RatingDistributionCard(distribution = distribution)
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        label = "All",
                        isSelected = selectedFilter == null,
                        color = Gray500,
                        bg = Gray100,
                        onClick = { selectedFilter = null }
                    )
                    RatingLevel.values().forEach { level ->
                        FilterChip(
                            label = level.label,
                            isSelected = selectedFilter == level,
                            color = level.color,
                            bg = level.bg,
                            onClick = { selectedFilter = if (selectedFilter == level) null else level }
                        )
                    }
                }
            }

            // Student List
            items(filteredStudents) { student ->
                StudentProgressCard(
                    student = student,
                    onClick = { selectedStudent = student },
                    onRate = { showRatingDialog = true }
                )
                Spacer(Modifier.height(8.dp))
            }

            // No Results
            item {
                if (filteredStudents.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = Gray300, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No students found", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Gray500)
                    }
                }
            }
        }
    }

    if (selectedStudent != null) {
        StudentDetailDialog(
            student = selectedStudent!!,
            onDismiss = { selectedStudent = null }
        )
    }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate Student", color = Gray900) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select rating for each development area:", fontSize = 13.sp, color = Gray500)
                    listOf("Language & Communication", "Numeracy Skills", "Motor Skills", "Social Development").forEach { area ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(area, fontSize = 13.sp, color = Gray700)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..5).forEach { star ->
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= 3) Orange400 else Gray200,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showRatingDialog = false }, shape = RoundedCornerShape(12.dp)) {
                    Text("Save Rating")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RatingDistributionCard(
    distribution: List<Pair<RatingLevel, Int>>
) {
    val total = distribution.sumOf { it.second }.toFloat()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Rating Distribution", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900)
            Spacer(Modifier.height(14.dp))

            // Circular progress overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val avgScore = distribution.fold(0f) { acc, (level, count) ->
                        acc + when (level) {
                            RatingLevel.EXCELLENT -> 4.5f * count
                            RatingLevel.GOOD -> 3.5f * count
                            RatingLevel.AVERAGE -> 2.5f * count
                            RatingLevel.NEEDS_SUPPORT -> 1.0f * count
                        }
                    } / if (total > 0) total else 1f

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2 - 8.dp.toPx()
                        drawArc(
                            color = Gray100,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        val sweep = (avgScore / 5f) * 360f
                        drawArc(
                            color = Purple400,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(String.format("%.1f", avgScore), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        Text("avg", fontSize = 10.sp, color = Gray500)
                    }
                }

                Spacer(Modifier.width(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    distribution.forEach { (level, count) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(level.color)
                            )
                            Text(level.label, fontSize = 12.sp, color = Gray700, modifier = Modifier.width(90.dp))
                            Text("$count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    bg: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) bg else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else Gray200),
        onClick = onClick
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) color else Gray500,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun StudentProgressCard(
    student: StudentProgress,
    onClick: () -> Unit,
    onRate: () -> Unit
) {
    val level = getRatingLevelForScore(student.overallScore)

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
                    .background(level.bg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    student.initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = level.color
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Age: ${student.age}", fontSize = 11.sp, color = Gray500)
                    Surface(shape = RoundedCornerShape(6.dp), color = level.bg) {
                        Text(
                            level.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = level.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    String.format("%.1f", student.overallScore),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = level.color
                )
                Row {
                    (1..5).forEach { star ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= student.overallScore.toInt()) level.color else Gray200,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onRate, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Gray400, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun StudentDetailDialog(
    student: StudentProgress,
    onDismiss: () -> Unit
) {
    val level = getRatingLevelForScore(student.overallScore)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Student Progress", color = Gray900) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(level.bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(student.initials, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = level.color)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(student.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        Text("Age: ${student.age}", fontSize = 12.sp, color = Gray500)
                    }
                }

                Spacer(Modifier.height(16.dp))

                student.categories.forEach { category ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(category.bg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(14.dp))
                                }
                                Text(category.name, fontSize = 13.sp, color = Gray700)
                            }
                            Text(String.format("%.1f/5.0", category.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = category.color)
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = category.rating / category.maxRating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = category.color,
                            trackColor = Gray100
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Purple400)) {
                Text("Close")
            }
        }
    )
}

private fun getRatingLevelForScore(score: Float): RatingLevel {
    return when {
        score >= 4.0f -> RatingLevel.EXCELLENT
        score >= 3.0f -> RatingLevel.GOOD
        score >= 2.0f -> RatingLevel.AVERAGE
        else -> RatingLevel.NEEDS_SUPPORT
    }
}
