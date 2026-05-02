package com.example.anganwadiapp.presentation.diet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTextStyle
import java.util.Locale

// ─── Design tokens (matches app light theme) ──────────────────────────────────

private val PageBg         = Color(0xFFF4F6FA)
private val CardBg         = Color(0xFFFFFFFF)
private val CardBorder     = Color(0xFFEEF2F7)
private val PrimaryBlue    = Color(0xFF185FA5)
private val PrimaryLight   = Color(0xFFEBF3FF)
private val TextPrimary    = Color(0xFF0D1526)
private val TextSecondary  = Color(0xFF64748B)
private val TextHint       = Color(0xFF94A3B8)
private val DividerColor   = Color(0xFFF1F5F9)
private val InputBg        = Color(0xFFF8FAFC)
private val InputBorder    = Color(0xFFE2E8F0)

// Meal colours
private val BreakfastColor = Color(0xFFBA7517)
private val BreakfastBg    = Color(0xFFFFF3E0)
private val MidDayColor    = Color(0xFF0F6E56)
private val MidDayBg       = Color(0xFFE6FAF2)
private val SnackColor     = Color(0xFF7F77DD)
private val SnackBg        = Color(0xFFF0EFFE)

// Compliance colours
private val MetGreen       = Color(0xFF0F6E56)
private val MetGreenBg     = Color(0xFFE6FAF2)
private val PartialAmber   = Color(0xFF854F0B)
private val PartialBg      = Color(0xFFFFF3E0)
private val NotMetRed      = Color(0xFFA32D2D)
private val NotMetBg       = Color(0xFFFFECEC)

// ─── Data models ──────────────────────────────────────────────────────────────

enum class MealType(
    val label: String,
    val icon: String,
    val color: Color,
    val bg: Color
) {
    BREAKFAST("Breakfast",    "☀", BreakfastColor, BreakfastBg),
    MID_DAY  ("Mid-day Meal", "🍱", MidDayColor,   MidDayBg),
    SNACK    ("Snack",        "🍎", SnackColor,     SnackBg)
}

enum class Compliance(val label: String, val color: Color, val bg: Color) {
    MET          ("Met",           MetGreen,    MetGreenBg),
    PARTIALLY_MET("Partially Met", PartialAmber,PartialBg),
    NOT_MET      ("Not Met",       NotMetRed,   NotMetBg)
}

data class NutrientData(
    val calories: String      = "",
    val proteins: String      = "",
    val carbohydrates: String = "",
    val fats: String          = "",
    val vitamins: String      = "",
    val minerals: String      = ""
)

data class MealEntry(
    val mealType: MealType,
    val foodItems: List<String>    = emptyList(),
    val newFoodItem: String        = "",
    val nutrients: NutrientData    = NutrientData(),
    val compliance: Compliance?    = null,
    val isExpanded: Boolean        = false
)

data class DayDietLog(
    val date: LocalDate,
    val meals: Map<MealType, MealEntry> = MealType.values().associateWith { MealEntry(it) }
)

// ─── Date window helpers ──────────────────────────────────────────────────────

private fun dateWindow(startDate: LocalDate): List<LocalDate> =
    (0..4).map { startDate.plusDays(it.toLong()) }

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun DietManagementScreen(
    viewModel: DietPlanViewModel = hiltViewModel()
) {
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val uiState by viewModel.uiState.collectAsState()

    // 5-day window always starts from today
    var startDate by remember { mutableStateOf(today) }
    val isCurrentWeek = startDate == today

    val window = dateWindow(startDate)
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val selectedDate = window[selectedDayIndex]
    val selectedDateStr = selectedDate.format(dateFormatter)

    val selectedDateMeals = uiState.dates[selectedDateStr] ?: emptyMap()

    fun getMealEntry(mealType: MealType): MealEntry {
        val mealUi = selectedDateMeals[mealType.name] ?: MealUiState()
        return MealEntry(
            mealType = mealType,
            foodItems = mealUi.foodItems,
            newFoodItem = mealUi.newFoodItem,
            nutrients = NutrientData(
                calories = mealUi.nutrients.calories,
                proteins = mealUi.nutrients.proteins,
                carbohydrates = mealUi.nutrients.carbohydrates,
                fats = mealUi.nutrients.fats,
                vitamins = mealUi.nutrients.vitamins,
                minerals = mealUi.nutrients.minerals
            ),
            compliance = mealUi.compliance?.let { Compliance.valueOf(it) },
            isExpanded = mealUi.isExpanded
        )
    }

    fun updateMeal(mealType: MealType, update: (MealEntry) -> MealEntry) {
        if (!isCurrentWeek) return
        val currentEntry = getMealEntry(mealType)
        val updatedEntry = update(currentEntry)
        viewModel.updateMeal(selectedDate, mealType.name) {
            it.copy(
                foodItems = updatedEntry.foodItems,
                newFoodItem = updatedEntry.newFoodItem,
                nutrients = NutrientUiState(
                    calories = updatedEntry.nutrients.calories,
                    proteins = updatedEntry.nutrients.proteins,
                    carbohydrates = updatedEntry.nutrients.carbohydrates,
                    fats = updatedEntry.nutrients.fats,
                    vitamins = updatedEntry.nutrients.vitamins,
                    minerals = updatedEntry.nutrients.minerals
                ),
                compliance = updatedEntry.compliance?.name,
                isExpanded = updatedEntry.isExpanded
            )
        }
    }

    fun dayHasLog(date: LocalDate): Boolean {
        val dateStr = date.format(dateFormatter)
        val meals = uiState.dates[dateStr] ?: return false
        return meals.values.any { it.foodItems.isNotEmpty() }
    }

    LaunchedEffect(selectedDate) {
        viewModel.selectDate(selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Week navigator ────────────────────────────────────────────────────
        WeekNavigator(
            startDate   = startDate,
            onPrev      = {
                if (startDate.isAfter(today)) {
                    startDate = startDate.minusDays(5)
                }
            },
            onNext      = { startDate = startDate.plusDays(5) }
        )

        // ── Day strip ─────────────────────────────────────────────────────────
        DayStrip(
            days            = window,
            selectedIdx     = selectedDayIndex,
            today           = today,
            hasLogFn        = { dayHasLog(it) },
            onDaySelected   = { selectedDayIndex = it }
        )

        if (!isCurrentWeek) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, null, tint = TextHint, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("View only — navigate to current week to edit", fontSize = 11.sp, color = TextHint)
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Date header ───────────────────────────────────────────────
                item {
                    DateHeader(selectedDate, today)
                }

                // ── Meal cards ────────────────────────────────────────────────
                items(MealType.values()) { mealType ->
                    val meal = getMealEntry(mealType)
                    MealCard(
                        meal = meal,
                        isEditable = isCurrentWeek,
                        onToggleExpand = {
                            updateMeal(mealType) { it.copy(isExpanded = !it.isExpanded) }
                        },
                        onFoodItemInput = { text ->
                            updateMeal(mealType) { it.copy(newFoodItem = text) }
                        },
                        onAddFoodItem = {
                            val text = meal.newFoodItem.trim()
                            if (text.isNotEmpty()) {
                                updateMeal(mealType) {
                                    it.copy(
                                        foodItems   = it.foodItems + text,
                                        newFoodItem = ""
                                    )
                                }
                            }
                        },
                        onRemoveFoodItem = { item ->
                            updateMeal(mealType) {
                                it.copy(foodItems = it.foodItems - item)
                            }
                        },
                        onNutrientChange = { field, value ->
                            updateMeal(mealType) { entry ->
                                entry.copy(nutrients = when (field) {
                                    "calories"      -> entry.nutrients.copy(calories      = value)
                                    "proteins"      -> entry.nutrients.copy(proteins      = value)
                                    "carbohydrates" -> entry.nutrients.copy(carbohydrates = value)
                                    "fats"          -> entry.nutrients.copy(fats          = value)
                                    "vitamins"      -> entry.nutrients.copy(vitamins      = value)
                                    "minerals"      -> entry.nutrients.copy(minerals      = value)
                                    else            -> entry.nutrients
                                })
                            }
                        },
                        onComplianceChange = { c ->
                            updateMeal(mealType) { it.copy(compliance = c) }
                        }
                    )
                }

                // ── Daily summary ─────────────────────────────────────────────
                item {
                    val dayLog = DayDietLog(
                        selectedDate,
                        MealType.values().associateWith { getMealEntry(it) }
                    )
                    DailySummaryCard(dayLog)
                }

                // ── Save button ───────────────────────────────────────────────
                item {
                    if (isCurrentWeek) {
                        SaveDayButton(
                            date = selectedDate,
                            isSaving = uiState.isSaving,
                            isSaved = uiState.saveSuccess,
                            onClick = { viewModel.saveDietPlan(selectedDate) }
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

// ─── Week navigator ───────────────────────────────────────────────────────────

@Composable
private fun WeekNavigator(
    startDate: LocalDate,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val window = dateWindow(startDate)
    val start  = window.first()
    val end    = window.last()
    val fmt    = { d: LocalDate -> "${d.dayOfMonth} ${d.month.getDisplayName(JTextStyle.SHORT, Locale.getDefault())}" }

    Surface(
        color = CardBg,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavArrow(
                icon    = Icons.Default.ChevronLeft,
                enabled = true,
                onClick = onPrev
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Date Range",
                    fontSize = 10.sp,
                    color    = TextHint,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "${fmt(start)} – ${fmt(end)}",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
            }

            NavArrow(
                icon    = Icons.Default.ChevronRight,
                enabled = true,
                onClick = onNext
            )
        }
    }
}

@Composable
private fun NavArrow(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (enabled) PrimaryLight else InputBg)
            .clickable(
                enabled           = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon, null,
            tint     = if (enabled) PrimaryBlue else TextHint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DayStrip(
    days: List<LocalDate>,
    selectedIdx: Int,
    today: LocalDate,
    hasLogFn: (LocalDate) -> Boolean,
    onDaySelected: (Int) -> Unit
) {
    Surface(
        color = CardBg,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(DividerColor, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
            }
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, date ->
                val isSelected = index == selectedIdx
                val isToday    = date == today
                val hasLog     = hasLogFn(date)
                val isFuture   = date.isAfter(today)

                DayPill(
                    date       = date,
                    isSelected = isSelected,
                    isToday    = isToday,
                    hasLog     = hasLog,
                    isFuture   = isFuture,
                    onClick    = { if (!isFuture) onDaySelected(index) }
                )
            }
        }
    }
}

@Composable
private fun DayPill(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasLog: Boolean,
    isFuture: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        if (isSelected) PrimaryBlue else Color.Transparent, tween(200), label = "dayBg"
    )
    val textColor by animateColorAsState(
        when {
            isSelected -> Color.White
            isFuture   -> TextHint.copy(alpha = 0.4f)
            isToday    -> PrimaryBlue
            else       -> TextSecondary
        }, tween(180), label = "dayText"
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
                enabled           = !isFuture,
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .widthIn(min = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            date.dayOfWeek.getDisplayName(JTextStyle.SHORT, Locale.getDefault()).take(3),
            fontSize   = 10.sp,
            fontWeight = FontWeight.Medium,
            color      = textColor.copy(alpha = if (isSelected) 0.75f else textColor.alpha)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            date.dayOfMonth.toString(),
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = textColor
        )
        Spacer(Modifier.height(3.dp))
        // Log indicator dot
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected && hasLog -> Color.White
                        hasLog              -> PrimaryBlue
                        else                -> Color.Transparent
                    }
                )
        )
    }
}

// ─── Date header ─────────────────────────────────────────────────────────────

@Composable
private fun DateHeader(date: LocalDate, today: LocalDate) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                date.dayOfWeek.getDisplayName(JTextStyle.FULL, Locale.getDefault()),
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = TextPrimary
            )
            Text(
                "${date.dayOfMonth} ${date.month.getDisplayName(JTextStyle.FULL, Locale.getDefault())} ${date.year}",
                fontSize = 13.sp,
                color    = TextSecondary
            )
        }
        if (date == today) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryLight
            ) {
                Text(
                    "Today",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = PrimaryBlue,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ─── Meal card ────────────────────────────────────────────────────────────────

@Composable
private fun MealCard(
    meal: MealEntry,
    isEditable: Boolean,
    onToggleExpand: () -> Unit,
    onFoodItemInput: (String) -> Unit,
    onAddFoodItem: () -> Unit,
    onRemoveFoodItem: (String) -> Unit,
    onNutrientChange: (String, String) -> Unit,
    onComplianceChange: (Compliance) -> Unit
) {
    val mt = meal.mealType
    val filledCount = meal.foodItems.size
    val hasData = filledCount > 0 || meal.compliance != null

    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = CardBg,
        border = BorderStroke(1.dp, if (hasData) mt.color.copy(alpha = 0.25f) else CardBorder)
    ) {
        Column(Modifier.fillMaxWidth()) {
            // ── Header row ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onToggleExpand
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colour badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(mt.bg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mt.icon, fontSize = 18.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(mt.label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        if (filledCount == 0) "Tap to log items"
                        else "$filledCount item${if (filledCount > 1) "s" else ""} logged",
                        fontSize = 12.sp,
                        color    = if (filledCount > 0) mt.color else TextHint
                    )
                }

                // Compliance badge (compact)
                meal.compliance?.let { c ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = c.bg
                    ) {
                        Text(
                            c.label,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = c.color,
                            modifier   = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                Icon(
                    if (meal.isExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    null,
                    tint     = TextHint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // ── Expanded content ─────────────────────────────────────────
            AnimatedVisibility(
                visible = meal.isExpanded,
                enter   = expandVertically(tween(280)) + fadeIn(tween(200)),
                exit    = shrinkVertically(tween(240)) + fadeOut(tween(160))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(DividerColor, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
                        }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Food items section
                    SectionTitle("Food Items", Icons.Default.Restaurant, mt.color)

                    // Existing chips
                    if (meal.foodItems.isNotEmpty()) {
                        if (isEditable) {
                            FoodItemChips(meal.foodItems, mt.color, mt.bg, onRemoveFoodItem)
                        } else {
                            ReadOnlyFoodChips(meal.foodItems, mt.color, mt.bg)
                        }
                    } else if (!isEditable) {
                        Text("No items logged", fontSize = 12.sp, color = TextHint, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    // Add food item (editable only)
                    if (isEditable) {
                        FoodItemInput(
                            value    = meal.newFoodItem,
                            onChange = onFoodItemInput,
                            onAdd    = onAddFoodItem,
                            accent   = mt.color
                        )
                    }

                    // Nutrients section
                    SectionTitle("Nutritional Info", Icons.Default.MonitorHeart, PrimaryBlue)

                    // Row 1: calories + proteins
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NutrientField("Calories", "kcal", meal.nutrients.calories,
                            { if (isEditable) onNutrientChange("calories", it) }, Modifier.weight(1f), !isEditable)
                        NutrientField("Proteins", "g", meal.nutrients.proteins,
                            { if (isEditable) onNutrientChange("proteins", it) }, Modifier.weight(1f), !isEditable)
                    }
                    // Row 2: carbs + fats
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NutrientField("Carbohydrates", "g", meal.nutrients.carbohydrates,
                            { if (isEditable) onNutrientChange("carbohydrates", it) }, Modifier.weight(1f), !isEditable)
                        NutrientField("Fats", "g", meal.nutrients.fats,
                            { if (isEditable) onNutrientChange("fats", it) }, Modifier.weight(1f), !isEditable)
                    }
                    // Row 3: vitamins + minerals (text)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NutrientTextField("Vitamins", meal.nutrients.vitamins,
                            { if (isEditable) onNutrientChange("vitamins", it) }, Modifier.weight(1f), !isEditable)
                        NutrientTextField("Minerals", meal.nutrients.minerals,
                            { if (isEditable) onNutrientChange("minerals", it) }, Modifier.weight(1f), !isEditable)
                    }

                    // Compliance section (editable only)
                    if (isEditable) {
                        SectionTitle("Guideline Compliance", Icons.Default.FactCheck, MetGreen)

                        ComplianceSelector(
                            selected = meal.compliance,
                            onSelect = onComplianceChange
                        )
                    } else if (meal.compliance != null) {
                        SectionTitle("Compliance", Icons.Default.FactCheck, MetGreen)
                        Surface(shape = RoundedCornerShape(8.dp), color = meal.compliance.bg) {
                            Text(
                                meal.compliance.label,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = meal.compliance.color,
                                modifier   = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Section title ────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String, icon: ImageVector, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Text(
            text.uppercase(),
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = color,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun ReadOnlyFoodChips(items: List<String>, accentColor: Color, accentBg: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.chunked(3).forEach { rowChunk ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowChunk.forEach { item ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(accentBg)
                            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            item,
                            fontSize   = 12.sp,
                            color      = accentColor,
                            fontWeight = FontWeight.Medium,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.widthIn(max = 100.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Food item chips ──────────────────────────────────────────────────────────

@Composable
private fun FoodItemChips(
    items: List<String>,
    accentColor: Color,
    accentBg: Color,
    onRemove: (String) -> Unit
) {
    // Simple wrapping flow using FlowRow-style via LazyRow
    var rowItems = items.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rowItems.forEach { rowChunk ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowChunk.forEach { item ->
                    FoodChip(item, accentColor, accentBg, onRemove)
                }
            }
        }
    }
}

@Composable
private fun FoodChip(
    text: String,
    accentColor: Color,
    accentBg: Color,
    onRemove: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accentBg)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text,
            fontSize   = 12.sp,
            color      = accentColor,
            fontWeight = FontWeight.Medium,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis,
            modifier   = Modifier.widthIn(max = 100.dp)
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = { onRemove(text) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, null, tint = accentColor, modifier = Modifier.size(10.dp))
        }
    }
}

// ─── Food item input ──────────────────────────────────────────────────────────

@Composable
private fun FoodItemInput(
    value: String,
    onChange: (String) -> Unit,
    onAdd: () -> Unit,
    accent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(InputBg)
            .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value         = value,
            onValueChange = onChange,
            singleLine    = true,
            cursorBrush   = SolidColor(accent),
            textStyle     = TextStyle(fontSize = 13.sp, color = TextPrimary),
            modifier      = Modifier.weight(1f),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text("Add food item…", fontSize = 13.sp, color = TextHint)
                    inner()
                }
            }
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (value.isNotBlank()) accent else InputBorder)
                .clickable(
                    enabled           = value.isNotBlank(),
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onAdd
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun NutrientField(
    label: String,
    unit: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Column(modifier) {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (readOnly) InputBg.copy(alpha = 0.7f) else InputBg)
                .border(1.dp, if (readOnly) InputBorder.copy(alpha = 0.5f) else InputBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (readOnly) {
                Text(
                    if (value.isEmpty()) "—" else value,
                    fontSize = 14.sp,
                    color = if (value.isEmpty()) TextHint else TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            } else {
                BasicTextField(
                    value         = value,
                    onValueChange = onChange,
                    singleLine    = true,
                    cursorBrush   = SolidColor(PrimaryBlue),
                    textStyle     = TextStyle(fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier      = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (value.isEmpty()) Text("0", fontSize = 14.sp, color = TextHint)
                            inner()
                        }
                    }
                )
            }
            Text(unit, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun NutrientTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Column(modifier) {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
        if (readOnly) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBg.copy(alpha = 0.7f))
                    .border(1.dp, InputBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    if (value.isEmpty()) "—" else value,
                    fontSize = 13.sp,
                    color = if (value.isEmpty()) TextHint else TextPrimary
                )
            }
        } else {
            BasicTextField(
                value         = value,
                onValueChange = onChange,
                singleLine    = false,
                maxLines      = 2,
                cursorBrush   = SolidColor(PrimaryBlue),
                textStyle     = TextStyle(fontSize = 13.sp, color = TextPrimary),
                modifier      = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBg)
                    .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                decorationBox = { inner ->
                    Box {
                        if (value.isEmpty()) Text("e.g. A, B12, C…", fontSize = 13.sp, color = TextHint)
                        inner()
                    }
                }
            )
        }
    }
}

// ─── Compliance selector ──────────────────────────────────────────────────────

@Composable
private fun ComplianceSelector(
    selected: Compliance?,
    onSelect: (Compliance) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Compliance.values().forEach { c ->
            val isActive = selected == c
            val scale by animateFloatAsState(
                if (isActive) 1.03f else 1f,
                spring(Spring.DampingRatioMediumBouncy),
                label = "compScale"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) c.bg else InputBg)
                    .border(
                        1.dp,
                        if (isActive) c.color else InputBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = { onSelect(c) }
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    c.label,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isActive) c.color else TextHint,
                    textAlign  = TextAlign.Center,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

// ─── Daily summary card ───────────────────────────────────────────────────────

@Composable
private fun DailySummaryCard(log: DayDietLog) {
    val totalCal = log.meals.values.sumOf { m ->
        m.nutrients.calories.toDoubleOrNull() ?: 0.0
    }
    val totalPro = log.meals.values.sumOf { m ->
        m.nutrients.proteins.toDoubleOrNull() ?: 0.0
    }
    val totalCarb = log.meals.values.sumOf { m ->
        m.nutrients.carbohydrates.toDoubleOrNull() ?: 0.0
    }
    val totalFat = log.meals.values.sumOf { m ->
        m.nutrients.fats.toDoubleOrNull() ?: 0.0
    }
    val allCompliance = log.meals.values.mapNotNull { it.compliance }

    if (totalCal == 0.0 && allCompliance.isEmpty()) return

    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = PrimaryLight,
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Daily Summary",
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = PrimaryBlue
            )
            // Macros grid
            if (totalCal > 0) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroChip("Calories", "${totalCal.toInt()} kcal", PrimaryBlue, Modifier.weight(1f))
                    MacroChip("Proteins", "${totalPro.toInt()} g",    MidDayColor, Modifier.weight(1f))
                    MacroChip("Carbs",    "${totalCarb.toInt()} g",   BreakfastColor, Modifier.weight(1f))
                    MacroChip("Fats",     "${totalFat.toInt()} g",    SnackColor,  Modifier.weight(1f))
                }
            }
            // Compliance overview
            if (allCompliance.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Compliance:", fontSize = 12.sp, color = TextSecondary)
                    allCompliance.forEach { c ->
                        Surface(shape = RoundedCornerShape(6.dp), color = c.bg) {
                            Text(
                                c.label,
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = c.color,
                                modifier   = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBg)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
        Text(label,  fontSize = 9.sp, color = TextHint, textAlign = TextAlign.Center)
    }
}

// ─── Save button ──────────────────────────────────────────────────────────────

@Composable
private fun SaveDayButton(date: LocalDate, isSaving: Boolean, isSaved: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSaved) MetGreenBg else PrimaryBlue)
            .border(1.dp, if (isSaved) MetGreen.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable(
                enabled           = !isSaving && !isSaved,
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            AnimatedContent(isSaved, label = "saveBtn") { saved ->
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        if (saved) Icons.Default.CheckCircle else Icons.Default.Save,
                        null,
                        tint     = if (saved) MetGreen else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (saved) "Diet Log Saved!" else "Save Diet Log",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = if (saved) MetGreen else Color.White
                    )
                }
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DietManagementScreenPreview() {
    DietManagementScreen()
}