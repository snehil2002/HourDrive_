package com.example.hourdrive.screens.screencomponents
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hourdrive.R
import com.example.hourdrive.model.MUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverCard(mUser: MUser, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.upfp),
                contentDescription = "Profile Picture of ${mUser.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mUser.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${mUser.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mUser.status,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 15.sp,
                    color = Color(0xFF038A3B)

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
private fun DriverCardPreview() {
    val driver = MUser(
        uid = "1",
        driverProfilePicture = R.drawable.pfp,
        name = "John Smith",
        distance = "0.5",
        status = "Available",
        rating = 4.5
    )
    DriverCard(mUser = driver, onClick = {})
}