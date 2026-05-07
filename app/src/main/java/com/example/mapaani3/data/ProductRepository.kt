package com.example.mapaani3.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

/**
 * Repository that handles data operations for products.
 * Includes mock live stock updates and in-memory favorite management.
 */
class ProductRepository {

    // Mock favorites in memory (to be replaced by Room DAO later)
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    /**
     * Toggles the favorite status for a product.
     */
    fun toggleFavorite(productId: String) {
        favoriteIds.update { current ->
            if (current.contains(productId)) current - productId else current + productId
        }
    }

    /**
     * Observes the favorite status for a specific product.
     */
    fun isFavorite(productId: String): Flow<Boolean> = flow {
        favoriteIds.collect { ids ->
            emit(ids.contains(productId))
        }
    }

    /**
     * Mock Flow that emits live stock levels for a product.
     * In a real app, this would be a WebSocket or Firestore listener.
     */
    fun observeStockCount(productId: String): Flow<Int> = flow {
        // Start with a random initial stock
        var currentStock = Random.nextInt(5, 50)
        
        while (true) {
            emit(currentStock)
            // Mock a change every 5-10 seconds
            delay(Random.nextLong(5000, 10000))
            
            // Randomly fluctuate stock (sales or restocks)
            val change = Random.nextInt(-2, 3)
            currentStock = (currentStock + change).coerceAtLeast(0)
        }
    }
}
