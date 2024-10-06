package com.example.hourdrive.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourdrive.R
import com.example.hourdrive.model.MUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allDrivers = MutableStateFlow<List<MUser>>(emptyList())
    private val _drivers = MutableStateFlow<List<MUser>>(emptyList())
    val drivers: StateFlow<List<MUser>> = _drivers.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadDrivers()
        viewModelScope.launch {
            combine(_allDrivers, _searchQuery) { drivers, query ->
                if (query.isEmpty()) {
                    drivers
                } else {
                    drivers.filter { it.name.contains(query, ignoreCase = true) }
                }
            }.collect { filteredDrivers ->
                _drivers.value = filteredDrivers
            }
        }
    }
    fun refreshDrivers() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                _allDrivers.value = getDriverList()
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing drivers: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadDrivers() {
        viewModelScope.launch {
            try {
                // Simulating network call with dummy data
                _allDrivers.value = getDriverList()
            } catch (e: Exception) {
                // Handle error, e.g., show error message
                println("Error loading drivers: ${e.message}")
            }
        }
    }


    suspend fun getDriverList(): List<MUser> {
        val db = FirebaseFirestore.getInstance()
        val driverList = mutableListOf<MUser>()

        try {
            val result = db.collection("users").get().await()
            for (document in result.documents) {
                val driver = document.toObject(MUser::class.java)
                if (driver != null && driver.driver) {
                    driverList.add(driver)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting documents.", e)
        }

        return driverList
    }
}

