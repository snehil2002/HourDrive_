package com.example.hourdrive.screens.screencomponents
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hourdrive.R
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.Color
import com.example.hourdrive.viewmodel.Car


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarCard(car: Car, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = car.profilePicture),
                contentDescription = "Profile Picture of ${car.name}",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = car.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Car Model: ${car.carModel}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight= FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${car.distance} km away from you",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Driver Icon",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CarCardPreview() {
    val car = Car(
        profilePicture = R.drawable.car,
        name = "John Smith",
        distance = "0.5",
        carModel = "Toyota Camry"
    )
    CarCard(car = car, onClick = {})
}