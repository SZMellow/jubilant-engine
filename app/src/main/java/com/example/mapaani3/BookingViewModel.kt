package com.example.mapaani3

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class BookingViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Keys for SavedStateHandle
    companion object {
        private const val KEY_PRODUCT_ID = "product_id"
        private const val KEY_QUANTITY = "quantity"
    }

    // LiveData or StateFlow could be used, but for direct binding to Compose 
    // we can use the SavedStateHandle's StateFlow capability or just the Handle itself.
    
    val productId = savedStateHandle.getStateFlow(KEY_PRODUCT_ID, "")
    val quantity = savedStateHandle.getStateFlow(KEY_QUANTITY, "")

    fun onProductIdChange(newId: String) {
        savedStateHandle[KEY_PRODUCT_ID] = newId
    }

    fun onQuantityChange(newQuantity: String) {
        savedStateHandle[KEY_QUANTITY] = newQuantity
    }
}
