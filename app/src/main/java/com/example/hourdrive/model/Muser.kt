package com.example.hourdrive.model

import com.example.hourdrive.R


data class MUser(
    val uid:String="",
    val phoneNumber: String = "987654321",
    val name: String = "John Doe",
    val email: String = "",
    val createdAt: String = "",
    val driver:Boolean=false,
    val profileComplete:Boolean=false,
    val adhaar:String="",
    val driverLicenseNumber:String="DL12345678",
    val vehicleRegistrationNumber:String="",
    val carDetails:String="",
    val price:String = "1000",
    val driverProfilePicture: Int = R.drawable.upfp,
    val distance: String = "1",
    val status: String = "inactive",
    val rating: Double = 4.0

)

fun muserToMap(mUser: MUser):Map<String,Any>{
    return mapOf(
        "uid" to mUser.uid,
        "phoneNumber" to mUser.phoneNumber,
        "name" to mUser.name,
        "email" to mUser.email,
        "createdAt" to mUser.createdAt,
        "driver" to mUser.driver,
        "profileComplete" to mUser.profileComplete,
        "adhaar" to mUser.adhaar,
        "driverLicenseNumber" to mUser.driverLicenseNumber,
        "vehicleRegistrationNumber" to mUser.vehicleRegistrationNumber,
        "driverProfilePicture" to mUser.driverProfilePicture,
        "distance" to mUser.distance,
        "status" to mUser.status,
        "rating" to mUser.rating,
        "carDetails" to mUser.carDetails,
        "price" to mUser.price

        )
}