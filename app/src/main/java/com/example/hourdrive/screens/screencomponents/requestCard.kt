package com.example.hourdrive.screens.screencomponents

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hourdrive.model.RideRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dsRequestCard(request: RideRequest, onClick: () -> Unit) {
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


            Column(modifier = Modifier.weight(1f)) {
                Row(){
                Text(
                    text = "${request.startLocation}  ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "-->  ${request.endLocation} ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )}
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹ ${request.money}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${request.customerName}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${request.customerNumber}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = request.status,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 15.sp,
                    color = Color.Red
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
@Composable
fun csRequestCard(request: RideRequest, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Column(modifier = Modifier) {
                Row(){
                    Text(
                        text = "${request.startLocation}  ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "-->  ${request.endLocation} ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )}
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹ ${request.money}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${request.driverName}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${request.driverNumber}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = request.status,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 15.sp,
                    color = Color.Red
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
    val rr=RideRequest(
        uid="23114343", customerId = "dsf44tw43r44", driverId = "ewrw4rw4rw4e3", startLocation = "bareilly",
        endLocation= "ghaziabad",money="1000", status = "pending", driverNumber = "123322322", customerNumber = "123443333",
        driverName = "asdsdasdaax sxa" , customerName = "axssxscscsc"
    )
    dsRequestCard(request = rr, onClick = {})
}