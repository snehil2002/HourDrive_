package com.example.hourdrive.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.hourdrive.viewmodel.UserViewModel

@Composable
fun MessageScreen(navController: NavController,userViewModel: UserViewModel) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, userViewModel = userViewModel)
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Coming Soon!",
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center)
            MessageScreenContent(navController = navController)
        }
    }
}

@Composable
fun MessageScreenContent(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.chat),
            contentDescription = "No Notifications Image",
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Chat With the Drivers",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Chat with the drivers on the app itself." +
                    "\n Quick, simple and secure.",
            color = Color(0xFF667085),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick =
        {
            navController.navigate(HourDriveScreens.HomeScreen.name)
            Toast.makeText(context, "You will be notified!", Toast.LENGTH_SHORT).show()
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A62B7)),
        )
        {
            Text(text = "Notify Me")
        }
    }
}



@Preview
@Composable
fun MessageScreenPreview() {
    val navController = rememberNavController()
    MessageScreen(navController = navController, userViewModel = UserViewModel())
}
