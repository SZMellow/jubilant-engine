package com.example.mapaani3.logic

import kotlin.math.round

/**
 * Utility functions for price calculations.
 */
object PriceUtils {
    
    private const val DISCOUNT_PERCENTAGE = 0.30

    /**
     * Calculates the 30% discount for imperfect crops.
     * Formula: originalPrice * (1 - 0.30)
     */
    fun calculateDiscountedPrice(originalPrice: Double): Double {
        val discounted = originalPrice * (1 - DISCOUNT_PERCENTAGE)
        // Round to 2 decimal places for clean UI display
        return round(discounted * 100) / 100.0
    }
}
