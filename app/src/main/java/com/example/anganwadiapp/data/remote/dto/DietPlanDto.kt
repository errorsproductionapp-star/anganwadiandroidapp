package com.example.anganwadiapp.data.remote.dto

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DietPlanDto(
    val date: String = "",
    val breakfastItems: List<String> = emptyList(),
    val breakfastCalories: String = "",
    val breakfastProteins: String = "",
    val breakfastCarbohydrates: String = "",
    val breakfastFats: String = "",
    val breakfastVitamins: String = "",
    val breakfastMinerals: String = "",
    val breakfastCompliance: String? = null,
    val midDayItems: List<String> = emptyList(),
    val midDayCalories: String = "",
    val midDayProteins: String = "",
    val midDayCarbohydrates: String = "",
    val midDayFats: String = "",
    val midDayVitamins: String = "",
    val midDayMinerals: String = "",
    val midDayCompliance: String? = null,
    val snackItems: List<String> = emptyList(),
    val snackCalories: String = "",
    val snackProteins: String = "",
    val snackCarbohydrates: String = "",
    val snackFats: String = "",
    val snackVitamins: String = "",
    val snackMinerals: String = "",
    val snackCompliance: String? = null,
    val timestamp: Any? = null
)
