package com.example.hourdrive.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hourdrive.screens.*
import com.example.hourdrive.viewmodel.AuthViewmodel
import com.example.hourdrive.viewmodel.CustomerSideViewmodel
import com.example.hourdrive.viewmodel.DriverSideViewmodel
import com.example.hourdrive.viewmodel.PaymentViewModel
import com.example.hourdrive.viewmodel.UserViewModel
import loginScreen

@Composable
fun navigation(navController: NavHostController, authViewmodel: AuthViewmodel,
               paymentViewModel: PaymentViewModel) {
    val userViewModel= viewModel<UserViewModel>()
    val customerSideViewmodel= viewModel<CustomerSideViewmodel>()
    val driverSideViewmodel= viewModel<DriverSideViewmodel>()
    NavHost(
        navController = navController,

        startDestination = HourDriveScreens.SplashScreen.name

    ) {
        composable(route = HourDriveScreens.LoginScreen.name) {
            loginScreen(navController = navController, authViewmodel = authViewmodel)
        }
        composable(route = HourDriveScreens.OtpScreen.name) {
            OtpVerificationScreen(navController = navController, authViewmodel = authViewmodel)
        }
        composable(route = HourDriveScreens.HomeScreen.name) {
            homeScreen(navController = navController, userViewModel = userViewModel,
                customerSideViewmodel = customerSideViewmodel, paymentViewModel = paymentViewModel)
        }
        composable(route = HourDriveScreens.SplashScreen.name) {
            splashScreen(navController = navController,userViewModel)
        }
        composable(route = HourDriveScreens.CarsHomeScreen.name) {
            carHomeScreen(navController = navController,userViewModel,driverSideViewmodel)
        }
        composable(route = HourDriveScreens.ProfileScreen.name) {
            ProfileScreen(navController = navController, userViewModel)
        }
        composable(route = HourDriveScreens.DriverBookingScreen.name) {
            DriverBookingScreen(navController = navController, customerSideViewmodel = customerSideViewmodel,
                userViewModel = userViewModel)
        }
        composable(route = HourDriveScreens.CustomerPastBookingScreen.name) {
            customerPastBookingScreen(navController = navController,userViewModel,customerSideViewmodel)
        }
        composable(route = HourDriveScreens.DriverPastBookingScreen.name) {
            driverPastBookingScreen(navController = navController,userViewModel,driverSideViewmodel)
        }

        composable(route=HourDriveScreens.NotificationScreen.name){
            NotificationScreen(navController = navController)
        }
        composable(route=HourDriveScreens.MessagesScreen.name){
            MessageScreen(navController = navController,userViewModel)
        }

        composable(route = HourDriveScreens.SettingScreen.name) {
            SettingScreen(navController = navController)
        }

        composable(HourDriveScreens.CarBookingScreen.name+"/{requestId}",
            arguments = listOf(navArgument(name = "requestId"){type= NavType.StringType})){

            CarBookingScreen(navController = navController, driverSideViewmodel = driverSideViewmodel,
                it.arguments?.getString("requestId").toString()
            )

        }
    }
}
