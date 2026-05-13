# Implementation Plan - MapaAni Final Updates

This plan outlines the changes for the five critical requirements of the MapaAni application: Firestore entities refactoring, process lifecycle handling, priority scheduling, admin dashboard creation, and Coil image loading integration.

## Proposed Changes

### Task 1: Refactor Firestore Entities

Update the data classes in `Entities.kt` and `ProductData.kt` to include new fields and remove old ones.

#### [Entities.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/Entities.kt)
- `UserEntity`: Add `isActive: Boolean = true`. Ensure `isVerified` is present.
- `ProductEntity`: Remove `imageRes`, add `imageUrl: String = ""`.
- `OrderEntity`: Add `priorityLevel: Int = 1`, `timestamp: Long = System.currentTimeMillis()`.

#### [ProductData.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/ProductData.kt)
- Update `Product` domain model: Remove `imageRes`, add `imageUrl: String = ""`.
- Update `Order` domain model: Add `priorityLevel: Int = 1`, `timestamp: Long`.
- Add `ADMIN` to `UserType` enum.

---

### Task 2: Mandatory OS Concept Implementation (Process Lifecycle)

Ensure the app survives process death by using `rememberSaveable` and `SavedStateHandle`.

#### [MainScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/MainScreen.kt)
- Replace `remember` with `rememberSaveable` for `selectedItem`, `selectedProduct`, and `currentScreen`.
- Implement a `Saver` for `Product` if needed, or store `selectedProductId`.

#### [NEW] [CheckoutViewModel.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/CheckoutViewModel.kt)
- Implement `CheckoutViewModel` using `SavedStateHandle` to store `selectedTime`, `isPriority`, and `cartItems`.

#### [CheckoutScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/CheckoutScreen.kt)
- Bind the UI to `CheckoutViewModel`.

---

### Task 3: Elective OS Concept (CPU Scheduling and Priority)

Implement a priority system for orders.

#### [CheckoutScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/CheckoutScreen.kt)
- Add a checkbox for "Priority Delivery".
- Update state in `CheckoutViewModel`.

#### [AppRepository.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/AppRepository.kt)
- Update `placeOrder` to save `priorityLevel` and `timestamp`.
- Update order queries to sort by `priorityLevel` DESC and `timestamp` ASC.

---

### Task 4: Create the Admin Dashboard

Create a dashboard for user and order management.

#### [NEW] [AdminScreen.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/AdminScreen.kt)
- Section A: Audit Log (List of all orders).
- Section B: User Management (List of farmers with `isVerified` and `isActive` toggles).

#### [AppRepository.kt](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/src/main/java/com/example/mapaani3/AppRepository.kt)
- Add `getAllUsersByType(type: String)` function.
- Add `updateUserStatus(userId: String, isVerified: Boolean, isActive: Boolean)` function.
- Add `getAllOrders()` function.

---

### Task 5: Coil Image Loading

Integrate Coil for efficient image loading.

#### [build.gradle.kts (app)](file:///D:/Coding/Android Studio Projects/jubilant-engine/app/build.gradle.kts)
- Add `io.coil-kt:coil-compose` dependency.

#### Image Loading Components
- Replace `painterResource` with `AsyncImage` in:
    - `HomeScreen.kt`
    - `ProductDetailScreen.kt`
    - `CartScreen.kt`
    - `OrdersScreen.kt`
    - `FarmerScreens.kt`

## Verification Plan

### Automated Tests
- I will run `./gradlew assembleDebug` to ensure the project builds correctly after changes.

### Manual Verification
- **Process Death Simulation**: Use `adb shell am kill <package_name>` while on the checkout screen and verify state is restored.
- **Priority Sort**: Place two orders (one priority, one normal) and verify the order in the Admin dashboard.
- **Admin Toggles**: Toggle verification for a farmer and verify they can/cannot list products.
- **Coil Loading**: Verify images load from URLs (using test URLs).
