package com.example.mapaani3

import com.google.firebase.firestore.DocumentId

data class UserEntity(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val userType: String = "", // "BUYER" or "FARMER"
    val identificationProof: String? = null
)

data class ProductEntity(
    @DocumentId val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val kilos: Double = 0.0, // Inventory available
    val description: String = "",
    val imageRes: Int = 0,
    val category: String = "",
    val farmerId: String = "", // Link to UserEntity (Owner)
    val farmerName: String = ""
)

data class OrderItemEntity(
    val productId: String = "",
    val productName: String = "",
    val quantityKilos: Double = 0.0,
    val priceAtTime: Double = 0.0,
    val imageRes: Int = 0
)

data class OrderEntity(
    @DocumentId val id: String = "",
    val buyerId: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val totalPrice: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val deliveryTime: String = "",
    val date: String = "",
    val status: String = "Active", // "Active", "Completed", "Cancelled"
    val items: List<OrderItemEntity> = emptyList()
)
