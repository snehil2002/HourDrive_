package com.example.hourdrive.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourdrive.R
import com.example.hourdrive.screens.screencomponents.BottomNavigationBar
import com.example.hourdrive.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            userViewModel.getUserDetails(it.uid)
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar()
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userViewModel = userViewModel)
        }
    ) { innerPadding ->
        val user = userViewModel.muser.collectAsState().value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            ProfilePicture(avatar = R.drawable.upfp)
            Spacer(modifier = Modifier.height(20.dp))
            if (user != null) {
                Details(name = user.name , email = user.email)
            } else {
                CircularProgressIndicator()
            }
            EditProfileButton { }
            Spacer(modifier = Modifier.height(44.dp))
            ProfileMenus(navController, userViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    TopAppBar(
        title = { Text(text = "Profile",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )},
    )
}

@Composable
private fun ProfilePicture(avatar: Int) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatar),
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp)
        )
    }
}

@Composable
private fun Details(name: String, email: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = email, fontSize = 16.sp)
    }
}

@Composable
private fun EditProfileButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A62B7)),
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Text(text = "Edit Profile", color = Color.White)
    }
}

@Composable
private fun ProfileMenus(navController: NavController,
                         userViewModel: UserViewModel) {
    Column {
        ProfileMenuRow(
            icon = Icons.Default.Info,
            text = "My Bookings",
            onClick = {navController.navigate(HourDriveScreens.BookingScreen.name)  }
        )
        ProfileMenuRow(
            icon = Icons.Default.Settings,
            text = "Settings",
            onClick = { navController.navigate(HourDriveScreens.SettingScreen.name)  }
        )
        ProfileMenuRow(
            iconRes = R.drawable.ic_logout,
            text = "Logout",
            textColor = Color(0xFFDD3636),
            onClick = {


                FirebaseAuth.getInstance().signOut()
                userViewModel.clearUserData()
                navController.navigate(HourDriveScreens.SplashScreen.name) {
                    popUpTo(HourDriveScreens.ProfileScreen.name) { inclusive = true }

                }
            }
        )
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color(0xFF8080E2)
            )
        }
        iconRes?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                tint = Color(0xFF8080E2)
            )
        }
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier
                .padding(start = 22.dp)
                .weight(1f)
        )
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}




