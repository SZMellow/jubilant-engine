package com.example.mapaani3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun AdminScreen(onExit: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Audit Log", "User Management")
    val repository = remember { AppRepository() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Admin Dashboard",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onExit) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit", tint = Color.White)
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.White
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            when (selectedTab) {
                0 -> AuditLogSection(repository)
                1 -> UserManagementSection(repository)
            }
        }
    }
}

@Composable
fun AuditLogSection(repository: AppRepository) {
    val orders by repository.getAllOrders().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green2A).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Order #${order.id.takeLast(6)}", fontWeight = FontWeight.Bold)
                        if (order.priorityLevel > 1) {
                            Surface(
                                color = Color.Red,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "PRIORITY",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(text = "Buyer ID: ${order.buyerId}", fontSize = 12.sp, color = Color.Gray)
                    if (order.notes.isNotEmpty()) {
                        Text(text = "Notes: ${order.notes}", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    }
                    Text(text = "Total: P${String.format("%.2f", order.totalPrice)}", fontWeight = FontWeight.SemiBold)
                    Text(text = "Status: ${order.status}", color = colorResource(id = R.color.green2))
                }
            }
        }
    }
}

@Composable
fun UserManagementSection(repository: AppRepository) {
    val farmers by repository.getFarmers().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(farmers) { farmer ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = farmer.name, fontWeight = FontWeight.Bold)
                        Text(text = farmer.email, fontSize = 12.sp, color = Color.Gray)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Verified", fontSize = 12.sp)
                            Switch(
                                checked = farmer.isVerified,
                                onCheckedChange = { verified ->
                                    scope.launch {
                                        repository.updateUserStatus(farmer.id, verified, farmer.isActive)
                                    }
                                }
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Active", fontSize = 12.sp)
                            Switch(
                                checked = farmer.isActive,
                                onCheckedChange = { active ->
                                    scope.launch {
                                        repository.updateUserStatus(farmer.id, farmer.isVerified, active)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
