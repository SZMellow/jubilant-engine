package com.example.mapaani3

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

object DatabaseSeeder {
    fun seedDatabase(db: FirebaseFirestore) {
        val batch = db.batch()

        // 1. Create Users
        val adminId = "user_admin"
        val farmerId = "user_farmer"
        val buyerId = "user_buyer"

        val admin = UserEntity(
            id = adminId,
            name = "System Admin",
            email = "admin@mapaani.com",
            passwordHash = PasswordHasher.hash("password123"),
            userType = "ADMIN",
            isVerified = true,
            isActive = true,
        )
        val farmer = UserEntity(
            id = farmerId,
            name = "Juan Farmer",
            email = "farmer@mapaani.com",
            passwordHash = PasswordHasher.hash("password123"),
            userType = "FARMER",
            isVerified = true,
            isActive = true,
        )
        val buyer = UserEntity(
            id = buyerId,
            name = "Maria Buyer",
            email = "buyer@mapaani.com",
            passwordHash = PasswordHasher.hash("password123"),
            userType = "BUYER",
            isVerified = true,
            isActive = true,
        )

        batch.set(db.collection("users").document(adminId), admin)
        batch.set(db.collection("users").document(farmerId), farmer)
        batch.set(db.collection("users").document(buyerId), buyer)

        // 2. Create Products
        val products = listOf(
            ProductEntity(
                name = "Fresh Carrots",
                price = 45.0,
                kilos = 100.0,
                category = "Roots",
                farmerId = farmerId,
                farmerName = "Juan Farmer",
                description = "Crispy organic carrots straight from the farm."
            ),
            ProductEntity(
                name = "Organic Tomatoes",
                price = 60.0,
                kilos = 50.0,
                category = "Vegetables",
                farmerId = farmerId,
                farmerName = "Juan Farmer",
                description = "Juicy red tomatoes perfect for salads."
            ),
            ProductEntity(
                name = "Sweet Corn",
                price = 25.0,
                kilos = 200.0,
                category = "Grains",
                farmerId = farmerId,
                farmerName = "Juan Farmer",
                description = "Freshly harvested sweet corn."
            ),
            ProductEntity(
                name = "Green Cabbage",
                price = 35.0,
                kilos = 80.0,
                category = "Vegetables",
                farmerId = farmerId,
                farmerName = "Juan Farmer",
                description = "Large, healthy green cabbage."
            ),
            ProductEntity(
                name = "Potatoes",
                price = 40.0,
                kilos = 150.0,
                category = "Roots",
                farmerId = farmerId,
                farmerName = "Juan Farmer",
                description = "Versatile potatoes for all your cooking needs."
            )
        )

        products.forEach { product ->
            val docRef = db.collection("products").document()
            batch.set(docRef, product)
        }

        // 3. Create Orders
        val order1 = OrderEntity(
            buyerId = buyerId,
            farmerId = farmerId,
            farmerName = "Juan Farmer",
            totalPrice = 150.0,
            priorityLevel = 0,
            status = "Completed",
            date = "2023-10-25",
            timestamp = System.currentTimeMillis() - (86400000L * 2) // 2 days ago
        )
        val order2 = OrderEntity(
            buyerId = buyerId,
            farmerId = farmerId,
            farmerName = "Juan Farmer",
            totalPrice = 240.0,
            priorityLevel = 1,
            status = "Active",
            date = "2023-10-26",
            timestamp = System.currentTimeMillis() - 86400000L // 1 day ago
        )
        val order3 = OrderEntity(
            buyerId = buyerId,
            farmerId = farmerId,
            farmerName = "Juan Farmer",
            totalPrice = 90.0,
            priorityLevel = 2,
            status = "Active",
            date = "2023-10-27",
            timestamp = System.currentTimeMillis()
        )

        batch.set(db.collection("orders").document(), order1)
        batch.set(db.collection("orders").document(), order2)
        batch.set(db.collection("orders").document(), order3)

        batch.commit().addOnSuccessListener {
            Log.d("DatabaseSeeder", "Database successfully seeded!")
        }.addOnFailureListener { e ->
            Log.w("DatabaseSeeder", "Error seeding database", e)
        }
    }
}
