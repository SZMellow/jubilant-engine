package com.example.mapaani3

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme

import androidx.compose.material.icons.filled.Add
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen(onProductClick: (Product) -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    // Sync from DB
    LaunchedEffect(Unit) {
        db.productDao().getAllProducts().collect { entities ->
            if (entities.isEmpty()) {
                // Seed database if empty
                val initialProducts = SampleData.products.map { p ->
                    ProductEntity(
                        name = p.name,
                        price = p.price,
                        kilos = p.kilos,
                        category = p.category.name,
                        description = p.description,
                        rating = p.rating,
                        imageRes = p.imageRes,
                        farmerName = "Sample Farmer",
                        isBestSeller = p.isBestSeller,
                        isRecommended = p.isRecommended
                    )
                }
                db.productDao().insertProducts(initialProducts)
            } else {
                ProductRepository.loadProducts(entities.map { e ->
                    Product(
                        id = e.id.toString(),
                        name = e.name,
                        price = e.price,
                        category = ProductCategory.valueOf(e.category),
                        imageRes = e.imageRes,
                        kilos = e.kilos,
                        description = e.description,
                        rating = e.rating,
                        isBestSeller = e.isBestSeller,
                        isRecommended = e.isRecommended
                    )
                })
            }
        }
    }

    val filteredProducts = remember(searchQuery, selectedCategory, ProductRepository.allProducts.size) {
        ProductRepository.allProducts.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || product.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val bestSellers = remember(filteredProducts) { filteredProducts.filter { it.isBestSeller } }
    val recommended = remember(filteredProducts) { filteredProducts.filter { it.isRecommended } }
    val others = remember(filteredProducts) { filteredProducts.filter { !it.isBestSeller && !it.isRecommended } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.green2))
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            HomeHeader(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it }
            )

            // Content Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
                    // Category Row
                    CategoryRow(
                        selectedCategory = selectedCategory,
                        onCategorySelect = { category ->
                            selectedCategory = if (selectedCategory == category) null else category
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (bestSellers.isNotEmpty()) {
                        // Best Sellers
                        SectionHeader(title = "Best Sellers", onViewAll = {})
                        BestSellersRow(items = bestSellers, onProductClick = onProductClick)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Promo Banner
                    PromoBanner()

                    Spacer(modifier = Modifier.height(24.dp))

                    if (recommended.isNotEmpty()) {
                        // Recommended
                        SectionHeader(title = "Recommended", onViewAll = {})
                        RecommendedGrid(items = recommended, onProductClick = onProductClick)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (others.isNotEmpty()) {
                        SectionHeader(title = "More Products", onViewAll = {})
                        RecommendedGrid(items = others, onProductClick = onProductClick)
                    }
                    
                    if (filteredProducts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No products found", color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Add Product FAB for Farmers
        if (UserSession.currentUserType == UserType.FARMER) {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = colorResource(id = R.color.green2),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(onDismiss = { showAddProductDialog = false })
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var kilos by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.ROOTS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price per Kilo") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        OutlinedTextField(
            value = kilos,
            onValueChange = { kilos = it },
            label = { Text("Available Kilos") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.height(100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
                
                Text("Category:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProductCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val scope = rememberCoroutineScope()
            
            Button(
                onClick = {
                    scope.launch {
                        val productEntity = ProductEntity(
                            name = name,
                            price = price.toDoubleOrNull() ?: 0.0,
                            kilos = kilos.toDoubleOrNull() ?: 1.0,
                            category = selectedCategory.name,
                            description = if (description.isNotBlank()) description else "Freshly harvested ${name}.",
                            imageRes = R.drawable.vegertable,
                            rating = 5.0,
                            farmerName = "My Farm",
                            isBestSeller = false,
                            isRecommended = false
                        )
                        db.productDao().insertProduct(productEntity)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank() && price.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun HomeHeader(searchQuery: String, onSearchChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search", color = Color.Gray, fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = { Icon(Icons.Outlined.Tune, contentDescription = null, tint = Color.Gray) }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Magandang Araw",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Extra Harvests From Local Farms To Your Table.",
            color = colorResource(id = R.color.yellowrice),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CategoryRow(selectedCategory: ProductCategory?, onCategorySelect: (ProductCategory) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProductCategory.entries.forEach { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategorySelect(category) }
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (selectedCategory == category) 
                                colorResource(id = R.color.green2)
                            else 
                                colorResource(id = R.color.green2A).copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = category.iconRes),
                        contentDescription = category.displayName,
                        tint = if (selectedCategory == category) Color.White else colorResource(id = R.color.green1),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.displayName, 
                    fontSize = 12.sp, 
                    color = if (selectedCategory == category) colorResource(id = R.color.green1) else Color.Gray,
                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green1))
        Row(
            modifier = Modifier.clickable { onViewAll() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "View All", fontSize = 14.sp, color = Color.Gray)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun BestSellersRow(items: List<Product>, onProductClick: (Product) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { product ->
            Card(
                modifier = Modifier.width(100.dp).clickable { onProductClick(product) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(text = "P${String.format("%.2f", product.price * product.kilos)}", color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PromoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green1))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "LOWER PRICES", color = Color.White, fontSize = 12.sp)
                Text(text = "30% OFF", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Image(
                painter = painterResource(id = R.drawable.vegertable),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun RecommendedGrid(items: List<Product>, onProductClick: (Product) -> Unit) {
    val chunks = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        chunks.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { product ->
                    RecommendedItem(
                        product = product,
                        modifier = Modifier.weight(1f).clickable { onProductClick(product) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RecommendedItem(product: Product, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            // Rating Tag
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = product.rating.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "★", color = colorResource(id = R.color.yellowrice), fontSize = 10.sp)
                }
            }

            // Price Tag
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = "P${String.format("%.2f", product.price * product.kilos)}", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MapaAni3Theme {
        HomeScreen(onProductClick = {})
    }
}
