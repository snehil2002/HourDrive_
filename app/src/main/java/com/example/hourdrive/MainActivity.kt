package com.example.hourdrive
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.hourdrive.model.getCurrentLocationAndStore
import com.example.hourdrive.navigation.navigation
import com.example.hourdrive.screens.HourDriveScreens
import com.example.hourdrive.ui.theme.HourDriveTheme
import com.example.hourdrive.viewmodel.AuthViewmodel
import com.example.hourdrive.viewmodel.PaymentViewModel

import com.example.hourdrive.viewmodel.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.location.LocationServices
import com.razorpay.PaymentResultListener

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity(), PaymentResultListener {

    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var authViewModel: AuthViewmodel




    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewmodel::class.java]




                setContent {
                    HourDriveTheme(darkTheme = false, dynamicColor = false) {
                        val navController = rememberNavController()

                        navigation(navController, authViewModel, paymentViewModel = paymentViewModel)

                        val paymentSuccess = paymentViewModel.paymentSuccess.observeAsState()

                        if (paymentSuccess.value == true) {
                            navController.navigate(HourDriveScreens.HomeScreen.name) {
                                popUpTo(HourDriveScreens.HomeScreen.name) { inclusive = true }
                            }
                            paymentViewModel.resetPaymentSuccess()
                        }
                    }
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    LaunchedEffect(Unit) {
                        while (true){
                            delay(4000)
                          getCurrentLocationAndStore(fusedLocationClient, this@MainActivity)

                        } }


                }


    }



    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        paymentViewModel.onPaymentSuccess(razorpayPaymentId)
        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        paymentViewModel.onPaymentError(code, response)
        Toast.makeText(this, "Error in payment: $response", Toast.LENGTH_LONG).show()
    }

}
