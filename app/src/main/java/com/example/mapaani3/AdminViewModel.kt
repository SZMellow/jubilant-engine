package com.example.mapaani3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val sortedOrders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    private val _farmers = MutableStateFlow<List<UserEntity>>(emptyList())
    val farmers: StateFlow<List<UserEntity>> = _farmers.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                val ordersSnapshot = db.collection("orders").get().await()
                val rawOrders = ordersSnapshot.toObjects(OrderEntity::class.java)
                _orders.value = applySchedulingLogic(rawOrders)

                val usersSnapshot = db.collection("users").whereEqualTo("userType", "FARMER").get().await()
                _farmers.value = usersSnapshot.toObjects(UserEntity::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Requirement 3: Preemptive Priority Scheduling with Aging
     * Sorting: priorityLevel DESC, then timestamp ASC
     * Aging: +1 priority if > 2 hours old
     */
    private fun applySchedulingLogic(orders: List<OrderEntity>): List<OrderEntity> {
        val currentTime = System.currentTimeMillis()
        val twoHoursInMillis = 2 * 60 * 60 * 1000L

        return orders.map { order ->
            val waitTime = currentTime - order.timestamp
            val effectivePriority = if (waitTime > twoHoursInMillis) {
                order.priorityLevel + 1
            } else {
                order.priorityLevel
            }
            order.copy(priorityLevel = effectivePriority) // We use copy to keep it immutable but updated for UI
        }.sortedWith(
            compareByDescending<OrderEntity> { it.priorityLevel }
                .thenBy { it.timestamp }
        )
    }

    fun updateFarmerVerification(farmerId: String, isVerified: Boolean) {
        viewModelScope.launch {
            db.collection("users").document(farmerId).update("isVerified", isVerified).await()
            fetchData() // Refresh list
        }
    }

    fun updateFarmerActiveStatus(farmerId: String, isActive: Boolean) {
        viewModelScope.launch {
            db.collection("users").document(farmerId).update("isActive", isActive).await()
            fetchData() // Refresh list
        }
    }
}
