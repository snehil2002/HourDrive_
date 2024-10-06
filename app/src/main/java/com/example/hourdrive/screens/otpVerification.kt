package com.example.hourdrive.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourdrive.model.MUser
import com.example.hourdrive.viewmodel.AuthState
import com.example.hourdrive.viewmodel.AuthViewmodel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun OtpVerificationScreen(navController: NavController, authViewmodel: AuthViewmodel) {
    var otp by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    val authState by authViewmodel.authState.collectAsState()
    var userid by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.CodeSent -> { /* handle CodeSent state */ }
            is AuthState.NewUser -> {
                userid = (authState as AuthState.NewUser).userId
                showBottomSheet = true

                // Show bottom sheet to complete profile
            }
            is AuthState.ExistingUser -> {

                navController.navigate(HourDriveScreens.SplashScreen.name) {
                    popUpTo(HourDriveScreens.OtpScreen.name) { inclusive = true }

                }
                Toast.makeText(navController.context, "Welcome back!", Toast.LENGTH_SHORT).show()
            }
            is AuthState.ProfileCompleted -> {
                Toast.makeText(navController.context, "Profile completed successfully", Toast.LENGTH_SHORT).show()
                showBottomSheet=false
                navController.navigate(HourDriveScreens.SplashScreen.name) {
                    popUpTo(HourDriveScreens.OtpScreen.name) { inclusive = true }

                }

            }
            is AuthState.Error -> {
                Toast.makeText(navController.context, "Error: ${(authState as AuthState.Error).message}", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }


    // Predefined valid OTP for verification
//    val validOtp = "12345" // Replace with the actual OTP from your backend
if (authViewmodel.loading.value){
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth().height(5.dp),
        color = MaterialTheme.colorScheme.primary
    )
}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter OTP",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "A one-time password (OTP) has been sent to your phone number. Please enter it below.",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // OTP Input Field
        TextField(
            value = otp,
            onValueChange = { otp = it },
            placeholder = { Text("Enter OTP") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Submit Button
        Button(
            onClick = {
                when (authState) {
                    is AuthState.CodeSent -> {
                        if (otp.isNotEmpty()) {
                            authViewmodel.verifyCode((authState as AuthState.CodeSent).verificationId, otp)
                        } else {
                            Toast.makeText(navController.context, "Please enter the OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is AuthState.NewUser -> {
                        userid = (authState as AuthState.NewUser).userId
                        showBottomSheet = true

                        // Show bottom sheet to complete profile
                    }
                    is AuthState.ExistingUser -> {navController.navigate(HourDriveScreens.SplashScreen.name) {
                        popUpTo(HourDriveScreens.OtpScreen.name) { inclusive = true }

                    }
                        Toast.makeText(navController.context, "Welcome back!", Toast.LENGTH_SHORT).show()
                    }
                    is AuthState.ProfileCompleted -> {
                        Toast.makeText(navController.context, "Profile completed successfully", Toast.LENGTH_SHORT).show()
                        showBottomSheet=false
                    }
                    is AuthState.Error -> {
                        Toast.makeText(navController.context, "Error: ${(authState as AuthState.Error).message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(navController.context, "Unknown Error", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Submit", color = Color.White)
        }

        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheetContent(onDismiss = { showBottomSheet = false }, authViewmodel = authViewmodel, userid = userid)
        }
    }
}

@Composable
fun BottomSheetContent(onDismiss: () -> Unit, authViewmodel: AuthViewmodel, userid: String) {
    var driver by remember { mutableStateOf(false) } // Default to "Customer"
    var licenseNumber by remember { mutableStateOf("") }
    var vehicleRegistrationNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var carDetails by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .clip(shape = MaterialTheme.shapes.medium)
            .verticalScroll(rememberScrollState()), // Enable scrolling
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Complete Your Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Enter Your Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Select User Type", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Radio Buttons for User Type Selection
        Row(modifier = Modifier.padding(vertical = 5.dp)) {
            RadioButton(
                selected = driver == false,
                onClick = { driver = false }
            )
            Text(text = "Customer", modifier = Modifier.padding(start = 2.dp).align(Alignment.CenterVertically))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = driver == true,
                onClick = { driver = true }
            )
            Text(text = "Driver", modifier = Modifier.padding(start = 2.dp).align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Conditional Input Fields
        if (driver) {
            TextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                placeholder = { Text("Enter License Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        } else {
            TextField(
                value = vehicleRegistrationNumber,
                onValueChange = { vehicleRegistrationNumber = it },
                placeholder = { Text("Enter Vehicle Registration Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
            TextField(
                value = carDetails,
                onValueChange = { carDetails = it },
                placeholder = { Text("Enter Car Details") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Enter Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Enter Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = aadhaarNumber,
            onValueChange = { aadhaarNumber = it },
            placeholder = { Text("Enter Your Aadhaar Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Generate timestamp
                val timestamp = System.currentTimeMillis()

                // Create a SimpleDateFormat instance with the desired format
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                // Convert the timestamp to Date
                val date = Date(timestamp)

                // Format the date into the desired string format
                val formattedDate = sdf.format(date)// Or format to a readable date
                // Handle submission of data here (e.g., save to database, send to server, etc.)
                authViewmodel.completeUserProfile(MUser(
                    uid = userid,
                    phoneNumber = phoneNumber,
                    name = name,
                    email = email,
                    createdAt = formattedDate,
                    driver = driver,
                    profileComplete = true,
                    adhaar = aadhaarNumber,
                    vehicleRegistrationNumber = vehicleRegistrationNumber,
                    driverLicenseNumber = licenseNumber,
                    carDetails = carDetails
                ), userid)

                // For now, just dismiss the bottom sheet

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Done", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

