package com.example.mapaani3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.R
import com.example.mapaani3.logic.PriceUtils

/**
 * A badge that highlights imperfect crops with a discount notice.
 */
@Composable
fun ImperfectBadge(isImperfect: Boolean) {
    if (isImperfect) {
        Surface(
            color = colorResource(id = R.color.yellowrice),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "Imperfect Crop 30% OFF",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Displays the price with discount logic for imperfect crops.
 */
@Composable
fun PriceDisplay(price: Double, isImperfect: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isImperfect) {
            val discountedPrice = PriceUtils.calculateDiscountedPrice(price)
            Text(
                text = "P$price",
                fontSize = 16.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "P$discountedPrice",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        } else {
            Text(
                text = "P$price",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green2)
            )
        }
    }
}

/**
 * Favorite button with live stock quantity display.
 */
@Composable
fun FavoriteStockButton(
    isFavorite: Boolean,
    stockCount: Int,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onToggle() }
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Toggle Favorite",
            tint = if (isFavorite) Color.Red else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$stockCount kg available",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}
