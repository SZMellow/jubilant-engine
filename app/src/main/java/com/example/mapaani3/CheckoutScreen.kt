package com.example.mapaani3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(onBack: () -> Unit, onOrderPlaced: () -> Unit) {
    val repository = remember { AppRepository() }
    val scope = rememberCoroutineScope()

    var selectedTime by remember { mutableStateOf("Morning (8AM - 11AM)") }
    val deliveryTimes = listOf(
        "Morning (8AM - 11AM)",
        "Afternoon (1PM - 4PM)",
        "Evening (5PM - 8PM)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Checkout",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Delivery Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                deliveryTimes.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedTime == time) colorResource(id = R.color.green2).copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                            .clickable { selectedTime = time }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTime == time,
                            onClick = { selectedTime = time },
                            colors = RadioButtonDefaults.colors(selectedColor = colorResource(id = R.color.green2))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = time, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Order Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(CartManager.items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = item.product.imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.product.name, fontWeight = FontWeight.Bold)
                                Text(text = "${item.quantityKilos} kg", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(text = "P${String.format("%.2f", item.product.price * item.quantityKilos)}", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                val subtotal = CartManager.items.sumOf { it.product.price * it.quantityKilos }
                val deliveryFee = subtotal * 0.02
                val total = subtotal + deliveryFee

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Subtotal", fontSize = 16.sp)
                    Text(text = "P${String.format("%.2f", subtotal)}", fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Delivery Fee (2%)", fontSize = 16.sp)
                    Text(text = "P${String.format("%.2f", deliveryFee)}", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Amount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "P${String.format("%.2f", total)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.green2)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            val buyerId = UserSession.currentUserId ?: ""
                            if (buyerId.isNotEmpty()) {
                                repository.placeOrder(CartManager.items, selectedTime, buyerId)
                                CartManager.clear()
                                onOrderPlaced()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green2)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(text = "Confirm Booking", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    MapaAni3Theme {
        CheckoutScreen(onBack = {}, onOrderPlaced = {})
    }
}
