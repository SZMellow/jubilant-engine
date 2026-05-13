package com.example.mapaani3

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme

@Composable
fun HomeScreen(onProductClick: (Product) -> Unit) {
    val repository = remember { AppRepository() }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }

    // Sync from DB
    LaunchedEffect(Unit) {
        repository.getAllProducts().collect { products ->
            ProductRepository.loadProducts(products)
        }
    }

    val filteredProducts by remember {
        derivedStateOf {
            ProductRepository.allProducts.filter { product ->
                val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || product.category == selectedCategory
                val hasStock = product.kilos > 0
                matchesSearch && matchesCategory && hasStock
            }
        }
    }

    val productsToShow by remember {
        derivedStateOf { filteredProducts }
    }

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

                    if (productsToShow.isNotEmpty()) {
                        SectionHeader(title = "Available Crops")
                        RecommendedGrid(items = productsToShow, onProductClick = onProductClick)
                    }
                    
                    if (filteredProducts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No products found", color = Color.Gray)
                        }
                    }
                    
                    // Extra spacer to ensure we can scroll past bottom elements if needed
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
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
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) }
            )
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
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green1))
    }
    Spacer(modifier = Modifier.height(16.dp))
}





@Composable
fun RecommendedGrid(items: List<Product>, onProductClick: (Product) -> Unit) {
    val chunks = items.chunked(2)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            ProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "by ${product.farmerName ?: "Local Farmer"}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "P${String.format("%.2f", product.price)}",
                        color = colorResource(id = R.color.green2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${product.kilos}kg left",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
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
