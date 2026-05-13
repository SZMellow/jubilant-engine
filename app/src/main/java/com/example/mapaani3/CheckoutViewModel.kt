package com.example.mapaani3

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = AppRepository()

    // Key constants for SavedStateHandle
    companion object {
        private const val KEY_DELIVERY_TIME = "delivery_time"
        private const val KEY_IS_PRIORITY = "is_priority"
        private const val KEY_NOTES = "notes"
    }

    // State exposed to UI
    val deliveryTime: StateFlow<String> = savedStateHandle.getStateFlow(KEY_DELIVERY_TIME, "Morning (8AM - 11AM)")
    val isPriority: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_IS_PRIORITY, false)
    val notes: StateFlow<String> = savedStateHandle.getStateFlow(KEY_NOTES, "")

    private val _orderPlaced = MutableStateFlow(false)
    val orderPlaced: StateFlow<Boolean> = _orderPlaced

    fun updateDeliveryTime(time: String) {
        savedStateHandle[KEY_DELIVERY_TIME] = time
    }

    fun togglePriority(priority: Boolean) {
        savedStateHandle[KEY_IS_PRIORITY] = priority
    }

    fun updateNotes(newNotes: String) {
        savedStateHandle[KEY_NOTES] = newNotes
    }

    fun confirmBooking(items: List<CartItem>, buyerId: String, onOrderPlaced: () -> Unit) {
        viewModelScope.launch {
            if (buyerId.isNotEmpty()) {
                val priorityLevel = if (isPriority.value) 2 else 1
                repository.placeOrder(items, deliveryTime.value, buyerId, priorityLevel, notes.value)
                CartManager.clear()
                _orderPlaced.value = true
                onOrderPlaced()
            }
        }
    }
}
