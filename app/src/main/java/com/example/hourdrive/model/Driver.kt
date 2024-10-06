package com.example.hourdrive.model

import com.example.hourdrive.R
import com.google.android.gms.maps.model.LatLng


data class Driver(
    val location: LatLng=LatLng(0.0, 0.0),
    val status: String="available",
    val uid:String="",
    val phoneNumber: String = "987654321",
    val name: String = "John Doe",
    val email: String = "",
    val aadhaar:String="",
    val driverLicenseNumber:String="DL12345678",

)

fun mapDriverToData(driver: Driver): Map<String, Any> {
    return mapOf(
        "location" to driver.location,
        "status" to driver.status,
        "uid" to driver.uid,
        "phoneNumber" to driver.phoneNumber,
        "name" to driver.name,
        "email" to driver.email,
        "aadhaar" to driver.aadhaar,
        "driverLicenseNumber" to driver.driverLicenseNumber
    )
}
