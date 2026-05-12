package com.example.mapaani3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val userType: String, // "BUYER" or "FARMER"
    val identificationProof: String? = null
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val kilos: Double,
    val description: String,
    val rating: Double,
    val imageRes: Int,
    val category: String,
    val farmerName: String,
    val isBestSeller: Boolean = false,
    val isRecommended: Boolean = false
)
