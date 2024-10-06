import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.hourdrive.screens.HourDriveScreens
import com.example.hourdrive.viewmodel.AuthViewmodel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginScreen(navController: NavController, authViewmodel: AuthViewmodel) {
    val context = LocalContext.current
    val activity = context as Activity
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var isTextFieldFocused by remember { mutableStateOf(false) }
if (authViewmodel.loading.value){
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary
    )
}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5F8))
            .padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Welcome Header
        Text(
            text = "Welcome to HourDrive",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color(0xFF333333)
        )

        // Description Text
        Text(
            text = "This number will be used for all ride-related communication. You shall receive an SMS with a code for verification.",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp)
        )

        // Phone Number Label
        Text(
            text = "Phone Number",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(bottom = 8.dp)
        )

        // Phone Number Input Field
       TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Enter 10 digit number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        // Next Button
        Button(
            onClick = {
                if (phoneNumber.text.isNotEmpty()) {
                    authViewmodel.sendVerificationCode("+91" + phoneNumber.text.trim(), activity)
                    navController.navigate(HourDriveScreens.OtpScreen.name) {
                        popUpTo(HourDriveScreens.LoginScreen.name) { inclusive = true }

                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Next",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terms and Conditions
        Text(
            text = "By continuing, you agree to our Terms and Conditions and Privacy Policy. " +
                    "Please ensure that you have read and understood these documents before proceeding. " +
                    "Your privacy and security are our top priority.",
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    // You can preview this screen without navigation or ViewModel here
    loginScreen(navController = NavController(LocalContext.current), authViewmodel = AuthViewmodel())
}
