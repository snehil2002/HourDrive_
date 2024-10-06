package com.example.hourdrive.model

import com.google.android.gms.maps.model.LatLng

data class Customer(
    val location: LatLng,
    val money:Int,
    val journeyStart:String,
    val journeyEnd:String,
    val uid:String="",
    val phoneNumber: String = "987654321",
    val name: String = "John Doe",
    val email: String = "",
    val aadhaar:String="",
    val vehicleRegistrationNumber:String="",
    val carDetails:String="",
    val status:String="available"
)

fun mapCustomerToData(customer: Customer): Map<String, Any> {
    return mapOf(
        "location" to customer.location,
        "money" to customer.money,
        "journeyStart" to customer.journeyStart,
        "journeyEnd" to customer.journeyEnd,
        "uid" to customer.uid,
        "phoneNumber" to customer.phoneNumber,
        "name" to customer.name,
        "email" to customer.email,
        "aadhaar" to customer.aadhaar,
        "vehicleRegistrationNumber" to customer.vehicleRegistrationNumber,
        "carDetails" to customer.carDetails,
        "status" to customer.status
    )
}

