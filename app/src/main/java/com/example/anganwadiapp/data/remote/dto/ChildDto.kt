package com.example.anganwadiapp.data.remote.dto

import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChildDto(
    val id: String = "",
    val name: String = "",
    val dateOfBirth: Long = 0L,
    val gender: String = "OTHER",
    val weight: Float? = null,
    val height: Float? = null,
    val motherName: String = "",
    val fatherName: String = "",
    val registrationDate: Long = 0L,
    val anganwadiCenterId: String = "",
    val photoUrl: String? = null
)

fun ChildDto.toDomain(): Child {
    return Child(
        id = id,
        name = name,
        dateOfBirth = dateOfBirth,
        gender = try {
            Gender.valueOf(gender.uppercase())
        } catch (e: Exception) {
            Gender.OTHER
        },
        weight = weight,
        height = height,
        motherName = motherName,
        fatherName = fatherName,
        registrationDate = registrationDate
    )
}

fun Child.toDto(): ChildDto {
    return ChildDto(
        id = id,
        name = name,
        dateOfBirth = dateOfBirth,
        gender = gender.name,
        weight = weight,
        height = height,
        motherName = motherName,
        fatherName = fatherName,
        registrationDate = registrationDate
    )
}
