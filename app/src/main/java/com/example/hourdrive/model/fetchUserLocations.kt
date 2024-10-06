package com.example.hourdrive.model
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

fun fetchUserLocations(database: DatabaseReference, onLocationsFetched: (List<LocationData>) -> Unit) {
    database.child("locations").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val locations = mutableListOf<LocationData>()
            for (userSnapshot in dataSnapshot.children) {
                val locationData = userSnapshot.getValue(LocationData::class.java)
                locationData?.let { locations.add(it) }
            }
            onLocationsFetched(locations)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle possible errors.
            databaseError.toException().printStackTrace()
        }
    })

}


