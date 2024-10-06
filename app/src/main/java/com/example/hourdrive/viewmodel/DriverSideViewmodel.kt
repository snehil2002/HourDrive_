package com.example.hourdrive.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourdrive.model.MUser
import com.example.hourdrive.model.RideRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DriverSideViewmodel: ViewModel() {
    val firestore = FirebaseFirestore.getInstance()

    private val _pendingRequests = MutableStateFlow<List<RideRequest>>(emptyList())  // Initial state is null
    val pendingRequests: StateFlow<List<RideRequest>> = _pendingRequests

    private val _completedRequests = MutableStateFlow<List<RideRequest>>(emptyList())  // Initial state is null
    val completedRequests: StateFlow<List<RideRequest>> = _completedRequests

    private val _customerDetails = MutableStateFlow<MUser?>(null)
    val customerDetails: StateFlow<MUser?> = _customerDetails

    private val _acceptedRequest = MutableStateFlow<RideRequest?>(null)
    val acceptedRequest: StateFlow<RideRequest?> = _acceptedRequest

    fun startLooking(driverId: String){
        viewModelScope.launch {
            firestore.collection("users").document(driverId)
                .update("status", "available")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }
    fun stopLooking(customerId: String){
        viewModelScope.launch {
            firestore.collection("users").document(customerId)
                .update("status", "inactive")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }
    fun acceptRideRequest(rideRequestId: String,customerId: String,driverId: String) {
        viewModelScope.launch {
        firestore.collection("requests").document(rideRequestId)
            .update("status", "accepted")
            .addOnSuccessListener { /* Notify customer */ }
            .addOnFailureListener { /* Handle error */ }
            firestore.collection("users").document(driverId)
                .update("status", "accepted")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
            firestore.collection("users").document(customerId)
                .update("status", "accepted")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }


    }
    }

    fun fetchPendingRideRequests(driverId:String) {
        viewModelScope.launch {
        val ridesRef = FirebaseFirestore.getInstance().collection("requests")
        ridesRef.whereEqualTo("status", "pending").whereEqualTo("driverId",driverId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val rideRequests = snapshot.toObjects(RideRequest::class.java)
                    _pendingRequests.value=rideRequests
                }
            }
    }}



    fun fetchCustomerDetails(customerId: String) {
        viewModelScope.launch {
            firestore.collection("users").document(customerId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val customer = document.toObject(MUser::class.java)
                        _customerDetails.value = customer
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                }
        }
    }

    fun fetchAcceptedRideRequests(driverId:String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("requests")
            ridesRef.whereEqualTo("status", "accepted").whereEqualTo("driverId",driverId).limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        val rideRequests = snapshot.documents[0].toObject(RideRequest::class.java)
                        _acceptedRequest.value=rideRequests
                    }
                }
        }}

    fun fetchCompletedRideRequests(driverId: String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("requests")
            try {
                val snapshot = ridesRef.whereEqualTo("driverId", driverId)
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
