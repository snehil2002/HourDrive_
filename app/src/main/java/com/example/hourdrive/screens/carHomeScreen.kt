package com.example.hourdrive.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hourdrive.GoogleMapView
import com.example.hourdrive.model.LocationData
import com.example.hourdrive.model.fetchUserLocations
import com.example.hourdrive.model.getUserUid
import com.example.hourdrive.model.RideRequest
import com.example.hourdrive.screens.screencomponents.AppBar
import com.example.hourdrive.screens.screencomponents.BottomNavigationBar
import com.example.hourdrive.screens.screencomponents.SearchBar
import kotlinx.coroutines.launch
import com.example.hourdrive.screens.screencomponents.DrawerContent
import com.example.hourdrive.screens.screencomponents.cButton
import com.example.hourdrive.screens.screencomponents.linearProgressIndicator
import com.example.hourdrive.screens.screencomponents.dsRequestCard
import com.example.hourdrive.viewmodel.DriverSideViewmodel
import com.example.hourdrive.viewmodel.UserViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

@Composable
fun carHomeScreen(navController: NavController,userViewModel: UserViewModel,driverSideViewmodel: DriverSideViewmodel) {


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val searchquery= remember { mutableStateOf("") }

    val pendingList by driverSideViewmodel.pendingRequests.collectAsState()
    val userProfile by userViewModel.muser.collectAsState()
    val acceptedRequest by driverSideViewmodel.acceptedRequest.collectAsState()



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
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Find Car owner",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationClick = { }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController, userViewModel = userViewModel)
            }
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
                            searchQuery = searchquery,
                            placeholder = "Search for Requests",
                            onSearchQueryChange = {}
                        )
                    if (userProfile!!.status == "accepted" && acceptedRequest != null) {
                        driverSideViewmodel.fetchAcceptedRideRequests(userProfile!!.uid)
                        MapView(assignedCustomerId = acceptedRequest!!.customerId)
                    } else {
                        // Show the map with nearby drivers (normal case)
                        MapView()
                    }
                        when(userProfile!!.status){
                            "available" ->{
                                driverSideViewmodel.fetchPendingRideRequests(userProfile!!.uid)
                                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                                    cButton("Stop Accepting") {

                                        driverSideViewmodel.stopLooking(userProfile!!.uid)



                                    }
                                }
                                if (pendingList.isEmpty()) {
                                    NoRequestFoundMessage()
                                } else {
                                    requestList(requestList = pendingList) {
                                        navController.navigate(HourDriveScreens.CarBookingScreen.name+"/${it}")

                                    }                                }


                            }
                            "accepted" ->{
                                driverSideViewmodel.fetchAcceptedRideRequests(userProfile!!.uid)
                                if (acceptedRequest!=null){

                                        dsRequestCard(request = acceptedRequest!!) { }


                                }

                            }
                            "inactive" ->{
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    cButton("Accept Rides"){
                                        driverSideViewmodel.startLooking(userProfile!!.uid)
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
fun NoRequestFoundMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "No Requests Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun requestList(
    requestList: List<RideRequest>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = requestList,

        ) { request ->
            dsRequestCard(
                request = request,
                onClick = {onClick(request.uid)
                }
            )
        }
    }
}
@Composable
private fun MapView(assignedCustomerId: String? = null) {
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
        LaunchedEffect(assignedCustomerId) {
            if (assignedCustomerId != null) {
                fetchUserLocations(database) { locations ->
                    // Filter to show only the assigned driver’s location
                    userLocations = locations.filter { it.uid == assignedCustomerId }
                }
                cameraState=true
            } else {
                cameraState=false
                // Show all nearby driver locations
                fetchUserLocations(database) { locations ->
                    userLocations = locations.filter { it.driver == false}
                }
            }
        }

        // Display the Google Map with user locations (assigned driver or nearby drivers)
        GoogleMapView(navController = navController, locationDataList = userLocations, camera = cameraState)
    }
}



