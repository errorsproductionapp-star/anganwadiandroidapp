package com.example.anganwadiapp.domain.model

data class Child(
    val id: String = "",
    val name: String = "",
    val dateOfBirth: String = "",
    val admissionDate: String = "",
    val age: String = "",
    val gender: Gender = Gender.OTHER,
    val fatherName: String = "",
    val motherName: String = "",
    val fatherMobile: String = "",
    val motherMobile: String = "",
    val placeOfBirth: String = "",
    val bloodGroup: String = "",
    val physicallyChallenged: Boolean = false,
    val height: Float? = null,
    val weight: Float? = null,
    val allergies: String = "",
    val healthNotes: String = "",
    val anganwadiCenterId: String = "",
    val photoUrl: String? = null
)

enum class Gender {
    MALE, FEMALE, OTHER
}
