# 🛒 Native Mobile E-Commerce App

A modern Android e-commerce application built with Clean Architecture, MVVM pattern, and the latest Android development technologies.

## ✨ Features

### 🏠 **Home Screen**
- **Product Listing**: Infinite scroll with Paging3 library
- **Search & Filter**: Real-time search and advanced filtering options
- **Grid Layout**: Responsive 4-column grid display
- **Favorites**: Add/remove products to favorites with real-time updates
- **Add to Cart**: Seamless cart integration

### 🔍 **Filter System**
- **Brand Filtering**: Filter products by brand
- **Model Filtering**: Filter products by model
- **Sort Options**: 
  - Price: High to Low / Low to High
  - Date: New to Old / Old to New
- **Search Integration**: Combined with filter system

### 🛍️ **Cart Management**
- **Persistent Cart**: Local storage with Room database
- **Quantity Management**: Adjust product quantities
- **Real-time Updates**: Live cart total and item count
- **Product Navigation**: Navigate to product details from cart
- **Clear Cart**: Bulk cart removal
- **Place Order**: Complete checkout process

### ❤️ **Favorites System**
- **Add/Remove**: Toggle favorite status
- **Persistent Storage**: Local favorites database
- **Real-time Sync**: Instant UI updates across screens
- **Dedicated Screen**: Browse all favorite products

### 📋 **Order Management**
- **Order History**: View all past orders
- **Order Details**: Complete order information
- **Order Status**: Track order progress
- **Delete Orders**: Remove completed orders

### 👤 **Profile Section**
- **Order History**: Complete order management
- **User Preferences**: Personalized settings
- **Navigation**: Easy access to all app sections

## 🏗️ Architecture

### **Clean Architecture**
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
├─────────────────────────────────────────────────────────────┤
│  • Activities & Fragments                                  │
│  • ViewModels                                              │
│  • UI State & Events                                       │
├─────────────────────────────────────────────────────────────┤
│                     Domain Layer                            │
├─────────────────────────────────────────────────────────────┤
│  • Use Cases                                               │
│  • Repository Interfaces                                   │
│  • Domain Models                                           │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                             │
├─────────────────────────────────────────────────────────────┤
│  • Repository Implementations                              │
│  • Local Database (Room)                                   │
│  • Remote API (Retrofit)                                   │
│  • Data Models                                             │
└─────────────────────────────────────────────────────────────┘
```

### **Design Patterns**
- **MVVM (Model-View-ViewModel)**: UI logic separation
- **Repository Pattern**: Data access abstraction
- **Use Case Pattern**: Business logic encapsulation
- **Observer Pattern**: Reactive UI updates
- **Dependency Injection**: Hilt integration

## 🛠️ Technologies & Libraries

### **Core Android**
- **Language**: Kotlin 100%
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture Components**: ViewModel, LiveData, Room

### **UI & Navigation**
- **Material Design**: Modern UI components
- **Navigation Component**: Type-safe navigation
- **RecyclerView**: Efficient list handling
- **ConstraintLayout**: Flexible layouts
- **Bottom Navigation**: Tab-based navigation

### **Data & Networking**
- **Room Database**: Local data persistence
- **Retrofit**: HTTP client for API calls
- **Gson**: JSON serialization
- **Paging3**: Efficient pagination
- **Coroutines & Flow**: Asynchronous programming

### **Image Loading**
- **Glide**: Fast image loading and caching
- **Error Handling**: Placeholder and error images
- **Memory Management**: Efficient memory usage

### **Dependency Injection**
- **Hilt**: Android dependency injection
- **Scoped Dependencies**: ViewModel and Fragment scopes

### **Testing**
- **JUnit4**: Unit testing framework
- **Mockito**: Mocking framework
- **Coroutines Test**: Asynchronous testing
- **Test Coverage**: Comprehensive test suite

## 📱 Screenshots

### Home Screen
<img width="407" height="849" alt="Screenshot 2025-08-17 at 23 59 19" src="https://github.com/user-attachments/assets/c8b29c8a-ed80-48f2-b3b4-25041778a939" />

### Product Detail
<img width="412" height="862" alt="Screenshot 2025-08-17 at 23 59 07" src="https://github.com/user-attachments/assets/f23783be-50a9-43b3-a051-b5341adec6a5" />

### Cart
<img width="411" height="863" alt="Screenshot 2025-08-17 at 23 58 47" src="https://github.com/user-attachments/assets/ca6a0883-e2b3-411b-977a-047e17c1551f" />

### Favorites
<img width="407" height="874" alt="Screenshot 2025-08-17 at 23 58 21" src="https://github.com/user-attachments/assets/2ac6aea6-52a5-4a93-9735-543d1358a5df" />

### Filter
<img width="420" height="866" alt="Screenshot 2025-08-17 at 23 57 59" src="https://github.com/user-attachments/assets/db1a1b48-f41e-4021-afa7-48597dce9169" />

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+
- JDK 11 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/native-mobile-ecommerce.git
   cd native-mobile-ecommerce
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Wait for Gradle sync to complete

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" button or press `Shift + F10`

### Configuration

1. **API Configuration**
   - Update API base URL in `NetworkModule.kt`
   - Configure API endpoints as needed

2. **Build Variants**
   - Debug: Development version
   - Release: Production version

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests HomeViewModelTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### Test Structure
```
app/src/test/
├── ui/
│   ├── home/HomeViewModelTest.kt
│   ├── filter/FilterViewModelTest.kt
│   ├── dashboard/DashboardViewModelTest.kt
│   ├── notifications/NotificationsViewModelTest.kt
│   ├── productdetail/ProductDetailViewModelTest.kt
│   └── profile/ProfileViewModelTest.kt
└── TestRunner.kt
```

### Test Coverage
- **ViewModel Tests**: 100% coverage
- **UI State Tests**: Comprehensive validation
- **Data Model Tests**: Structure verification
- **Business Logic Tests**: Use case validation

## 📊 Project Structure

```
app/src/main/
├── java/com/ozatactunahan/nativemobileapp/
│   ├── data/
│   │   ├── local/
│   │   │   ├── entity/          # Room entities
│   │   │   ├── dao/             # Data access objects
│   │   │   └── database/        # Database configuration
│   │   ├── model/               # Data models
│   │   ├── remote/              # API interfaces
│   │   └── repository/          # Repository implementations
│   ├── di/                      # Dependency injection modules
│   ├── domain/
│   │   ├── model/               # Domain models
│   │   ├── repository/          # Repository interfaces
│   │   └── usecase/             # Business logic
│   ├── ui/
│   │   ├── home/                # Home screen
│   │   ├── filter/              # Filter screen
│   │   ├── dashboard/           # Cart screen
│   │   ├── notifications/       # Favorites screen
│   │   ├── productdetail/       # Product detail
│   │   └── profile/             # Profile screen
│   └── util/                    # Utility classes
└── res/                         # Resources
    ├── layout/                  # UI layouts
    ├── values/                  # Strings, colors, themes
    ├── drawable/                # Icons and images
    └── navigation/              # Navigation graphs
```

## 🔧 Configuration

### Build Configuration
```gradle
android {
    compileSdk 34
    defaultConfig {
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}
```

### Dependencies
```gradle
dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // UI Components
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    
    // Navigation
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'
    
    // Room Database
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-ktx:2.6.1'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // Dependency Injection
    implementation 'com.google.dagger:hilt-android:2.48'
    
    // Paging
    implementation 'androidx.paging:paging-runtime-ktx:3.2.1'
}
```

## 🌟 Key Features

### **Performance Optimizations**
- **Lazy Loading**: Efficient image loading with Glide
- **Pagination**: Smooth scrolling with Paging3
- **Memory Management**: Optimized memory usage
- **Background Processing**: Coroutines for async operations

### **User Experience**
- **Responsive Design**: Adapts to different screen sizes
- **Smooth Animations**: Material Design transitions
- **Offline Support**: Local data persistence
- **Error Handling**: User-friendly error messages

### **Code Quality**
- **Clean Architecture**: Separation of concerns
- **SOLID Principles**: Maintainable code structure
- **Comprehensive Testing**: High test coverage
- **Code Documentation**: Clear and readable code

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### **Development Guidelines**
- Follow Clean Architecture principles
- Write comprehensive tests
- Use meaningful commit messages
- Follow Kotlin coding conventions
- Add proper documentation
