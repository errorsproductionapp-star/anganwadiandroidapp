package com.example.anganwadiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val dateOfBirth: Long,
    val gender: String,
    val weight: Float?,
    val height: Float?,
    val motherName: String,
    val fatherName: String,
    val registrationDate: Long
)

fun ChildEntity.toDomain(): Child = Child(
    id = id,
    name = name,
    dateOfBirth = dateOfBirth,
    gender = Gender.valueOf(gender),
    weight = weight,
    height = height,
    motherName = motherName,
    fatherName = fatherName,
    registrationDate = registrationDate
)

fun Child.toEntity(): ChildEntity = ChildEntity(
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
