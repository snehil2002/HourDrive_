package com.example.hourdrive

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.hourdrive.model.LocationData
import com.example.hourdrive.model.getCurrentLocationAndStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay

@Composable
fun GoogleMapView(
    navController: NavController,
    camera:Boolean,
    locationDataList: List<LocationData> // List of user locations
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val userMarkers = remember { mutableMapOf<String, Marker>() } // Stores user markers

    // Check and request location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getCurrentLocation(fusedLocationClient) { location ->
                    currentLocation = location
                }

            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        delay(3000)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation(fusedLocationClient) { location ->
                currentLocation = location
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { map ->
            map.getMapAsync { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true

                // Enable the user's location layer
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap.isMyLocationEnabled = true
                }

//                // Move camera to the user's location initially, if available
//                currentLocation?.let { location ->
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
//                }
                val manIcon = resizeMapIcon(context, com.example.hourdrive.R.drawable.ic_man, 120  , 130)
                val carIcon= resizeMapIcon(context, com.example.hourdrive.R.drawable.ic_car, 140  , 130)
                // Add or update markers for each user's location in the list
                locationDataList.forEach { locationData ->
                    locationData.latLong?.let { latLong ->
                        val userLocation = LatLng(latLong.latitude ?: 0.0, latLong.longitude ?: 0.0)
                        val uidString = locationData.uid.toString()

                        // Check if the user is inactive and remove the marker if it exists
                        if (locationData.status == "inactive") {
                            // Remove the marker from the map if the user is inactive
                            userMarkers[uidString]?.remove()

                            // Remove the marker from the userMarkers map
                            userMarkers.remove(uidString)
                        } else {
                            // Check if a marker already exists for this active user
                            val marker = userMarkers[uidString]

                            if (marker != null) {
                                // Update the marker's position if it exists
                                marker.position = userLocation
                                marker.title = "User: ${locationData.name}"
                            } else if (locationData.driver==true) {
                                // Add a new marker for the active user
                                val newMarker = googleMap.addMarker(
                                    MarkerOptions().position(userLocation).title("User: ${locationData.name}")
                                        .icon(manIcon)
                                )
                                newMarker?.showInfoWindow()


                                // Store the new marker in the map if it is non-null
                                if (newMarker != null) {
                                    userMarkers[uidString] = newMarker
                                }
                            }
                            else{
                                val newMarker = googleMap.addMarker(
                                    MarkerOptions().position(userLocation).title("User: ${locationData.name}")
                                        .icon(carIcon)
                                )
                                if (newMarker != null) {
                                    userMarkers[uidString] = newMarker
                                }
                            }
                        }
                        if (camera){
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        }
                    }
                }

            }
        }
    )
}


@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                mapView.onCreate(Bundle())
            }

            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDestroy()
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    } catch (e: SecurityException) {
        // Handle the security exception if permissions are not granted
        e.printStackTrace()
    }
}

fun resizeMapIcon(context: Context, drawableId: Int, width: Int, height: Int): BitmapDescriptor {
    val imageBitmap = BitmapFactory.decodeResource(context.resources, drawableId)
    val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
}