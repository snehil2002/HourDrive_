package com.example.hourdrive.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourdrive.R
import com.example.hourdrive.model.RideRequest
import com.example.hourdrive.viewmodel.CustomerSideViewmodel
import com.example.hourdrive.viewmodel.UserViewModel

@Composable
fun DriverBookingScreen(navController: NavController,customerSideViewmodel: CustomerSideViewmodel,userViewModel: UserViewModel) {
    val driver=customerSideViewmodel.driverDetails.collectAsState()
    val user=userViewModel.muser.collectAsState()

    if (driver.value==null){
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(5.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }else{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {

        IconButton(onClick = {navController.navigateUp()}) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back Button")
        }
            Text(
                text = "Driver Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.upfp),
            contentDescription = "Driver Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                InfoRow(label = "Driver Name", value = driver.value!!.name)
                Divider(thickness = 1.dp, color = Color.LightGray)
                InfoRow(label = "Phone", value = driver.value!!.phoneNumber)
                Divider(thickness = 1.dp, color = Color.LightGray)
                InfoRow(label = "License Number", value = driver.value!!.driverLicenseNumber)
                Divider(thickness = 1.dp, color = Color.LightGray)


            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                customerSideViewmodel.sendBookingRequest(rideRequest = RideRequest(
                    uid= "",
                    customerId = user.value!!.uid,
                    driverId = driver.value!!.uid,
                    startLocation = customerSideViewmodel.startLocation.value,
                    endLocation = customerSideViewmodel.endLocation.value,
                    money = customerSideViewmodel.money.value,
                    driverName = driver.value!!.name,
                    driverNumber = driver.value!!.phoneNumber,
                    customerNumber = user.value!!.phoneNumber,
                    customerName = user.value!!.name,
                    carInfo = user.value!!.carDetails,
                    status = "pending"
                ))
                navController.navigate(HourDriveScreens.HomeScreen.name) {
                    popUpTo(HourDriveScreens.DriverBookingScreen.name) { inclusive = true }

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
        ) {
            Text(text = "Book Driver", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),  // Add some spacing between rows
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)  // Label takes up some space
        )
        Spacer(modifier = Modifier.width(16.dp))  // Add spacing between label and value
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),  // Value takes up the remaining space
            textAlign = TextAlign.End  // Align the value text to the end
        )
    }
}
