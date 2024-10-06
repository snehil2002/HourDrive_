package com.example.hourdrive.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hourdrive.R
import com.example.hourdrive.screens.screencomponents.BottomNavigationBar
import com.example.hourdrive.screens.screencomponents.csRequestCard
import com.example.hourdrive.viewmodel.CustomerSideViewmodel
import com.example.hourdrive.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customerPastBookingScreen(navController: NavController, userViewModel: UserViewModel,customerSideViewmodel: CustomerSideViewmodel) {
    LaunchedEffect(Unit) {
        customerSideViewmodel.fetchCompletedRideRequests(userViewModel.muser.value!!.uid)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Bookings",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userViewModel )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            customerBookingContent(navController = navController,userViewModel,customerSideViewmodel)
        }
    }
}

@Composable
fun customerBookingContent(navController: NavController,userViewModel: UserViewModel,customerSideViewmodel: CustomerSideViewmodel) {
    val completed by customerSideViewmodel.completedRequests.collectAsState()
    if(completed.isEmpty()){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_booking_screen),
            contentDescription = "No Previous Booking Image",
            modifier = Modifier.size(300.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No Previous Booking",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "All your bookings will be shown here",
            color = Color(0xFF667085),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        customerBookDriversButton(navController )
    }}
    else{
        LazyColumn {
            items(completed){
                csRequestCard(it) { }
            }
        }
    }
}

@Composable
fun customerBookDriversButton(navController: NavController) {

    Button(
        onClick = { navController.navigate(HourDriveScreens.HomeScreen.name) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A62B7)),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "Book Drivers", color = Color.White)

    }
}


