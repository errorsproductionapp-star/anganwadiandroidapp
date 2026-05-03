package com.example.anganwadiapp.presentation.progress

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// ── Palette ──────────────────────────────────────────────────────────────────
private val PageBg         = Color(0xFFF0F7FF)          // very light sky wash
private val CardBg         = Color(0xFFFFFFFF)
private val CardBorder     = Color(0xFFD6E8F8)
private val PrimaryBlue    = Color(0xFF1E82D0)           // sky blue
private val PrimaryMid     = Color(0xFF3B9FE8)
private val PrimaryLight   = Color(0xFFE0F2FF)
private val PrimaryXLight  = Color(0xFFF0F8FF)
private val AccentTeal     = Color(0xFF0EA5C9)
private val TextPrimary    = Color(0xFF0E2A45)
private val TextSecondary  = Color(0xFF4A7A9B)
private val TextHint       = Color(0xFF94B8D0)
private val DividerColor   = Color(0xFFE8F3FB)
private val RatedGreen     = Color(0xFF0E9F72)
private val RatedBg        = Color(0xFFE8FBF4)
private val RatedBorder    = Color(0xFFB8EDDA)
private val UnratedAmber   = Color(0xFFF59E0B)
private val UnratedBg      = Color(0xFFFFF8E8)
private val UnratedBorder  = Color(0xFFFFE5A0)
private val StarGold       = Color(0xFFFBBF24)
private val StarEmpty      = Color(0xFFDBECF9)
// Header gradient
private val HeaderStart    = Color(0xFF1E82D0)
private val HeaderEnd      = Color(0xFF0EA5C9)

@Composable
fun ProgressRatingScreen(
    viewModel: ProgressRatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedStudentId by remember { mutableStateOf<String?>(null) }
    var showRatingDialog  by remember { mutableStateOf(false) }

    val today     = uiState.todayDate
    val rated     = uiState.students.count { it.isRated }
    val unrated   = uiState.students.size - rated
    val total     = uiState.students.size
    val filtered  = if (uiState.searchQuery.isBlank()) uiState.students
    else uiState.students.filter {
        it.name.contains(uiState.searchQuery, ignoreCase = true)
    }
    val selectedStudent = selectedStudentId?.let { id ->
        uiState.students.find { it.id == id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Gradient header bar ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(HeaderStart, HeaderEnd))
                )
                .padding(horizontal = 20.dp, vertical = 15.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = today,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.3.sp
                )

                Text(
                    text = "Today's Progress Overview",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        if (uiState.isLoading && uiState.students.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Stat chips ───────────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            label    = "Total",
                            value    = "$total",
                            icon     = Icons.Default.Groups,
                            bgColor  = PrimaryLight,
                            iconColor= PrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label    = "Rated",
                            value    = "$rated",
                            icon     = Icons.Default.CheckCircle,
                            bgColor  = RatedBg,
                            iconColor= RatedGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label    = "Pending",
                            value    = "$unrated",
                            icon     = Icons.Default.HourglassEmpty,
                            bgColor  = UnratedBg,
                            iconColor= UnratedAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── Progress bar ─────────────────────────────────────────────
                item {
                    if (total > 0) RatingProgress(rated, total)
                }

                // ── Search ───────────────────────────────────────────────────
                item {
                    SearchBar(
                        query    = uiState.searchQuery,
                        onChange = { viewModel.onSearchQueryChange(it) }
                    )
                }

                // ── Section heading ──────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Students",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PrimaryLight
                        ) {
                            Text(
                                "${filtered.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // ── Student rows ─────────────────────────────────────────────
                items(filtered, key = { it.id }) { student ->
                    StudentRatingRow(
                        student = student,
                        onClick = {
                            selectedStudentId = student.id
                            showRatingDialog  = true
                        }
                    )
                }

                // ── Save button ──────────────────────────────────────────────
                item {
                    Spacer(Modifier.height(4.dp))
                    SaveButton(
                        isSaved  = uiState.isSaved,
                        allRated = uiState.allRated,
                        onClick  = { viewModel.saveAllRatings() }
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }

    if (showRatingDialog && selectedStudent != null) {
        RatingDialog(
            student        = selectedStudent!!,
            onDismiss      = { showRatingDialog = false; selectedStudentId = null },
            onRatingChange = { category, rating ->
                viewModel.updateRating(selectedStudent!!.id, category, rating)
            }
        )
    }
}

// ── Stat Card ─────────────────────────────────────────────────────────────────
@Composable
private fun StatCard(
    label    : String,
    value    : String,
    icon     : ImageVector,
    bgColor  : Color,
    iconColor: Color,
    modifier : Modifier = Modifier
) {
    Surface(
        modifier        = modifier,
        shape           = RoundedCornerShape(16.dp),
        color           = CardBg,
        shadowElevation = 2.dp,
        border          = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            // Icon bubble
            Box(
                modifier           = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment   = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                fontSize   = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = TextPrimary,
                textAlign  = TextAlign.Center
            )
            Text(
                label,
                fontSize   = 10.sp,
                color      = TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign  = TextAlign.Center
            )
        }
    }
}

// ── Rating Progress ───────────────────────────────────────────────────────────
@Composable
private fun RatingProgress(rated: Int, total: Int) {
    if (total == 0) return
    val pct       = (rated.toFloat() / total * 100).toInt()
    val ratedFrac = rated.toFloat() / total

    Surface(
        shape           = RoundedCornerShape(16.dp),
        color           = CardBg,
        shadowElevation = 2.dp,
        border          = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Rating Progress",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Surface(shape = RoundedCornerShape(20.dp), color = if (pct == 100) RatedBg else PrimaryLight) {
                    Text(
                        "$pct% complete",
                        fontSize   = 11.sp,
                        color      = if (pct == 100) RatedGreen else PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            // Progress track with rounded ends
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(DividerColor)
            ) {
                if (ratedFrac > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratedFrac)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(PrimaryBlue, AccentTeal)
                                )
                            )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$rated rated", fontSize = 11.sp, color = RatedGreen, fontWeight = FontWeight.Medium)
                Text("${total - rated} left", fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────
@Composable
private fun SearchBar(query: String, onChange: (String) -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(14.dp),
        color           = CardBg,
        shadowElevation = 1.dp,
        border          = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = PrimaryMid, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            TextField(
                value             = query,
                onValueChange     = onChange,
                placeholder       = { Text("Search students…", fontSize = 14.sp, color = TextHint) },
                singleLine        = true,
                colors            = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = TextPrimary,
                    unfocusedTextColor      = TextPrimary
                ),
                textStyle         = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                modifier          = Modifier.fillMaxWidth()
            )
        }
    }
}

// ── Student Row ───────────────────────────────────────────────────────────────
@Composable
private fun StudentRatingRow(
    student: StudentRatingItem,
    onClick: () -> Unit
) {
    val (statusBg, statusBorder, avatarBg) = if (student.isRated)
        Triple(RatedBg, RatedBorder, RatedGreen)
    else
        Triple(CardBg, CardBorder, PrimaryBlue)

    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape           = RoundedCornerShape(16.dp),
        color           = statusBg,
        border          = androidx.compose.foundation.BorderStroke(1.dp, statusBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(avatarBg.copy(alpha = 0.15f))
                    .border(2.dp, avatarBg.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    student.initials,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = avatarBg
                )
            }
            Spacer(Modifier.width(12.dp))

            // Name + age
            Column(Modifier.weight(1f)) {
                Text(
                    student.name,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Age ${student.age}",
                    fontSize   = 11.sp,
                    color      = TextHint,
                    fontWeight = FontWeight.Medium
                )
            }

            // Status badge
            if (student.isRated) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RatedGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RatedGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = RatedGreen, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Rated",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = RatedGreen
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = UnratedAmber.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UnratedAmber.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.HourglassEmpty, null, tint = UnratedAmber, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Pending",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = UnratedAmber
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint     = TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Save Button ───────────────────────────────────────────────────────────────
@Composable
private fun SaveButton(isSaved: Boolean, allRated: Boolean, onClick: () -> Unit) {
    val enabled  = allRated && !isSaved
    val bgBrush  = when {
        isSaved  -> Brush.horizontalGradient(listOf(RatedGreen, Color(0xFF0CB87A)))
        enabled  -> Brush.horizontalGradient(listOf(PrimaryBlue, AccentTeal))
        else     -> Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color(0xFFCBD5E1)))
    }
    val txtColor = if (!enabled && !isSaved) TextHint else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .clickable(
                enabled            = enabled,
                interactionSource  = remember { MutableInteractionSource() },
                indication         = null,
                onClick            = onClick
            )
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(isSaved, label = "saveBtn") { saved ->
            Row(
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    if (saved) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                    null,
                    tint     = txtColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    when {
                        saved      -> "All Ratings Saved ✓"
                        !allRated  -> "Rate all students to save"
                        else       -> "Save Ratings"
                    },
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = txtColor
                )
            }
        }
    }
}

// ── Rating Dialog ─────────────────────────────────────────────────────────────
@Composable
private fun RatingDialog(
    student       : StudentRatingItem,
    onDismiss     : () -> Unit,
    onRatingChange: (String, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardBg,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Column {
                // Student header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryLight)
                            .border(2.dp, PrimaryBlue.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            student.initials,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = PrimaryBlue
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(student.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Age ${student.age}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Thin divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DividerColor)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RatingCategoryRow("Food",                    Icons.Default.Restaurant, student.foodRating)         { onRatingChange("food", it) }
                Divider(color = DividerColor, thickness = 0.5.dp)
                RatingCategoryRow("Education",               Icons.Default.MenuBook,   student.educationRating)    { onRatingChange("education", it) }
                Divider(color = DividerColor, thickness = 0.5.dp)
                RatingCategoryRow("Activity",                Icons.Default.DirectionsRun, student.activityRating)  { onRatingChange("activity", it) }
                Divider(color = DividerColor, thickness = 0.5.dp)
                RatingCategoryRow("Health",                  Icons.Default.FavoriteBorder, student.healthRating)   { onRatingChange("health", it) }
                Divider(color = DividerColor, thickness = 0.5.dp)
                RatingCategoryRow("School Readiness",        Icons.Default.School,     student.preparednessRating) { onRatingChange("preparedness", it) }
            }
        },
        confirmButton = {
            val allFilled = student.foodRating > 0 && student.educationRating > 0 &&
                    student.activityRating > 0 && student.healthRating > 0 && student.preparednessRating > 0
            Button(
                onClick  = onDismiss,
                enabled  = allFilled,
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = PrimaryBlue,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Done", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ── Rating Category Row ───────────────────────────────────────────────────────
@Composable
private fun RatingCategoryRow(
    label        : String,
    icon         : ImageVector,
    rating       : Int,
    onRatingChange: (Int) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier            = Modifier.weight(1f)
        ) {
            Box(
                modifier         = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            }
            Text(label, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            (1..5).forEach { star ->
                StarButton(
                    filled  = star <= rating,
                    onClick = { onRatingChange(if (star == rating) 0 else star) }
                )
            }
        }
    }
}

// ── Star Button ───────────────────────────────────────────────────────────────
@Composable
private fun StarButton(filled: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue    = if (filled) 1.2f else 1f,
        animationSpec  = spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy),
        label          = "starScale"
    )
    Icon(
        if (filled) Icons.Default.Star else Icons.Default.StarBorder,
        contentDescription = null,
        tint      = if (filled) StarGold else StarEmpty,
        modifier  = Modifier
            .size(26.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    )
}