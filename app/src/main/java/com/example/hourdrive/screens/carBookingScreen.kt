package com.example.hourdrive.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourdrive.R
import com.example.hourdrive.model.RideRequest
import com.example.hourdrive.viewmodel.DriverSideViewmodel

@Composable
fun CarBookingScreen(navController: NavController,
    driverSideViewmodel: DriverSideViewmodel,
                     requestId:String
) {

var request:RideRequest=RideRequest()

    val requestList=driverSideViewmodel.pendingRequests.collectAsState().value.filter {
        it.uid==requestId
    }
    if (!requestList.isEmpty()){
        request=requestList[0]
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Booking Details",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.upfp),
            contentDescription = "Car Image",
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
                CarInfoRow(label = "Car Info", value = request.carInfo)
                Divider(color = Color.LightGray, thickness = 1.dp)
                CarInfoRow(label = "Start Location", value = request.startLocation)
                Divider(color = Color.LightGray, thickness = 1.dp)
                CarInfoRow(label = "End Location", value = request.endLocation)
                Divider(color = Color.LightGray, thickness = 1.dp)
                CarInfoRow(label = "Customer Name", value = request.customerName)
                Divider(color = Color.LightGray, thickness = 1.dp)
                CarInfoRow(label = "Phone Number", value = request.customerNumber)
                Divider(color = Color.LightGray, thickness = 1.dp)
                CarInfoRow(label = "Payment", value = request.money)

            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {driverSideViewmodel.acceptRideRequest(requestId, customerId = request.customerId, driverId = request.driverId)
                    navController.navigate(HourDriveScreens.CarsHomeScreen.name) {
                        popUpTo(HourDriveScreens.CarBookingScreen.name) { inclusive = true }

                    }},
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)) // Green button
            ) {
                Text(text = "Accept", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Button(
                onClick = {navController.popBackStack()},
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC574B)) // Red button
            ) {
                Text(text = "Go Back", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}


@Composable
fun CarInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

