package com.example.hourdrive.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourdrive.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class Car(
    val profilePicture: Int,
    val name: String,
    val distance: String,
    val carModel: String
)

class CarsViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allCars = MutableStateFlow<List<Car>>(emptyList())
    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    init {
        loadCars()
        viewModelScope.launch {
            combine(_allCars, _searchQuery) { cars, query ->
                if (query.isEmpty()) {
                    cars
                } else {
                    cars.filter { it.name.contains(query, ignoreCase = true) }
                }
            }.collect { filteredCars ->
                _cars.value = filteredCars
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onCarSelected(car: Car) {
        // Handle car selection
        println("Car selected: ${car.name}")
    }

    private fun loadCars() {
        viewModelScope.launch {
            _allCars.value = getDummyCars()
        }
    }

    private fun getDummyCars(): List<Car> {
        return listOf(
            Car(
                profilePicture = R.drawable.car,
                name = "Car A",
                distance = "10km",
                carModel = "Model A"
            ),
            Car(
                profilePicture = R.drawable.car3,
                name = "Car B",
                distance = "20km",
                carModel = "Model B"
            ),
            Car(
                profilePicture = R.drawable.car2,
                name = "Car C",
                distance = "20km",
                carModel = "Model B"
            ),
            Car(
                profilePicture = R.drawable.car4,
                name = "Car D",
                distance = "20km",
                carModel = "Model B"
            )
        )
    }
}