package com.example.mapaani3

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * A central repository to bridge the UI and Firebase Firestore.
 */
class AppRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")
    private val usersCollection = db.collection("users")
    private val ordersCollection = db.collection("orders")

    // --- Product Operations ---

    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val products = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(ProductEntity::class.java)?.toProductDomain()
            } ?: emptyList()
            trySend(products)
        }
        awaitClose { listener.remove() }
    }

    fun getFarmerProducts(farmerId: String): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection.whereEqualTo("farmerId", farmerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ProductEntity::class.java)?.toProductDomain()
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addProduct(product: Product) {
        val entity = product.toEntity()
        if (entity.id.isEmpty()) {
            productsCollection.add(entity).await()
        } else {
            productsCollection.document(entity.id).set(entity).await()
        }
    }

    suspend fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete().await()
    }

    // --- Order Operations ---

    fun getBuyerOrders(buyerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection.whereEqualTo("buyerId", buyerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(OrderEntity::class.java)?.toOrderDomain()
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun getFarmerOrders(farmerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection.whereEqualTo("farmerId", farmerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(OrderEntity::class.java)?.toOrderDomain()
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    suspend fun placeOrder(items: List<CartItem>, deliveryTime: String, buyerId: String) {
        val firstProduct = items.firstOrNull()?.product
        val farmerId = firstProduct?.farmerId ?: ""
        val farmerName = firstProduct?.farmerName ?: ""
        val subtotal = items.sumOf { it.product.price * it.quantityKilos }
        val deliveryFee = subtotal * 0.02
        val total = subtotal + deliveryFee
        
        val orderItems = items.map { item ->
            OrderItemEntity(
                productId = item.product.id,
                productName = item.product.name,
                quantityKilos = item.quantityKilos,
                priceAtTime = item.product.price,
                imageRes = item.product.imageRes
            )
        }

        val orderEntity = OrderEntity(
            buyerId = buyerId,
            farmerId = farmerId,
            farmerName = farmerName,
            totalPrice = total,
            deliveryFee = deliveryFee,
            deliveryTime = deliveryTime,
            date = "May 13, 2026",
            status = "Active",
            items = orderItems
        )

        ordersCollection.add(orderEntity).await()

        // Decrease stock for each product
        items.forEach { item ->
            val productRef = productsCollection.document(item.product.id)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                val currentKilos = snapshot.getDouble("kilos") ?: 0.0
                transaction.update(productRef, "kilos", (currentKilos - item.quantityKilos).coerceAtLeast(0.0))
            }.await()
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        if (newStatus == "Cancelled") {
            db.runTransaction { transaction ->
                val orderRef = ordersCollection.document(orderId)
                val orderSnapshot = transaction.get(orderRef)
                val status = orderSnapshot.getString("status") ?: ""
                
                if (status == "Active") {
                    val orderItems = orderSnapshot.get("items") as? List<Map<String, Any>> ?: emptyList()
                    
                    // Reallocate stock back to products
                    orderItems.forEach { itemMap ->
                        val productId = itemMap["productId"] as? String ?: ""
                        val qty = (itemMap["quantityKilos"] as? Number)?.toDouble() ?: 0.0
                        
                        if (productId.isNotEmpty()) {
                            val productRef = productsCollection.document(productId)
                            val productSnapshot = transaction.get(productRef)
                            val currentKilos = productSnapshot.getDouble("kilos") ?: 0.0
                            transaction.update(productRef, "kilos", currentKilos + qty)
                        }
                    }
                    
                    transaction.update(orderRef, "status", "Cancelled")
                }
            }.await()
        } else {
            ordersCollection.document(orderId).update("status", newStatus).await()
        }
    }

    suspend fun clearAllData() {
        // Warning: This is not efficient for large collections, but fine for small samples
        val collections = listOf(productsCollection, usersCollection, ordersCollection)
        collections.forEach { coll ->
            val snapshot = coll.get().await()
            snapshot.documents.forEach { it.reference.delete().await() }
        }
    }

    // --- User Operations ---

    suspend fun getUserByEmail(email: String): UserEntity? {
        val snapshot = usersCollection.whereEqualTo("email", email).get().await()
        return snapshot.documents.firstOrNull()?.toObject(UserEntity::class.java)
    }

    suspend fun getUserById(userId: String): UserEntity? {
        val snapshot = usersCollection.document(userId).get().await()
        return snapshot.toObject(UserEntity::class.java)
    }

    fun getUserByIdStream(userId: String): Flow<UserEntity?> = callbackFlow {
        val listener = usersCollection.document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(UserEntity::class.java))
        }
        awaitClose { listener.remove() }
    }

    suspend fun registerUser(user: UserEntity) {
        usersCollection.add(user).await()
    }

    // --- Helper Mappers ---

    private fun ProductEntity.toProductDomain() = Product(
        id = id,
        name = name,
        price = price,
        category = try { ProductCategory.valueOf(category) } catch(e: Exception) { ProductCategory.ROOTS },
        imageRes = imageRes,
        kilos = kilos,
        description = description,
        farmerId = farmerId,
        farmerName = farmerName
    )

    private fun OrderEntity.toOrderDomain() = Order(
        id = id,
        items = items.map { it.toCartItemDomain() }, 
        totalPrice = totalPrice,
        deliveryFee = deliveryFee,
        deliveryTime = deliveryTime,
        date = date,
        status = status,
        buyerId = buyerId,
        farmerId = farmerId,
        farmerName = farmerName
    )

    private fun OrderItemEntity.toCartItemDomain() = CartItem(
        product = Product(
            id = productId,
            name = productName,
            price = priceAtTime,
            category = ProductCategory.ROOTS, // Default
            imageRes = imageRes
        ),
        quantityKilos = quantityKilos
    )

    private fun Product.toEntity() = ProductEntity(
        id = id,
        name = name,
        price = price,
        kilos = kilos,
        description = description,
        imageRes = imageRes,
        category = category.name,
        farmerId = farmerId ?: "",
        farmerName = farmerName ?: ""
    )
}
