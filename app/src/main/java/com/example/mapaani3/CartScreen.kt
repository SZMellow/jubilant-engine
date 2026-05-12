package com.example.mapaani3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CartScreen(onProductClick: (Product) -> Unit, onCheckout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "My Cart",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Content Card
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            if (CartManager.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.transfer_document_icon),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Your cart is empty",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.green1)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(CartManager.items) { item ->
                            CartItemRow(item, onProductClick)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val total = CartManager.items.sumOf { it.product.price * it.quantityKilos }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "P${String.format("%.2f", total)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.green2)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.green2)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onProductClick: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(item.product) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.product.imageRes),
                contentDescription = item.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
                Text(
                    text = "${item.quantityKilos} kg",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.green2),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "P${String.format("%.2f", item.product.price * item.quantityKilos)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            IconButton(onClick = { CartManager.removeProduct(item.product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}
