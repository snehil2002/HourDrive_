@file:Suppress("DEPRECATION")

package com.example.hourdrive.model
import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.location.FusedLocationProviderClient

import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val REQUEST_CHECK_SETTINGS = 1002

// Your LocationData data class
data class LatLong(
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class LocationData(
    val status:String?=null,
    val name: String? = null,
    val driver:Boolean?=null,
    val uid: String? = null,
    val latLong: LatLong? = null // Changed to use LatLong
)

// Function to convert LocationData to a map
fun locationToMap(status: String?, uid: String?, name: String?,driver: Boolean?, latLong: LatLong?): Map<String, Any?> {
    return mapOf(
        "status" to status,
        "name" to name,
        "driver" to driver,
        "uid" to uid,
        "latLong" to latLong
    )
}
// Function to get user UID from Firebase Authentication
fun getUserUid(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
}
data class UserData(
    val name: String? = null,
    val driver: Boolean? = null,
    val status: String?=null
)

// Function to retrieve the user's name and driver status from Firestore
fun getUserData(onResult: (UserData?) -> Unit) {
    val uid = getUserUid() // Retrieve the user UID
    if (uid != null) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid) // Replace "users" with your collection name

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Get the name and driver status from the document
                val name = document.getString("name") // Replace "name" with the exact field name in Firestore
                val driver = document.getBoolean("driver") // Replace "driver" with the exact field name in Firestore
                val status= document.getString("status")
                // Create UserData object with the retrieved values
                val userData = UserData(name, driver,status)
                onResult(userData)
            } else {
                onResult(null) // Document does not exist
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            onResult(null)
            Log.w("Firestore", "Error getting document: ", exception)
        }
    } else {
        onResult(null) // User UID is null
    }
}


// Function to store live location in Firebase Realtime Database
fun storeLocationInFirebase(locationData: LocationData) {
    val databaseRef = FirebaseDatabase.getInstance().getReference("locations")

    locationData.uid?.let { uid ->
        databaseRef.child(uid).setValue(locationToMap(uid = uid,name=locationData.name, driver = locationData.driver, status = locationData.status, latLong = locationData.latLong))
            .addOnSuccessListener {
                // Successfully stored location
            }
            .addOnFailureListener { e ->
                // Handle the error
                e.printStackTrace()
            }
    }
}

fun getCurrentLocationAndStore(
    fusedLocationClient: FusedLocationProviderClient,
    activity: Activity
) {
    try {
        // Step 1: Create LocationRequest
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update interval every 10 seconds
            fastestInterval = 5000 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // High accuracy
        }

        // Step 2: Create LocationSettingsRequest
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        // Step 3: Check if location settings are satisfied
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener {
            // Location settings are satisfied. Proceed to get location.
            fetchLocation(fusedLocationClient, activity)
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog
                    exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error
                    sendEx.printStackTrace()
                }
            } else {
                Log.e("Location", "Location settings are not satisfied.")
            }
        }

    } catch (e: SecurityException) {
        Log.e("Location", "SecurityException: Missing location permissions", e)
    }
}

// Helper function to fetch the location and store it in Firebase
fun fetchLocation(fusedLocationClient: FusedLocationProviderClient, activity: Activity) {
    // Check location permissions before requesting location
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
        return
    }

    val uid = getUserUid()


    if (uid == null) {
        Log.e("Location", "User is not authenticated.")
        return
    }

    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            getUserData { userData ->
                if (userData != null) {
                    val locationData = LocationData(
                        uid = uid,
                        status = userData.status,
                        name = userData.name,
                        driver = userData.driver,
                        latLong = LatLong(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )

                    Log.d("Location", "Storing location: ${location.latitude}, ${location.longitude}")
                    storeLocationInFirebase(locationData)
                } else {
                    Log.d("UserData", "User data not found")
                }
            }


        } else {
            Log.e("Location", "Location is null.")
        }
    }.addOnFailureListener { exception ->
        Log.e("Location", "Failed to get location.", exception)
    }
}
