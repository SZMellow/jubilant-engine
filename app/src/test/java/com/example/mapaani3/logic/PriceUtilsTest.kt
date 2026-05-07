package com.example.mapaani3.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceUtilsTest {

    @Test
    fun `calculateDiscountedPrice applies 30 percent discount and rounds to 2 decimal places`() {
        val originalPrice = 100.0
        val expectedPrice = 70.0
        assertEquals(expectedPrice, PriceUtils.calculateDiscountedPrice(originalPrice), 0.001)

        val originalPrice2 = 10.0
        val expectedPrice2 = 7.0
        assertEquals(expectedPrice2, PriceUtils.calculateDiscountedPrice(originalPrice2), 0.001)

        val originalPrice3 = 12.99
        // 12.99 * 0.7 = 9.093 -> rounds to 9.09
        val expectedPrice3 = 9.09
        assertEquals(expectedPrice3, PriceUtils.calculateDiscountedPrice(originalPrice3), 0.001)
    }
}
