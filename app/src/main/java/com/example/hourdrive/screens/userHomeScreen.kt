@file:Suppress("DEPRECATION")

package com.example.hourdrive.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hourdrive.GoogleMapView

import com.example.hourdrive.model.LocationData
import com.example.hourdrive.model.MUser
import com.example.hourdrive.model.fetchUserLocations
import com.example.hourdrive.model.getUserUid
import com.example.hourdrive.screens.screencomponents.AppBar
import com.example.hourdrive.screens.screencomponents.BottomNavigationBar
import com.example.hourdrive.screens.screencomponents.DrawerContent
import com.example.hourdrive.screens.screencomponents.DriverCard
import com.example.hourdrive.screens.screencomponents.SearchBar
import com.example.hourdrive.screens.screencomponents.cButton
import com.example.hourdrive.screens.screencomponents.csRequestCard
import com.example.hourdrive.screens.screencomponents.linearProgressIndicator
import com.example.hourdrive.viewmodel.CustomerSideViewmodel
import com.example.hourdrive.viewmodel.PaymentViewModel
import com.example.hourdrive.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay


@Composable
fun homeScreen(navController: NavController, customerSideViewmodel: CustomerSideViewmodel,
               userViewModel: UserViewModel, paymentViewModel: PaymentViewModel) {




    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val driverList by customerSideViewmodel.nearDrivers.collectAsState()
    val userProfile by userViewModel.muser.collectAsState()

    val pendingRequest by customerSideViewmodel.pendingRequests.collectAsState()
    val acceptedRequest by customerSideViewmodel.acceptedRequest.collectAsState()

    val searchquery= remember { mutableStateOf("") }
    val context = LocalContext.current



    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController = navController, userViewModel = userViewModel) {
                    scope.launch { drawerState.close() }
                }
            }
        }
    )
    {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Hire a Driver",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationClick = {
                        navController.navigate(HourDriveScreens.NotificationScreen.name)
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController, userViewModel = userViewModel) }
        ) { innerPadding ->
            if (userProfile==null){
                linearProgressIndicator()

            }
            else{


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        SearchBar(
                            searchQuery =searchquery ,
                            placeholder = "Search for drivers",
                            onSearchQueryChange = {}
                        )
// Check if the ride request is accepted, show the driver’s location only
                        if (userProfile!!.status == "accepted" && acceptedRequest != null) {
                            customerSideViewmodel.fetchAcceptedRideRequests(userProfile!!.uid)
                            MapView(assignedDriverId = acceptedRequest!!.driverId)
                        } else {
                            // Show the map with nearby drivers (normal case)
                            MapView()
                        }

                        when(userProfile!!.status){
                            "available" ->{
                                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                cButton("Stop Hiring") {
                    customerSideViewmodel.startLocation.value=""
                    customerSideViewmodel.endLocation.value=""
                    customerSideViewmodel.money.value=""
                    customerSideViewmodel.stopLooking(userProfile!!.uid)



}}
                                if (driverList.isEmpty()) {
                                    NoDriversFoundMessage()
                                } else {
                                    DriversList(drivers = driverList, onDriverSelected = {driverId->
                                        customerSideViewmodel.fetchDriverDetails(driverId)
                                        navController.navigate(HourDriveScreens.DriverBookingScreen.name)
                                    })
                                }


                            }
                            "pending"->{
                                customerSideViewmodel.fetchPendingRideRequests(userProfile!!.uid)
                                if (pendingRequest!=null){
                                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                                    cButton("Delete Request") {
                                        customerSideViewmodel.startLocation.value=""
                                        customerSideViewmodel.endLocation.value=""
                                        customerSideViewmodel.money.value=""

                                        customerSideViewmodel.deleteRequest(pendingRequest!!.uid,pendingRequest!!.customerId)
                                    }
                                    csRequestCard(request = pendingRequest!!) { }
                                }

                            }
                            }
                            "accepted" ->{
                                customerSideViewmodel.fetchAcceptedRideRequests(userProfile!!.uid)
                                if (acceptedRequest!=null){
                                    Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                                        cButton("Journey Completed") {
                                            val activity = context as? Activity

                                            activity?.let {
                                                val moneyValue = customerSideViewmodel.money.value.toIntOrNull()
                                                    ?: 0
                                                if (moneyValue > 0) {
                                                    paymentViewModel.startPayment(activity, moneyValue)
                                                } else {
                                                    Toast.makeText(activity, "Amount is invalid", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            customerSideViewmodel.startLocation.value=""
                                            customerSideViewmodel.endLocation.value=""
                                            customerSideViewmodel.money.value=""

                                            customerSideViewmodel.completeRideRequest(acceptedRequest!!.uid,acceptedRequest!!.customerId,acceptedRequest!!.driverId)
                                        }
                                        csRequestCard(request = acceptedRequest!!) { }
                                    }

                                }

                            }
                            "inactive" ->{
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    TextField(
                                        value = customerSideViewmodel.startLocation.value,
                                        onValueChange = { customerSideViewmodel.startLocation.value = it },
                                        placeholder = { Text("Start Location") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                            unfocusedIndicatorColor = Color.Gray
                                        )
                                    )
                                    TextField(
                                        value = customerSideViewmodel.endLocation.value,
                                        onValueChange = { customerSideViewmodel.endLocation.value = it },
                                        placeholder = { Text("End Location") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                            unfocusedIndicatorColor = Color.Gray
                                        )
                                    )
                                    TextField(
                                        value = customerSideViewmodel.money.value,
                                        onValueChange = { customerSideViewmodel.money.value = it },
                                        placeholder = { Text("Payment Amount") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                            unfocusedIndicatorColor = Color.Gray
                                        )
                                    )
                                    cButton("Hire Driver") {
                                        if (customerSideViewmodel.money.value.trim().isNotEmpty() && customerSideViewmodel.startLocation.value.trim().isNotEmpty() && customerSideViewmodel.endLocation.value.trim().isNotEmpty() ){
                                            customerSideViewmodel.fetchNearbyDrivers()
                                        customerSideViewmodel.startLooking(userProfile!!.uid)}



                                    }

                                }
                            }
                        }



                    }
                }


            }

        }
    }




@Composable
fun NoDriversFoundMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(
            text = "No driver found",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }}

@Composable
fun DriversList(
    drivers: List<MUser>,
    onDriverSelected: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = drivers,
            key = { it.uid }
        )
        { driver ->
            DriverCard(
                mUser = driver,
                onClick = { onDriverSelected(driver.uid) }
            )
        }
    }
}

@Composable
private fun MapView(assignedDriverId: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {

        val navController = rememberNavController()
        val database = FirebaseDatabase.getInstance().reference

        var userLocations by remember { mutableStateOf<List<LocationData>>(emptyList()) }
        var cameraState by remember { mutableStateOf(false) }

        // Fetch the driver’s location if an assigned driver ID is provided
        LaunchedEffect(assignedDriverId) {
            if (assignedDriverId != null) {
                fetchUserLocations(database) { locations ->
                    // Filter to show only the assigned driver’s location
                    userLocations = locations.filter { it.uid == assignedDriverId }
                }
                cameraState =true
            } else {
                cameraState=false
                // Show all nearby driver locations
                fetchUserLocations(database) { locations ->
                    userLocations = locations.filter { it.driver == true }
                }
            }
        }

        // Display the Google Map with user locations (assigned driver or nearby drivers)
        GoogleMapView(navController = navController, locationDataList = userLocations, camera = cameraState)
    }
}








