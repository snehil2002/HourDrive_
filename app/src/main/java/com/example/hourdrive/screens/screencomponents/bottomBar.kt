package com.example.hourdrive.screens.screencomponents

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hourdrive.R
import androidx.compose.ui.graphics.Color
import com.example.hourdrive.screens.HourDriveScreens
import com.example.hourdrive.viewmodel.UserViewModel


@Composable
fun BottomNavigationBar(navController: NavController, userViewModel: UserViewModel) {
    val muser by userViewModel.muser.collectAsState()
    NavigationBar(
        containerColor = Color(0xFFEFEFEF)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = {

                if (muser!=null){
                    if (muser!!.driver){

                        navController.navigate(HourDriveScreens.CarsHomeScreen.name)

                    }else{
                        navController.navigate(HourDriveScreens.HomeScreen.name)
                    }

                }
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.booking_icon), contentDescription = "Booking") },
            label = { Text("Booking") },
            selected = false,
            onClick = {if (muser!=null){
                if (muser!!.driver){
                    navController.navigate(HourDriveScreens.DriverPastBookingScreen.name)

                }else{
                    navController.navigate(HourDriveScreens.CustomerPastBookingScreen.name)
                }

            }
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.message_icon), contentDescription = "Message") },
            label = { Text("Messages") },
            selected = false,
            onClick = {
                navController.navigate(HourDriveScreens.MessagesScreen.name)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = {
                navController.navigate(HourDriveScreens.ProfileScreen.name)
            }
        )
    }
}
@Composable
fun BottomNavigationBarScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, userViewModel = UserViewModel())
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun BottomNavigationBarScreenPreview() {
    BottomNavigationBarScreen()
}