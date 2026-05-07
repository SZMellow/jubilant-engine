package com.example.mapaani3

import androidx.annotation.DrawableRes

import androidx.compose.runtime.mutableStateListOf

enum class ProductCategory(val displayName: String, @DrawableRes val iconRes: Int) {
    ROOTS("Roots", R.drawable.roots),
    LEAFY("Leafy", R.drawable.leafy),
    BEANS("Beans", R.drawable.beans),
    GOURDS("Gourds", R.drawable.gourd)
}

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: ProductCategory,
    @DrawableRes val imageRes: Int,
    val description: String = "Freshly harvested locally grown vegetables. High quality and pesticide-free from our local farms to your table.",
    val rating: Double = 5.0,
    val isBestSeller: Boolean = false,
    val isRecommended: Boolean = false
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

object CartManager {
    val items = mutableStateListOf<CartItem>()

    fun addProduct(product: Product) {
        val index = items.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val existing = items[index]
            items[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            items.add(CartItem(product))
        }
    }

    fun removeProduct(productId: String) {
        items.removeAll { it.product.id == productId }
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

object SampleData {
    val products = listOf(
        Product("1", "Onions", 103.0, ProductCategory.ROOTS, R.drawable.onions, isBestSeller = true),
        Product("2", "Ginger", 50.0, ProductCategory.ROOTS, R.drawable.ginger, isBestSeller = true),
        Product("3", "Potatoes", 12.99, ProductCategory.ROOTS, R.drawable.potatoes, isBestSeller = true),
        Product("4", "Cabbage", 8.20, ProductCategory.LEAFY, R.drawable.cabbage, isBestSeller = true),
        Product("5", "Eggplant", 10.0, ProductCategory.GOURDS, R.drawable.eggplant, isRecommended = true),
        Product("6", "Carrots", 25.0, ProductCategory.ROOTS, R.drawable.carrots, isRecommended = true),
        Product("7", "Lettuce", 35.0, ProductCategory.LEAFY, R.drawable.lettuce),
        Product("8", "String Beans", 15.0, ProductCategory.BEANS, R.drawable.sitaw),
        Product("9", "Bitter Gourd", 20.0, ProductCategory.GOURDS, R.drawable.bittergourd)
    )
}
