package com.example.hourdrive.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourdrive.R
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.material.LinearProgressIndicator
import com.example.hourdrive.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun splashScreen(navController: NavController,userViewModel: UserViewModel) {
    val auth=FirebaseAuth.getInstance()
    var visible by remember { mutableStateOf(true) }
    val muser by userViewModel.muser.collectAsState()


    LaunchedEffect(Unit) {
        delay(2000)
        visible = false
        delay(300)
    if (auth.currentUser==null){
        navController.navigate(HourDriveScreens.LoginScreen.name) {
            popUpTo(HourDriveScreens.SplashScreen.name) { inclusive = true }

        }}else
    {
        userViewModel.getUserDetails(auth.currentUser!!.uid)


    }


    }
    LaunchedEffect(muser) {

        if (muser!=null){
            if (muser!!.driver){
                navController.navigate(HourDriveScreens.CarsHomeScreen.name) {
                    popUpTo(HourDriveScreens.SplashScreen.name) { inclusive = true }

                }

            }else{
                navController.navigate(HourDriveScreens.HomeScreen.name) {
                    popUpTo(HourDriveScreens.SplashScreen.name) { inclusive = true }

                }

            }

        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001A33)),
        contentAlignment = Alignment.Center
    ) {






            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {

                Text(
                    text = "HourDrive",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.splash),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(400.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "If you have a car and need someone to drive it for you, an ideal driver will be available to carry you wherever you want at low rates.",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)

}


