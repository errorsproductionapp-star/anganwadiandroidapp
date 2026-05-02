package com.example.anganwadiapp.data.remote.dto

import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChildDto(
    val id: String = "",
    val name: String = "",
    val dateOfBirth: String = "",
    val admissionDate: String = "",
    val age: String = "",
    val gender: String = "OTHER",
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

fun ChildDto.toDomain(): Child {
    return Child(
        id = id,
        name = name,
        dateOfBirth = dateOfBirth,
        admissionDate = admissionDate,
        age = age,
        gender = try {
            Gender.valueOf(gender.uppercase())
        } catch (e: Exception) {
            Gender.OTHER
        },
        fatherName = fatherName,
        motherName = motherName,
        fatherMobile = fatherMobile,
        motherMobile = motherMobile,
        placeOfBirth = placeOfBirth,
        bloodGroup = bloodGroup,
        physicallyChallenged = physicallyChallenged,
        height = height,
        weight = weight,
        allergies = allergies,
        healthNotes = healthNotes,
        anganwadiCenterId = anganwadiCenterId,
        photoUrl = photoUrl
    )
}

fun Child.toDto(): ChildDto {
    return ChildDto(
        id = id,
        name = name,
        dateOfBirth = dateOfBirth,
        admissionDate = admissionDate,
        age = age,
        gender = gender.name,
        fatherName = fatherName,
        motherName = motherName,
        fatherMobile = fatherMobile,
        motherMobile = motherMobile,
        placeOfBirth = placeOfBirth,
        bloodGroup = bloodGroup,
        physicallyChallenged = physicallyChallenged,
        height = height,
        weight = weight,
        allergies = allergies,
        healthNotes = healthNotes,
        anganwadiCenterId = anganwadiCenterId,
        photoUrl = photoUrl
    )
}
