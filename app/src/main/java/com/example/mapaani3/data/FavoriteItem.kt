package com.example.mapaani3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing favorite products.
 * This ensures that a user's favorites persist across app restarts.
 */
@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey val productId: String
)
