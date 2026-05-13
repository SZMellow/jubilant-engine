package com.example.mapaani3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun FarmerMain(onExit: () -> Unit) {
    var selectedTab by remember { mutableStateOf("home") }
    var currentFarmerScreen by remember { mutableStateOf("selling_list") }
    val draftListing = remember { mutableStateListOf<Product>() }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var pickupAddress by remember { mutableStateOf("778 Locust View Drive Oakland, CA") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    "home" -> HomeScreen(onProductClick = { selectedProduct = it })
                    "selling_list" -> {
                        when (currentFarmerScreen) {
                            "selling_list" -> FarmerSellingListScreen(
                                onAddClick = { currentFarmerScreen = "add_crop" }
                            )
                            "add_crop" -> AddCropScreen(
                                onBack = { currentFarmerScreen = "selling_list" },
                                onNext = { product ->
                                    draftListing.clear()
                                    draftListing.add(product)
                                    currentFarmerScreen = "confirm_listing"
                                }
                            )
                            "confirm_listing" -> ConfirmListingScreen(
                                drafts = draftListing,
                                address = pickupAddress,
                                onAddressChange = { pickupAddress = it },
                                onBack = { currentFarmerScreen = "add_crop" },
                                onConfirm = { currentFarmerScreen = "take_photo" }
                            )
                            "take_photo" -> {
                                val repository = remember { AppRepository() }
                                val scope = rememberCoroutineScope()
                                
                                TakeProductPhotoScreen(
                                    onBack = { currentFarmerScreen = "confirm_listing" },
                                    onTakePhoto = { 
                                        scope.launch {
                                            draftListing.forEach { product ->
                                                repository.addProduct(product)
                                            }
                                            currentFarmerScreen = "success"
                                        }
                                    }
                                )
                            }
                            "success" -> ListingSuccessScreen(
                                onFinished = { 
                                    draftListing.clear()
                                    currentFarmerScreen = "selling_list" 
                                }
                            )
                        }
                    }
                    "orders" -> FarmerOrdersScreen()
                    "settings" -> FarmerSettingsScreen(onExit = onExit)
                }
            }
            
            if (currentFarmerScreen != "success") {
                FarmerBottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { 
                        selectedTab = it
                        currentFarmerScreen = "selling_list" // Reset flow if switching tabs
                    }
                )
            }
        }

        // Overlay Detail Screen if a product is clicked in Home
        selectedProduct?.let { product ->
            ProductDetailScreen(
                product = product,
                onBack = { selectedProduct = null },
                onAddToCart = { kilos ->
                    CartManager.addProduct(product, kilos)
                    selectedProduct = null
                }
            )
        }
    }
}

@Composable
fun FarmerOrdersScreen() {
    val repository = remember { AppRepository() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("Active") }
    val visibleTabs = listOf("Active", "Completed", "Cancelled")
    
    var orders by remember { mutableStateOf(emptyList<Order>()) }
    
    LaunchedEffect(UserSession.currentUserId) {
        UserSession.currentUserId?.let { userId ->
            repository.getFarmerOrders(userId).collect {
                orders = it
            }
        }
    }

    val filteredOrders = orders.filter { 
        it.status == selectedTab
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Product Orders",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                // Status Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    visibleTabs.forEach { tab ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab }
                        ) {
                            Text(
                                text = tab,
                                color = if (selectedTab == tab) colorResource(id = R.color.green1) else Color.Gray,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                            if (selectedTab == tab) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(width = 40.dp, height = 2.dp)
                                        .background(colorResource(id = R.color.green1))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (filteredOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No $selectedTab orders yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredOrders) { order ->
                            FarmerOrderItem(order, onStatusUpdate = { newStatus ->
                                scope.launch {
                                    repository.updateOrderStatus(order.id, newStatus)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FarmerOrderItem(order: Order, onStatusUpdate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green2A).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Order #${order.id.takeLast(6)}", fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green1))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            order.items.forEach { cartItem ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${cartItem.product.name} (${cartItem.quantityKilos} kg)", fontSize = 14.sp)
                    Text(text = "P${String.format("%.2f", cartItem.product.price * cartItem.quantityKilos)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.3f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Earned:", fontWeight = FontWeight.Bold)
                Text(
                    text = "P${String.format("%.2f", order.totalPrice - order.deliveryFee)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Deliver at: ${order.deliveryTime}", fontSize = 12.sp, color = colorResource(id = R.color.green2), fontWeight = FontWeight.Bold)
            
            if (order.status == "Active") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onStatusUpdate("Completed") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mark as Delivered")
                }
            }
        }
    }
}

@Composable
fun FarmerSettingsScreen(onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exit / Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FarmerSellingListScreen(
    onAddClick: () -> Unit,
    isVerified: Boolean = UserSession.isUserVerified,
    repository: AppRepository = remember { AppRepository() }
) {
    var products by remember { mutableStateOf(emptyList<Product>()) }
    var currentVerificationStatus by remember { mutableStateOf(isVerified) }
    
    LaunchedEffect(UserSession.currentUserId) {
        UserSession.currentUserId?.let { userId ->
            // Listen for product changes
            repository.getFarmerProducts(userId).collect {
                products = it
            }
        }
    }

    // New: Listen for verification status changes in real-time
    LaunchedEffect(UserSession.currentUserId) {
        UserSession.currentUserId?.let { userId ->
            repository.getUserByIdStream(userId).collect { user ->
                user?.let {
                    UserSession.isUserVerified = it.isVerified
                    currentVerificationStatus = it.isVerified
                }
            }
        }
    }

    val scope = rememberCoroutineScope()
    
    FarmerSellingListContent(
        products = products,
        isVerified = currentVerificationStatus,
        onAddClick = onAddClick,
        onDeleteProduct = { productId ->
            scope.launch {
                repository.deleteProduct(productId)
            }
        }
    )
}

@Composable
fun FarmerSellingListContent(
    products: List<Product>,
    isVerified: Boolean,
    onAddClick: () -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.green2))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
        ) {
            Text(
                text = "Selling List",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color.White
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (products.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.green2).copy(alpha = 0.8f)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Your selling list is empty",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(40.dp))
                            
                            IconButton(
                                onClick = if (isVerified) onAddClick else ({}),
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .padding(8.dp)
                                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Want To Add\nSomething?",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current Listings",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.green1)
                                    )
                                    TextButton(onClick = if (isVerified) onAddClick else ({})) {
                                        Text("+ Add More", color = if (isVerified) colorResource(id = R.color.green2) else Color.Gray)
                                    }
                                }
                            }
                            items(products) { product ->
                                ListingSummaryItem(
                                    name = product.name,
                                    date = "Today",
                                    price = "P${String.format("%.2f", product.price)}",
                                    quantity = "${product.kilos} kg",
                                    imageRes = product.imageRes,
                                    isDone = product.kilos <= 0,
                                    onCancel = {
                                        if (isVerified) {
                                            onDeleteProduct(product.id)
                                        }
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }

                    // Verification Overlay
                    if (!isVerified) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Gray.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Account Verification Pending",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your selling capabilities are disabled while we verify your identity. This usually takes 24-48 hours.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AddCropScreen(onBack: () -> Unit, onNext: (Product) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var kilos by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.ROOTS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Add New Crop",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Crop Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price per Kilo (P)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = kilos,
                    onValueChange = { kilos = it },
                    label = { Text("Available Kilos") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Select Category", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ProductCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName, fontSize = 10.sp) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        if (name.isNotEmpty() && price.isNotEmpty()) {
                            val newProduct = Product(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                price = price.toDoubleOrNull() ?: 0.0,
                                kilos = kilos.toDoubleOrNull() ?: 1.0,
                                description = if (description.isNotBlank()) description else "Freshly harvested ${name}.",
                                category = selectedCategory,
                                imageRes = when(selectedCategory) {
                                    ProductCategory.ROOTS -> R.drawable.roots
                                    ProductCategory.LEAFY -> R.drawable.leafy
                                    ProductCategory.BEANS -> R.drawable.beans
                                    ProductCategory.GOURDS -> R.drawable.gourd
                                },
                                farmerId = UserSession.currentUserId,
                                farmerName = null // Will be handled by repository if needed
                            )
                            onNext(newProduct)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green2)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Review Listing", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun ConfirmListingScreen(drafts: List<Product>, address: String, onAddressChange: (String) -> Unit, onBack: () -> Unit, onConfirm: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Confirm Listing",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Pickup Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f))
                ) {
                    TextField(
                        value = address,
                        onValueChange = onAddressChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = colorResource(id = R.color.green1)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colorResource(id = R.color.green1), modifier = Modifier.size(16.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Listing Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.green1)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(drafts) { product ->
                        ListingSummaryItem(
                            name = product.name,
                            date = "Ready to list",
                            price = "P${product.price}",
                            quantity = "${product.kilos} kg",
                            imageRes = product.imageRes
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green2)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(text = "Confirm Listing", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun ListingSummaryItem(name: String, date: String, price: String, quantity: String, imageRes: Int, isDone: Boolean = false, onCancel: () -> Unit = {}) {
    var currentName by remember { mutableStateOf(name) }
    var currentPrice by remember { mutableStateOf(price) }
    var showEditDialog by remember { mutableStateOf(false) }

    val safeImageRes = remember(name) {
        getDrawableForProductName(name, R.drawable.logo)
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Listing") },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentName,
                        onValueChange = { currentName = it },
                        label = { Text("Product Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentPrice,
                        onValueChange = { currentPrice = it },
                        label = { Text("Price") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    // currentName and currentPrice are already updated by states
                    showEditDialog = false 
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isDone) 0.5f else 1.0f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = safeImageRes),
            contentDescription = currentName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = currentName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = date, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            if (!isDone) {
                Surface(
                    color = colorResource(id = R.color.green2).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.clickable { onCancel() }
                ) {
                    Text(
                        text = "Cancel Listing",
                        fontSize = 10.sp,
                        color = colorResource(id = R.color.green2),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            } else {
                Text(
                    text = "Sold Out",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(text = currentPrice, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green1))
            Text(text = quantity, fontSize = 12.sp, color = Color.Gray)
            if (!isDone) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit", 
                        modifier = Modifier.size(16.dp).clickable { showEditDialog = true }, 
                        tint = colorResource(id = R.color.green1)
                    )
                }
            }
        }
    }
}

@Composable
fun TakeProductPhotoScreen(onBack: () -> Unit, onTakePhoto: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Listing Confirmation",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "To confirm your listings legitimacy, post a photo of the product.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(60.dp))
                
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = onTakePhoto,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green2)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(text = "Take photo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun ListingSuccessScreen(onFinished: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFinished) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorResource(id = R.color.green1))
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, colorResource(id = R.color.green1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(colorResource(id = R.color.green1), CircleShape)
                        .align(Alignment.Center)
                        .offset(x = (-5).dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Listing Confirmed!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green1)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your listing has been successfully posted!",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(100.dp))
            
            Text(
                text = "If you have any questions, please reach out directly to our customer support",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 48.dp)
            )
        }
    }
}

@Composable
fun FarmerBottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = colorResource(id = R.color.green2),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("home", Icons.Default.Home, Icons.Outlined.Home),
                Triple("selling_list", Icons.Default.ShoppingBasket, Icons.Outlined.ShoppingBasket),
                Triple("orders", Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Filled.Assignment),
                Triple("settings", Icons.Default.Settings, Icons.Outlined.Settings)
            )
            
            tabs.forEach { (tag, filledIcon, outlinedIcon) ->
                IconButton(onClick = { onTabSelected(tag) }) {
                    Icon(
                        imageVector = if (selectedTab == tag) filledIcon else outlinedIcon,
                        contentDescription = tag,
                        tint = if (selectedTab == tag) Color.White else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmerOrderItemPreview() {
    MapaAni3Theme {
        FarmerOrderItem(
            order = Order(
                id = "ORD-123456789",
                items = listOf(
                    CartItem(
                        product = Product("1", "Carrots", 25.0, ProductCategory.ROOTS, R.drawable.carrots),
                        quantityKilos = 5.0
                    )
                ),
                totalPrice = 127.5, // 125 + 2.5 (2%)
                deliveryFee = 2.5,
                deliveryTime = "10:00 AM",
                date = "May 07, 2026",
                status = "Active"
            ),
            onStatusUpdate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FarmerSellingListUnverifiedPreview() {
    MapaAni3Theme {
        FarmerSellingListContent(
            products = emptyList(),
            isVerified = false,
            onAddClick = {},
            onDeleteProduct = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FarmerSellingListVerifiedPreview() {
    MapaAni3Theme {
        FarmerSellingListContent(
            products = listOf(
                Product("1", "Carrots", 25.0, ProductCategory.ROOTS, R.drawable.carrots, kilos = 50.0)
            ),
            isVerified = true,
            onAddClick = {},
            onDeleteProduct = {}
        )
    }
}

