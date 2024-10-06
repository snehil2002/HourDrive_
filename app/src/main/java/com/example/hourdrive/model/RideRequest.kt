package com.example.hourdrive.model

data class RideRequest(
    val uid:String="",
    val customerId: String="",
    val driverId: String="",
    val startLocation:String="",
    val endLocation:String="",
    val money:String="",
    val driverName:String="",
    val customerName:String="",
    val driverNumber:String="",
    val customerNumber: String="",
    val carInfo:String="",
    val status: String="" // "pending", "accepted", etc.
)

fun RideRequestToMap(rr:RideRequest): Map<String, Any> {
    return mapOf(
        "uid" to rr.uid,
        "customerId" to rr.customerId,
        "driverId" to rr.driverId,
        "startLocation" to rr.startLocation,
        "endLocation" to rr.endLocation,
        "money" to rr.money,
        "driverName" to rr.driverName,
        "customerName" to rr.customerName,
        "driverNumber" to rr.driverNumber,
        "customerNumber" to rr.customerNumber,
        "carInfo" to rr.carInfo,
        "status" to rr.status
    )
}

