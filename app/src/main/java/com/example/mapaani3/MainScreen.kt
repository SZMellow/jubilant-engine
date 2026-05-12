package com.example.mapaani3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

sealed class BottomNavItem(val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home)
    object Cart : BottomNavItem("Cart", Icons.Outlined.ShoppingCart, Icons.Filled.ShoppingCart)
    object Bookmarks : BottomNavItem("Bookmarks", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite)
    object History : BottomNavItem("History", Icons.Outlined.ListAlt, Icons.Filled.ListAlt)
    object FAQ : BottomNavItem("FAQ", Icons.Outlined.HeadsetMic, Icons.Filled.HeadsetMic)
}

@Composable
fun MainScreen(onExit: () -> Unit, modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf(0) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var currentScreen by remember { mutableStateOf("main") } // main, checkout

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Cart,
        BottomNavItem.Bookmarks,
        BottomNavItem.History,
        BottomNavItem.FAQ
    )

    val cartBadgeCount by remember {
        derivedStateOf { CartManager.items.size }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentScreen == "checkout") {
            CheckoutScreen(
                onBack = { currentScreen = "main" },
                onOrderPlaced = {
                    currentScreen = "main"
                    selectedItem = 3 // Go to History
                }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = colorResource(id = R.color.green2),
                        contentColor = Color.White
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    val badgeCount = if (item == BottomNavItem.Cart) cartBadgeCount else 0
                                    Box {
                                        Icon(
                                            if (selectedItem == index) item.selectedIcon else item.icon,
                                            contentDescription = item.title,
                                            tint = Color.White
                                        )
                                        if (badgeCount > 0) {
                                            Badge(
                                                modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp),
                                                containerColor = Color.Red,
                                                contentColor = Color.White
                                            ) {
                                                Text(badgeCount.toString(), fontSize = 10.sp)
                                            }
                                        }
                                    }
                                },
                                selected = selectedItem == index,
                                onClick = { 
                                    selectedItem = index 
                                    selectedProduct = null // Reset details view when switching tabs
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                ContentScreen(
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    selectedIndex = selectedItem,
                    onProductClick = { selectedProduct = it },
                    onCheckout = { currentScreen = "checkout" },
                    onExit = onExit
                )
            }
        }

        // Overlay Detail Screen
        selectedProduct?.let { product ->
            ProductDetailScreen(
                product = product,
                onBack = { selectedProduct = null },
                onAddToCart = { kilos ->
                    CartManager.addProduct(product, kilos)
                    selectedProduct = null
                    selectedItem = 1 // Switch to Cart tab
                }
            )
        }
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier, 
    selectedIndex: Int, 
    onProductClick: (Product) -> Unit,
    onCheckout: () -> Unit,
    onExit: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (selectedIndex) {
            0 -> HomeScreen(onProductClick = onProductClick)
            1 -> CartScreen(onProductClick = onProductClick, onCheckout = onCheckout)
            2 -> BookmarksScreen(onProductClick = onProductClick)
            3 -> OrdersScreen()
            4 -> FAQScreen(onExit = onExit)
            else -> Text(text = "Page $selectedIndex")
        }
    }
}
