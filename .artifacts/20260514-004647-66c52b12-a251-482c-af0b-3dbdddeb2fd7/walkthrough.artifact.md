# Walkthrough - MapaAni Final Updates Implementation

I have completed the five critical requirements for the MapaAni application. Below is a summary of the implementation and how to verify it.

## 1. Refactored Firestore Entities
Updated the data models to support the new business logic.
- **UserEntity**: Added `isActive` (default `true`) and `isVerified` (default `false`).
- **ProductEntity**: Replaced `imageRes` (Int) with `imageUrl` (String).
- **OrderEntity**: Added `priorityLevel` (Int) and `timestamp` (Long).
- [Entities.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/Entities.kt)
- [ProductData.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/ProductData.kt)

## 2. Process Lifecycle Management
Implemented state preservation to handle OS process death.
- **Main Navigation**: Updated `MainScreen` to use `rememberSaveable` for navigation state and the current product ID.
- **Checkout Flow**: Created `CheckoutViewModel` using `SavedStateHandle`. If the app is killed while the user is typing delivery details, the data is restored upon return.
- [MainScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/MainScreen.kt)
- [CheckoutViewModel.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/CheckoutViewModel.kt)
- [CheckoutScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/CheckoutScreen.kt)

## 3. Priority Scheduling System
Implemented a "CPU-like" scheduling for orders based on priority levels.
- **Checkout UI**: Added a "Priority Delivery" checkbox that sets `priorityLevel` to 2.
- **Repository Sorting**: Firestore queries now sort orders first by `priorityLevel` (Descending) and then by `timestamp` (Ascending).
- [AppRepository.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/ProductRepository.kt)

## 4. Admin Dashboard
Created a comprehensive [AdminScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/AdminScreen.kt) with two sections:
- **Audit Log**: Displays all orders sorted by priority.
- **User Management**: Lists all farmers with toggles for `isVerified` and `isActive`. These toggles update Firestore in real-time.
- **Navigation**: Integrated "ADMIN" role in `MainActivity.kt`.

## 5. Coil Image Loading
Migrated the entire app to use the Coil library for network-based image loading.
- **Dependency**: Added `io.coil-kt:coil-compose` to `build.gradle.kts`.
- **Reusable Component**: Created `ProductImage` in `CommonUI.kt` to handle `AsyncImage` with placeholders.
- **UI Updates**: Updated `HomeScreen`, `ProductDetailScreen`, `CartScreen`, and `OrdersScreen`.

## Verification Summary
- **Build**: Successfully completed `./gradlew app:assembleDebug`.
- **Logic**: All Firebase calls are wrapped in Coroutine scopes (`viewModelScope` or `rememberCoroutineScope`).
- **Mappers**: Updated all repository mappers to handle the transition between `Entity` and `Domain` objects with the new fields.
