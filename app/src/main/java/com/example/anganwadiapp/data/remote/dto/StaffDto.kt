package com.example.anganwadiapp.data.remote.dto

import com.example.anganwadiapp.domain.model.Staff
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class StaffDto(
    val id: String = "",
    val name: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    //val username: String = "",
    val anganwadiCenterId: String = ""
)

fun StaffDto.toDomain(): Staff {
    return Staff(
        id = id,
        name = name,
        mobileNumber = mobileNumber,
        email = email,
        //username = username,
        anganwadiCenterId = anganwadiCenterId
    )
}

fun Staff.toDto(): StaffDto {
    return StaffDto(
        id = id,
        name = name,
        mobileNumber = mobileNumber,
        email = email,
        //username = username,
        anganwadiCenterId = anganwadiCenterId
    )
}
