package com.example.hourdrive.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourdrive.model.MUser
import com.example.hourdrive.model.RideRequest
import com.example.hourdrive.model.RideRequestToMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CustomerSideViewmodel:ViewModel(){
    val firestore=FirebaseFirestore.getInstance()
    private val _nearDrivers = MutableStateFlow<List<MUser>>(emptyList())  // Initial state is null
    val nearDrivers: StateFlow<List<MUser>> = _nearDrivers

    private val _driverDetails = MutableStateFlow<MUser?>(null)
    val driverDetails: StateFlow<MUser?> = _driverDetails

    private val _acceptedRequest = MutableStateFlow<RideRequest?>(null)
    val acceptedRequest: StateFlow<RideRequest?> = _acceptedRequest

    private val _pendingRequests = MutableStateFlow<RideRequest?>(null)  // Initial state is null
    val pendingRequests: StateFlow<RideRequest?> = _pendingRequests

    private val _completedRequests = MutableStateFlow<List<RideRequest>>(emptyList())  // Initial state is null
    val completedRequests: StateFlow<List<RideRequest>> = _completedRequests


    val startLocation = mutableStateOf("")
    val endLocation = mutableStateOf("")
    val money = mutableStateOf("")




    fun startLooking(customerId: String){
        viewModelScope.launch {
            firestore.collection("users").document(customerId)
                .update("status", "available")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }

    fun stopLooking(driverId: String){
        viewModelScope.launch {
            firestore.collection("users").document(driverId)
                .update("status", "inactive")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }

    fun fetchNearbyDrivers() {
        viewModelScope.launch {
        firestore.collection("users").whereEqualTo("status", "available").whereEqualTo("driver",true)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val nearbyDrivers = snapshot.toObjects(MUser::class.java)
                    _nearDrivers.value=nearbyDrivers
                }
            }
    }
    }

    fun sendBookingRequest(rideRequest: RideRequest) {
        viewModelScope.launch {
            val requestsCollection = firestore.collection("requests")


            val documentRef = requestsCollection.document()
            val uniqueId = documentRef.id


            val updatedRideRequest = rideRequest.copy(uid = uniqueId)

            documentRef
            .set(RideRequestToMap(updatedRideRequest))
            .addOnSuccessListener { /* Notify success */ }
            .addOnFailureListener { /* Handle failure */ }

            firestore.collection("users").document(rideRequest.customerId)
                .update("status", "pending")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }

    fun fetchDriverDetails(driverId: String) {
        viewModelScope.launch {
            firestore.collection("users").document(driverId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val customer = document.toObject(MUser::class.java)
                        _driverDetails.value = customer
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                }
        }
    }
    fun fetchPendingRideRequests(customerId:String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("requests")
            ridesRef.whereEqualTo("status", "pending").whereEqualTo("customerId",customerId).limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty ) {
                        val rideRequests = snapshot.documents[0].toObject(RideRequest::class.java)
                        _pendingRequests.value=rideRequests
                    }
                }
        }}

    fun fetchAcceptedRideRequests(customerId:String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("requests")
            ridesRef.whereEqualTo("status", "accepted").whereEqualTo("customerId",customerId).limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        val rideRequests = snapshot.documents[0].toObject(RideRequest::class.java)
                        _acceptedRequest.value=rideRequests
                    }
                }
        }}



    fun completeRideRequest(rideRequestId: String,customerId: String,driverId: String) {
        viewModelScope.launch {
            firestore.collection("requests").document(rideRequestId)
                .update("status", "completed")
                .addOnSuccessListener { /* Notify customer */ }
                .addOnFailureListener { /* Handle error */ }
            firestore.collection("users").document(driverId)
                .update("status", "inactive")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
            firestore.collection("users").document(customerId)
                .update("status", "inactive")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }


        }
    }

    fun deleteRequest(requestId:String,customerId: String){
        viewModelScope.launch {
            firestore.collection("requests").document(requestId).delete()
            firestore.collection("users").document(customerId)
                .update("status", "inactive")
        }
    }
    fun fetchCompletedRideRequests(customerId: String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("requests")
            try {
                val snapshot = ridesRef.whereEqualTo("customerId", customerId)
                    .whereEqualTo("status", "completed")
                    .get()
                    .await()
                val rideRequests = snapshot.toObjects(RideRequest::class.java)
                _completedRequests.value = rideRequests
            } catch (e: Exception) {
                // Handle error
                Log.d("FetchError", "Error fetching ride requests: ${e.message}")
            }
        }
    }



}