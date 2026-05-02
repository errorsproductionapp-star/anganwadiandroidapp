package com.example.anganwadiapp.domain.model

data class Child(
    val id: String,
    val name: String,
    val dateOfBirth: Long,
    val gender: Gender,
    val weight: Float? = null,
    val height: Float? = null,
    val motherName: String,
    val fatherName: String,
    val registrationDate: Long
)

enum class Gender {
    MALE, FEMALE, OTHER
}
