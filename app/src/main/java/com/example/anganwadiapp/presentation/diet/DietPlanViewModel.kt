package com.example.anganwadiapp.presentation.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.core.util.ToastHelper
import com.example.anganwadiapp.domain.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DietPlanViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val auth: FirebaseAuth,
    private val toastHelper: ToastHelper
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    private val _uiState = MutableStateFlow(DietPlanUiState())
    val uiState: StateFlow<DietPlanUiState> = _uiState.asStateFlow()

    init {
        loadCenterId()
    }

    private fun loadCenterId() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val centerId = appRepository.getAnganwadiCenterId(uid)
            if (centerId != null) {
                _uiState.update { it.copy(anganwadiCenterId = centerId, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Could not find your center") }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val dateStr = date.format(dateFormatter)
        _uiState.update {
            it.copy(selectedDate = dateStr, isExpanded = it.dates[dateStr]?.let { true } ?: false)
        }
        loadDietPlanForDate(date)
    }

    private fun loadDietPlanForDate(date: LocalDate) {
        val state = _uiState.value
        val centerId = state.anganwadiCenterId
        if (centerId.isEmpty()) return

        val dateStr = date.format(dateFormatter)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = appRepository.getDietPlan(centerId, dateStr)) {
                is Result.Success -> {
                    val data = result.data
                    val existingMeals = mutableMapOf<String, MealUiState>()
                    if (data != null) {
                        existingMeals["BREAKFAST"] = MealUiState(
                            foodItems = data["breakfastItems"] as? List<String> ?: emptyList(),
                            nutrients = NutrientUiState(
                                calories = data["breakfastCalories"] as? String ?: "",
                                proteins = data["breakfastProteins"] as? String ?: "",
                                carbohydrates = data["breakfastCarbohydrates"] as? String ?: "",
                                fats = data["breakfastFats"] as? String ?: "",
                                vitamins = data["breakfastVitamins"] as? String ?: "",
                                minerals = data["breakfastMinerals"] as? String ?: ""
                            ),
                            compliance = data["breakfastCompliance"] as? String
                        )
                        existingMeals["MID_DAY"] = MealUiState(
                            foodItems = data["midDayItems"] as? List<String> ?: emptyList(),
                            nutrients = NutrientUiState(
                                calories = data["midDayCalories"] as? String ?: "",
                                proteins = data["midDayProteins"] as? String ?: "",
                                carbohydrates = data["midDayCarbohydrates"] as? String ?: "",
                                fats = data["midDayFats"] as? String ?: "",
                                vitamins = data["midDayVitamins"] as? String ?: "",
                                minerals = data["midDayMinerals"] as? String ?: ""
                            ),
                            compliance = data["midDayCompliance"] as? String
                        )
                        existingMeals["SNACK"] = MealUiState(
                            foodItems = data["snackItems"] as? List<String> ?: emptyList(),
                            nutrients = NutrientUiState(
                                calories = data["snackCalories"] as? String ?: "",
                                proteins = data["snackProteins"] as? String ?: "",
                                carbohydrates = data["snackCarbohydrates"] as? String ?: "",
                                fats = data["snackFats"] as? String ?: "",
                                vitamins = data["snackVitamins"] as? String ?: "",
                                minerals = data["snackMinerals"] as? String ?: ""
                            ),
                            compliance = data["snackCompliance"] as? String
                        )
                    }
                    val newDates = state.dates.toMutableMap()
                    newDates[dateStr] = existingMeals
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dates = newDates,
                            isExpanded = data != null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun updateMeal(
        date: LocalDate,
        mealKey: String,
        update: (MealUiState) -> MealUiState
    ) {
        val dateStr = date.format(dateFormatter)
        _uiState.update { state ->
            val dates = state.dates.toMutableMap()
            val meals = dates[dateStr]?.toMutableMap() ?: mutableMapOf()
            val currentMeal = meals[mealKey] ?: MealUiState()
            meals[mealKey] = update(currentMeal)
            dates[dateStr] = meals
            state.copy(dates = dates, saveSuccess = false)
        }
    }

    fun saveDietPlan(date: LocalDate) {
        val state = _uiState.value
        val centerId = state.anganwadiCenterId
        if (centerId.isEmpty()) return

        val dateStr = date.format(dateFormatter)
        val meals = state.dates[dateStr] ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val breakfast = meals["BREAKFAST"] ?: MealUiState()
            val midDay = meals["MID_DAY"] ?: MealUiState()
            val snack = meals["SNACK"] ?: MealUiState()

            val dietPlanMap = mapOf(
                "date" to dateStr,
                "breakfastItems" to breakfast.foodItems,
                "breakfastCalories" to breakfast.nutrients.calories,
                "breakfastProteins" to breakfast.nutrients.proteins,
                "breakfastCarbohydrates" to breakfast.nutrients.carbohydrates,
                "breakfastFats" to breakfast.nutrients.fats,
                "breakfastVitamins" to breakfast.nutrients.vitamins,
                "breakfastMinerals" to breakfast.nutrients.minerals,
                "breakfastCompliance" to (breakfast.compliance ?: ""),
                "midDayItems" to midDay.foodItems,
                "midDayCalories" to midDay.nutrients.calories,
                "midDayProteins" to midDay.nutrients.proteins,
                "midDayCarbohydrates" to midDay.nutrients.carbohydrates,
                "midDayFats" to midDay.nutrients.fats,
                "midDayVitamins" to midDay.nutrients.vitamins,
                "midDayMinerals" to midDay.nutrients.minerals,
                "midDayCompliance" to (midDay.compliance ?: ""),
                "snackItems" to snack.foodItems,
                "snackCalories" to snack.nutrients.calories,
                "snackProteins" to snack.nutrients.proteins,
                "snackCarbohydrates" to snack.nutrients.carbohydrates,
                "snackFats" to snack.nutrients.fats,
                "snackVitamins" to snack.nutrients.vitamins,
                "snackMinerals" to snack.nutrients.minerals,
                "snackCompliance" to (snack.compliance ?: "")
            )

            when (val result = appRepository.saveDietPlan(centerId, dateStr, dietPlanMap)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    toastHelper.showToast("Diet log saved successfully")
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                    toastHelper.showToast("Failed to save diet log")
                }
                Result.Loading -> {
                    _uiState.update { it.copy(isSaving = true) }
                }
            }
        }
    }
}

data class DietPlanUiState(
    val anganwadiCenterId: String = "",
    val selectedDate: String = "",
    val dates: Map<String, Map<String, MealUiState>> = emptyMap(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isExpanded: Boolean = false,
    val error: String? = null
)

data class MealUiState(
    val foodItems: List<String> = emptyList(),
    val newFoodItem: String = "",
    val nutrients: NutrientUiState = NutrientUiState(),
    val compliance: String? = null,
    val isExpanded: Boolean = false
)

data class NutrientUiState(
    val calories: String = "",
    val proteins: String = "",
    val carbohydrates: String = "",
    val fats: String = "",
    val vitamins: String = "",
    val minerals: String = ""
)
