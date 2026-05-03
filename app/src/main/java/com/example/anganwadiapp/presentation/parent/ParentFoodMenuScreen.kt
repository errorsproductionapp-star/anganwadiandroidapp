package com.example.anganwadiapp.presentation.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.presentation.auth.parent.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentFoodMenuScreen(
    childId: String,
    centerId: String,
    onBack: () -> Unit,
    viewModel: ParentViewModel = hiltViewModel()
) {
    val dietPlanState by viewModel.dietPlanState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val selectedDateString = dateFormatter.format(Date(selectedDateMillis))
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDateMillis) {
        viewModel.fetchDietPlan(centerId, selectedDateString)
    }

    Scaffold(
        containerColor = ParentBgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Food Menu", fontWeight = FontWeight.Bold, color = ParentTextDark) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetDietPlanState()
                        onBack()
                    }) {
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, null, tint = ParentPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(selectedDateString, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ParentTextDark, modifier = Modifier.weight(1f))
                    Button(onClick = { showDatePicker = true }, colors = ButtonDefaults.buttonColors(containerColor = ParentPrimary), modifier = Modifier.height(36.dp)) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Date", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (dietPlanState) {
                is ParentDietPlanState.Idle,
                is ParentDietPlanState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ParentPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading food menu...", color = ParentTextGray, fontSize = 14.sp)
                        }
                    }
                }
                is ParentDietPlanState.Error -> {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Error", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text((dietPlanState as ParentDietPlanState.Error).message, textAlign = TextAlign.Center, color = ParentTextDark, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchDietPlan(centerId, selectedDateString) }, colors = ButtonDefaults.buttonColors(containerColor = ParentPrimary)) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is ParentDietPlanState.NoData -> {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, null, tint = ParentTextGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No food menu available", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No diet plan has been set for $selectedDateString", textAlign = TextAlign.Center, color = ParentTextGray, fontSize = 14.sp)
                        }
                    }
                }
                is ParentDietPlanState.Loaded -> {
                    val dietPlan = (dietPlanState as ParentDietPlanState.Loaded).dietPlan
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            MealSectionCard(
                                title = "Breakfast",
                                icon = Icons.Default.FreeBreakfast,
                                items = dietPlan["breakfastItems"] as? List<*> ?: emptyList<String>(),
                                calories = dietPlan["breakfastCalories"] as? String ?: "",
                                proteins = dietPlan["breakfastProteins"] as? String ?: "",
                                carbs = dietPlan["breakfastCarbohydrates"] as? String ?: "",
                                fats = dietPlan["breakfastFats"] as? String ?: "",
                                vitamins = dietPlan["breakfastVitamins"] as? String ?: "",
                                minerals = dietPlan["breakfastMinerals"] as? String ?: ""
                            )
                        }
                        item {
                            MealSectionCard(
                                title = "Mid-Day Meal",
                                icon = Icons.Default.RestaurantMenu,
                                items = dietPlan["midDayItems"] as? List<*> ?: emptyList<String>(),
                                calories = dietPlan["midDayCalories"] as? String ?: "",
                                proteins = dietPlan["midDayProteins"] as? String ?: "",
                                carbs = dietPlan["midDayCarbohydrates"] as? String ?: "",
                                fats = dietPlan["midDayFats"] as? String ?: "",
                                vitamins = dietPlan["midDayVitamins"] as? String ?: "",
                                minerals = dietPlan["midDayMinerals"] as? String ?: ""
                            )
                        }
                        item {
                            MealSectionCard(
                                title = "Snacks",
                                icon = Icons.Default.LocalDining,
                                items = dietPlan["snackItems"] as? List<*> ?: emptyList<String>(),
                                calories = dietPlan["snackCalories"] as? String ?: "",
                                proteins = dietPlan["snackProteins"] as? String ?: "",
                                carbs = dietPlan["snackCarbohydrates"] as? String ?: "",
                                fats = dietPlan["snackFats"] as? String ?: "",
                                vitamins = dietPlan["snackVitamins"] as? String ?: "",
                                minerals = dietPlan["snackMinerals"] as? String ?: ""
                            )
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis -> selectedDateMillis = millis }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun MealSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<*>,
    calories: String,
    proteins: String,
    carbs: String,
    fats: String,
    vitamins: String,
    minerals: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(ParentPrimary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = ParentPrimary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ParentTextDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isNotEmpty()) {
                Text("Menu Items", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ParentTextGray)
                Spacer(modifier = Modifier.height(8.dp))
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.ChevronRight, null, tint = ParentPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item.toString(), fontSize = 15.sp, color = ParentTextDark)
                    }
                }
            } else {
                Text("No items listed", fontSize = 14.sp, color = ParentTextGray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = ParentBgLight, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Nutrition Info", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ParentTextGray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NutritionBadge("Calories", calories, Color(0xFFFF9800), modifier = Modifier.weight(1f))
                NutritionBadge("Protein", proteins, Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                NutritionBadge("Carbs", carbs, Color(0xFF2196F3), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NutritionBadge("Fats", fats, Color(0xFF9C27B0), modifier = Modifier.weight(1f))
                NutritionBadge("Vitamins", vitamins, Color(0xFF00BCD4), modifier = Modifier.weight(1f))
                NutritionBadge("Minerals", minerals, Color(0xFF795548), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun NutritionBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
            if (value.isNotEmpty()) {
                Text(value, fontSize = 13.sp, color = ParentTextDark, fontWeight = FontWeight.Bold)
            } else {
                Text("-", fontSize = 13.sp, color = ParentTextGray)
            }
        }
    }
}
