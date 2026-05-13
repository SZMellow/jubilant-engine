package com.example.mapaani3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeightSelector(
    maxKilos: Double,
    selectedKilos: Double,
    onWeightChange: (Double) -> Unit
) {
    var textValue by remember(selectedKilos) {
        val displayValue = if (selectedKilos == selectedKilos.toInt().toDouble()) {
            selectedKilos.toInt().toString()
        } else {
            selectedKilos.toString()
        }
        mutableStateOf(displayValue)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { if (selectedKilos > 1) onWeightChange(selectedKilos - 1) },
            modifier = Modifier.background(colorResource(id = R.color.green2).copy(alpha = 0.1f), CircleShape)
        ) {
            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green2))
        }

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                textValue = newValue
                val doubleValue = newValue.toDoubleOrNull()
                if (doubleValue != null && doubleValue <= maxKilos && doubleValue >= 0) {
                    onWeightChange(doubleValue)
                } else if (newValue.isEmpty()) {
                    onWeightChange(0.0)
                }
            },
            modifier = Modifier.width(100.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            suffix = { Text("kg") }
        )

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { if (selectedKilos < maxKilos) onWeightChange(selectedKilos + 1) },
            modifier = Modifier.background(colorResource(id = R.color.green2).copy(alpha = 0.1f), CircleShape)
        ) {
            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green2))
        }
    }
}

@Composable
fun ProductDetailScreen(product: Product, onBack: () -> Unit, onAddToCart: (Double) -> Unit) {
    var selectedKilos by remember(product.id) { mutableStateOf(1.0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Consumes click to prevent leakage to background */ }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image Header
            Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Top Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.green1)
                    )
                    Text(
                        text = "Farmer: ${product.farmerName ?: "Local Farmer"}",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.green2),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "P${String.format("%.2f", product.price * selectedKilos)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.green2)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Available: ${product.kilos} kg",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.green1)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (UserSession.currentUserType == UserType.BUYER) {
                    Text(
                        text = "Select Quantity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.green1)
                    )
                    WeightSelector(
                        maxKilos = product.kilos,
                        selectedKilos = selectedKilos,
                        onWeightChange = { selectedKilos = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                if (UserSession.currentUserType == UserType.BUYER) {
                    Button(
                        onClick = { onAddToCart(selectedKilos) },
                        enabled = selectedKilos > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.green2)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Add to Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
