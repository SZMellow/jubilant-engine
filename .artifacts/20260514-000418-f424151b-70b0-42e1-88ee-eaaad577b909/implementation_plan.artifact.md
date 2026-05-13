# Implementation Plan - Database Seeder

Create a `DatabaseSeeder` object to populate an empty Firestore database with mock data for demo purposes.

## User Review Required

> [!NOTE]
> I will use hardcoded document IDs for the primary users (Admin, Farmer, Buyer) to ensure relationships between Users, Products, and Orders are correctly established during the seeding process.

## Proposed Changes

### [Database Seeder]

#### [DatabaseSeeder.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/DatabaseSeeder.kt)

- Create a new Kotlin object `DatabaseSeeder`.
- Implement `seedDatabase(db: FirebaseFirestore)`.
- Use a `WriteBatch` to perform all operations atomically.
- Define:
    - 3 Users: Admin, Farmer, Buyer.
    - 5 Products: Linked to the Farmer.
    - 3 Orders: Linked to the Buyer and Farmer, with varying priority levels.

```kotlin
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
            isActive = true
        )
        // ... set in batch ...

        // 2. Create Products
        val product1 = ProductEntity(
            name = "Fresh Carrots",
            price = 45.0,
            kilos = 100.0,
            category = "Roots",
            farmerId = farmerId,
            farmerName = "Juan Farmer"
        )
        // ... set in batch ...

        // 3. Create Orders
        val order1 = OrderEntity(
            buyerId = buyerId,
            farmerId = farmerId,
            totalPrice = 150.0,
            priorityLevel = 0,
            timestamp = System.currentTimeMillis()
        )
        // ... set in batch ...

        batch.commit().addOnSuccessListener {
            // Log success
        }.addOnFailureListener {
            // Log failure
        }
    }
}
```

## Verification Plan

### Automated Tests
- I will verify the code compiles by checking it against the existing `Entities.kt` and `PasswordHasher.kt`.

### Manual Verification
- I will use `analyze_file` to ensure there are no syntax errors or unresolved symbols in the new `DatabaseSeeder.kt`.
- The user can trigger this seeder from their main activity or a button in the UI (outside the scope of this specific task but essential for the demo).
