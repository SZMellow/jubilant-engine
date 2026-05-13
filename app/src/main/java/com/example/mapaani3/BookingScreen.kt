package com.example.mapaani3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BookingScreen(
    viewModel: BookingViewModel = viewModel()
) {
    val productId by viewModel.productId.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Draft Order", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = productId,
            onValueChange = { viewModel.onProductIdChange(it) },
            label = { Text("Product ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { viewModel.onQuantityChange(it) },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { /* Proceed to checkout */ }) {
            Text("Book Now")
        }
        
        Text(
            text = "Note: Data entered here persists even if the app process is killed by the OS.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
