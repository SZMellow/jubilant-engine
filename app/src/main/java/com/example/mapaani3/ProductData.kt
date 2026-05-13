package com.example.mapaani3

import com.example.mapaani3.R
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ProductCategory(val displayName: String, @DrawableRes val iconRes: Int) {
    ROOTS("Roots", R.drawable.roots),
    LEAFY("Leafy", R.drawable.leafy),
    BEANS("Beans", R.drawable.beans),
    GOURDS("Gourds", R.drawable.gourd)
}

enum class UserType {
    FARMER, BUYER, ADMIN
}

object UserSession {
    var currentUserType by mutableStateOf(UserType.BUYER)
    var currentUserId by mutableStateOf<String?>(null)
    var isUserVerified by mutableStateOf(false)
}

data class Product(
    val id: String = "",
    val name: String,
    val price: Double,
    val category: ProductCategory,
    @DrawableRes val imageRes: Int,
    val kilos: Double = 1.0,
    val description: String = "Freshly harvested locally grown vegetables. High quality and pesticide-free from our local farms to your table.",
    val farmerId: String? = null,
    val farmerName: String? = null
)

data class CartItem(
    val product: Product,
    val quantityKilos: Double = 1.0
)

object CartManager {
    val items = mutableStateListOf<CartItem>()

    fun addProduct(product: Product, kilos: Double) {
        val index = items.indexOfFirst { it.product.id == product.id }
        if (index == -1) {
            items.add(CartItem(product, kilos))
        } else {
            // Update quantity if already in cart
            val currentItem = items[index]
            items[index] = currentItem.copy(quantityKilos = currentItem.quantityKilos + kilos)
        }
    }

    fun removeProduct(productId: String) {
        items.removeAll { it.product.id == productId }
    }

    fun clear() {
        items.clear()
    }
}

data class Order(
    val id: String = "",
    val items: List<CartItem>,
    val totalPrice: Double,
    val deliveryFee: Double = 0.0,
    val deliveryTime: String,
    val date: String,
    var status: String = "Active",
    val buyerId: String? = null,
    val farmerId: String? = null,
    val farmerName: String? = null
)

object OrderManager {
    val orders = mutableStateListOf<Order>()
    val farmerOrders = mutableStateListOf<Order>()

    fun placeOrder(items: List<CartItem>, deliveryTime: String, buyerId: String?) {
        val totalPrice = items.sumOf { it.product.price * it.quantityKilos }
        // Assume for simplicity an order is for one farmer's items or we tag it with the first item's farmer
        val firstProduct = items.firstOrNull()?.product
        val farmerId = firstProduct?.farmerId
        val farmerName = firstProduct?.farmerName
        
        val order = Order(
            id = "ORD-${System.currentTimeMillis()}",
            items = items.toList(),
            totalPrice = totalPrice,
            deliveryTime = deliveryTime,
            date = "May 07, 2026", // Mock date
            status = "Active",
            buyerId = buyerId,
            farmerId = farmerId,
            farmerName = farmerName
        )
        orders.add(0, order)
        farmerOrders.add(0, order)
    }
}

data class ProductRequirement(
    val id: String = "",
    val name: String,
    val minKilos: Double,
    val buyerId: String? = null
)

object RequirementManager {
    val requirements = mutableStateListOf<ProductRequirement>()

    fun addRequirement(name: String, kilos: Double, buyerId: String?) {
        requirements.add(ProductRequirement(
            id = System.currentTimeMillis().toString(),
            name = name,
            minKilos = kilos,
            buyerId = buyerId
        ))
    }

    fun removeRequirement(id: String) {
        requirements.removeAll { it.id == id }
    }

    fun findMatches(requirement: ProductRequirement): List<Product> {
        return ProductRepository.allProducts.filter { product ->
            product.name.contains(requirement.name, ignoreCase = true) &&
            product.kilos >= requirement.minKilos
        }
    }
}

object ProductRepository {
    val allProducts = mutableStateListOf<Product>()

    fun loadProducts(products: List<Product>) {
        allProducts.clear()
        allProducts.addAll(products)
    }

    fun addProduct(product: Product) {
        allProducts.add(0, product)
    }

    fun removeProduct(productId: String) {
        allProducts.removeAll { it.id == productId }
    }
}

object FarmerManager {
    val myListings = mutableStateListOf<Product>()

    fun addListing(product: Product) {
        myListings.add(product)
    }
    
    fun removeListing(productId: String) {
        myListings.removeAll { it.id == productId }
    }
}

fun getDrawableForProductName(productName: String, fallbackIcon: Int): Int {
    val nameToMatch = productName.lowercase()
    
    return when {
        nameToMatch.contains("carrot") -> R.drawable.carrots
        nameToMatch.contains("cabbage") -> R.drawable.cabbage
        nameToMatch.contains("eggplant") -> R.drawable.eggplant
        nameToMatch.contains("onion") -> R.drawable.onions
        nameToMatch.contains("potato") -> R.drawable.potatoes
        nameToMatch.contains("sitaw") || nameToMatch.contains("bean") || nameToMatch.contains("pea") -> R.drawable.sitaw
        nameToMatch.contains("ginger") -> R.drawable.ginger
        nameToMatch.contains("lettuce") || nameToMatch.contains("spinach") || nameToMatch.contains("leaf") -> R.drawable.lettuce
        nameToMatch.contains("rice") -> R.drawable.rice
        nameToMatch.contains("bitter") -> R.drawable.bittergourd
        nameToMatch.contains("tomato") || nameToMatch.contains("cucumber") || nameToMatch.contains("gourd") -> R.drawable.gourd
        nameToMatch.contains("radish") -> R.drawable.roots
        else -> fallbackIcon
    }
}

object SampleData {
    val products = listOf(
        Product("1", "Onions", 103.0, ProductCategory.ROOTS, R.drawable.onions, kilos = 50.0),
        Product("2", "Ginger", 50.0, ProductCategory.ROOTS, R.drawable.ginger, kilos = 30.0),
        Product("3", "Potatoes", 12.99, ProductCategory.ROOTS, R.drawable.potatoes, kilos = 100.0),
        Product("4", "Cabbage", 8.20, ProductCategory.LEAFY, R.drawable.cabbage, kilos = 40.0),
        Product("5", "Eggplant", 10.0, ProductCategory.GOURDS, R.drawable.eggplant, kilos = 25.0),
        Product("6", "Carrots", 25.0, ProductCategory.ROOTS, R.drawable.carrots, kilos = 35.0),
        Product("7", "Lettuce", 35.0, ProductCategory.LEAFY, R.drawable.lettuce, kilos = 20.0),
        Product("8", "String Beans", 15.0, ProductCategory.BEANS, R.drawable.sitaw, kilos = 15.0),
        Product("9", "Bitter Gourd", 20.0, ProductCategory.GOURDS, R.drawable.bittergourd, kilos = 10.0)
    )

    fun getFarmerSpecificProducts(farmerId: String, farmerName: String = "Unknown", farmerIndex: Int = 1): List<Product> {
        val index = ((farmerIndex - 1) % 5) + 1 // Ensure we get 1 to 5
        
        return listOf(
            Product("", "Tomato", 45.0, ProductCategory.GOURDS, getDrawableId("tomato$index"), kilos = 100.0, farmerId = farmerId, farmerName = farmerName),
            Product("", "Cucumber", 30.0, ProductCategory.GOURDS, getDrawableId("cucumber$index"), kilos = 50.0, farmerId = farmerId, farmerName = farmerName),
            Product("", "Spinach", 25.0, ProductCategory.LEAFY, getDrawableId("spinach$index"), kilos = 20.0, farmerId = farmerId, farmerName = farmerName),
            Product("", "Radish", 15.0, ProductCategory.ROOTS, getDrawableId("radish$index"), kilos = 40.0, farmerId = farmerId, farmerName = farmerName),
            Product("", "Peas", 60.0, ProductCategory.BEANS, getDrawableId("peas$index"), kilos = 15.0, farmerId = farmerId, farmerName = farmerName)
        )
    }

    private fun getDrawableId(name: String): Int {
        return when (name) {
            "tomato1" -> R.drawable.tomato1
            "tomato2" -> R.drawable.tomato2
            "tomato3" -> R.drawable.tomato3
            "tomato4" -> R.drawable.tomato4
            "tomato5" -> R.drawable.tomato5
            "cucumber1" -> R.drawable.cucumber1
            "cucumber2" -> R.drawable.cucumber2
            "cucumber3" -> R.drawable.cucumber3
            "cucumber4" -> R.drawable.cucumber4
            "cucumber5" -> R.drawable.cucumber5
            "spinach1" -> R.drawable.spinach1
            "spinach2" -> R.drawable.spinach2
            "spinach3" -> R.drawable.spinach3
            "spinach4" -> R.drawable.spinach4
            "spinach5" -> R.drawable.spinach5
            "radish1" -> R.drawable.radish1
            "radish2" -> R.drawable.radish2
            "radish3" -> R.drawable.radish3
            "radish4" -> R.drawable.radish4
            "radish5" -> R.drawable.radish5
            "peas1" -> R.drawable.peas1
            "peas2" -> R.drawable.peas2
            "peas3" -> R.drawable.peas3
            "peas4" -> R.drawable.peas4
            "peas5" -> R.drawable.peas5
            else -> R.drawable.gourd
        }
    }
}
