package com.example.hourdrive.screens.screencomponents


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hourdrive.R
import com.example.hourdrive.screens.HourDriveScreens
import com.example.hourdrive.viewmodel.UserViewModel


@Composable
fun DrawerContent(navController: NavController,userViewModel: UserViewModel, onItemClick: () -> Unit) {


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(40.dp))

        Image(painter = painterResource(id = R.drawable.upfp),
            contentDescription =null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp) )

        Text(
            "Book a Driver",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Version 1.0.0  \n  © NimbleQ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(0.55f)
        )

        Spacer(modifier = Modifier.height(16.dp))
        DrawerItem("Home", Icons.Default.Home) {
            onItemClick()
        }

        DrawerItem("Bookings", iconRes = R.drawable.booking_icon) {
            onItemClick()
            if (userViewModel.muser.value!!.driver){
            navController.navigate(HourDriveScreens.DriverBookingScreen.name)}
            else{
                navController.navigate(HourDriveScreens.CustomerPastBookingScreen.name)
            }
        }

        DrawerItem("Search", Icons.Default.Search) {
            onItemClick()
        }

        DrawerItem("Message",
            iconRes = R.drawable.message_icon) {
            onItemClick()
            navController.navigate(HourDriveScreens.MessagesScreen.name)
        }

        DrawerItem("Profile", Icons.Default.Person) {
            onItemClick()
            navController.navigate(HourDriveScreens.ProfileScreen.name)
        }
    }
}




@Composable
fun DrawerItem(title: String, icon: ImageVector? = null, iconRes: Int? =null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        icon?.let {
            Icon(
                imageVector = it, contentDescription = null)
        }
        iconRes?.let {
            Icon(painter = painterResource(id = it), contentDescription = null, Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(32.dp))
        Text(title)
    }
}