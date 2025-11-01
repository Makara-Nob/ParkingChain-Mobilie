# Smart Parking Mobile App - Architecture Documentation

## Overview

This Android application follows **Clean Architecture** principles to ensure separation of concerns, maintainability, testability, and scalability. The architecture is organized into distinct layers, each with specific responsibilities.

## Architecture Pattern: Clean Architecture

Clean Architecture divides the application into layers with clear boundaries and dependency rules:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│              (UI, ViewModels, Components)                  │
└──────────────────────┬──────────────────────────────────┘
                       │ Depends on
┌──────────────────────┴──────────────────────────────────┐
│                     Domain Layer                         │
│        (Use Cases, Entities, Repository Interfaces)     │
└──────────────────────┬──────────────────────────────────┘
                       │ Implemented by
┌──────────────────────┴──────────────────────────────────┐
│                      Data Layer                          │
│        (Repositories, API Services, Data Sources)       │
└─────────────────────────────────────────────────────────┘
```

### Dependency Rule
- **Inner layers should NOT depend on outer layers**
- **Domain layer is independent** - it has no dependencies on other layers
- **Presentation and Data layers depend on Domain layer**
- This ensures business logic remains isolated and testable

---

## Layer Structure

### 📱 **Presentation Layer**
**Location**: `features/[feature]/presentation/`

**Purpose**: Handles user interface and user interactions

**Components**:
- **Views/Screens**: Composable functions that display UI (`SignInScreen.kt`, `SignUpScreen.kt`)
- **ViewModels**: Manage UI state and handle user actions (`SignInViewModel.kt`, `SignUpViewModel.kt`)
- **Components**: Reusable UI components (`EmailInput.kt`, `PasswordInput.kt`, etc.)
- **ViewModelFactories**: Create ViewModels with proper dependency injection

**Responsibilities**:
- Display data to users
- Handle user input and interactions
- Manage UI state (loading, error, success)
- Navigate between screens
- **DOES NOT contain business logic** - delegates to UseCases

**Example Flow**:
```kotlin
User clicks "Sign In" 
  → ViewModel calls UseCase 
  → UseCase executes business logic 
  → Repository fetches data 
  → ViewModel updates UI state
```

---

### 🎯 **Domain Layer**
**Location**: `features/[feature]/domain/`

**Purpose**: Contains business logic and core entities

**Components**:
- **Entities/Models**: Pure data classes representing business objects (`User.kt`)
- **Use Cases**: Business logic operations (`LoginUseCase.kt`, `RegisterUseCase.kt`)
- **Repository Interfaces**: Contracts that data layer must implement (`IAuthRepository.kt`)

**Responsibilities**:
- Define business rules and validation logic
- Define entities (data models) used across the app
- Define repository contracts (interfaces)
- **NO dependencies on Android framework or external libraries**
- **Completely testable without UI or network**

**Example - LoginUseCase**:
```kotlin
class LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validate email format
        // Validate password length
        // Call repository to authenticate
        // Return result
    }
}
```

**Key Benefits**:
- ✅ Business logic is independent and reusable
- ✅ Easy to unit test without mocking Android components
- ✅ Can be shared across different platforms (if needed)

---

### 💾 **Data Layer**
**Location**: `features/[feature]/data/`

**Purpose**: Handles data operations (API calls, local storage, etc.)

**Components**:
- **Repositories**: Implement domain repository interfaces (`AuthRepository.kt`)
- **Remote Services**: API service definitions (`AuthService.kt`)
- **Request/Response Models**: Data transfer objects for API communication
- **Data Sources**: Handle data persistence (database, shared preferences, etc.)

**Responsibilities**:
- Make API calls to backend
- Cache data locally if needed
- Handle network errors and retries
- Transform API responses to domain entities
- **Implements domain repository interfaces**

**Example - AuthRepository**:
```kotlin
class AuthRepository : IAuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        // Make API call
        // Handle errors
        // Return domain entity directly (no DTO conversion needed)
    }
}
```

**Note**: In this implementation, we skip the DTO layer because the backend API already returns data in the correct format matching our domain entities.

---

## Feature Module Structure

Each feature follows this structure:

```
features/
└── auth/
    ├── data/
    │   ├── remote/
    │   │   ├── AuthService.kt          # Retrofit API interface
    │   │   └── models/
    │   │       ├── LoginRequest.kt
    │   │       ├── LoginResponse.kt
    │   │       └── RegisterRequest.kt
    │   └── repository/
    │       └── AuthRepository.kt      # Implements IAuthRepository
    ├── domain/
    │   ├── model/
    │   │   └── User.kt                 # Domain entity
    │   ├── repository/
    │   │   └── IAuthRepository.kt     # Repository interface
    │   ├── LoginUseCase.kt             # Business logic
    │   └── RegisterUseCase.kt          # Business logic
    └── presentation/
        ├── view/
        │   ├── SignInScreen.kt         # UI screen
        │   └── SignUpScreen.kt          # UI screen
        ├── viewmodel/
        │   ├── SignInViewModel.kt       # UI state management
        │   ├── SignUpViewModel.kt
        │   └── [Feature]ViewModelFactory.kt
        └── components/
            └── [Reusable UI components]
```

---

## Data Flow Example: Authentication

### 1. **User Action**
```kotlin
// SignInScreen.kt
Button(onClick = { viewModel.signIn(email, password) })
```

### 2. **ViewModel**
```kotlin
// SignInViewModel.kt
fun signIn(email: String, password: String) {
    viewModelScope.launch {
        _loginState.value = Resource.Loading
        val result = loginUseCase(email, password)  // ← Calls UseCase
        // Update UI state based on result
    }
}
```

### 3. **Use Case (Business Logic)**
```kotlin
// LoginUseCase.kt
suspend operator fun invoke(email: String, password: String): Result<User> {
    // Validate input
    if (email.isBlank() || password.isBlank()) {
        return Result.failure(Exception("Email and password cannot be empty"))
    }
    
    // Validate email format
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return Result.failure(Exception("Invalid email format"))
    }
    
    // Delegate to repository
    return authRepository.login(email, password)
}
```

### 4. **Repository**
```kotlin
// AuthRepository.kt
override suspend fun login(email: String, password: String): Result<User> {
    val response = authService.login(LoginRequest(email, password))
    
    if (response.success && response.data != null) {
        tokenDataStore.saveToken(response.data.token)
        return Result.success(response.data.user)  // Domain entity
    }
    
    return Result.failure(Exception(response.message))
}
```

### 5. **API Service**
```kotlin
// AuthService.kt
@POST("auth/login")
suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>
```

### 6. **Response Flows Back**
```
API Response → Repository → UseCase → ViewModel → UI Update
```

---

## Key Principles

### ✅ **Separation of Concerns**
- Each layer has a single, well-defined responsibility
- Business logic is isolated from UI and data access

### ✅ **Dependency Inversion**
- Domain layer defines interfaces
- Data layer implements those interfaces
- Presentation layer depends on abstractions (UseCases), not concrete implementations

### ✅ **Testability**
- Domain layer can be tested without Android framework
- UseCases can be tested with mock repositories
- ViewModels can be tested with mock UseCases

### ✅ **Maintainability**
- Changes in one layer don't affect others (when done correctly)
- Easy to understand where code belongs
- Clear boundaries make refactoring safer

### ✅ **Scalability**
- Easy to add new features following the same pattern
- Consistent structure across the codebase
- Easy for new developers to understand

---

## Dependency Injection

ViewModels are created using **ViewModelFactory** pattern:

```kotlin
// SignInViewModelFactory.kt
class SignInViewModelFactory(private val context: Context) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenDataStore = TokenDataStore(context)
        val authService = RetrofitInstance.authApi
        val repository = AuthRepository(authService, tokenDataStore)
        val loginUseCase = LoginUseCase(repository)
        return SignInViewModel(loginUseCase) as T
    }
}
```

**Usage in Screen**:
```kotlin
val viewModel: SignInViewModel = viewModel(
    factory = SignInViewModelFactory(context)
)
```

---

## Why This Architecture?

### 🎯 **For Developers**
- **Clear structure**: Know exactly where to put new code
- **Easy debugging**: Issues are isolated to specific layers
- **Code reuse**: Business logic can be shared across features

### 🧪 **For Testing**
- Test business logic without UI
- Test UI without network calls
- Mock dependencies easily

### 🔄 **For Maintenance**
- Update UI without touching business logic
- Change data source (API → database) without affecting domain
- Modify business rules without UI changes

### 📈 **For Scaling**
- Add new features consistently
- Onboard new developers faster
- Reduce bugs through clear boundaries

---

## Best Practices

1. **Always follow the dependency rule**: Inner layers don't depend on outer layers
2. **Put business logic in UseCases**, not ViewModels or Repositories
3. **Use domain entities** throughout the app (not DTOs)
4. **Keep ViewModels thin**: They should only manage UI state
5. **Validate in UseCases**: Don't validate in ViewModels
6. **Handle errors at appropriate layers**: Network errors in Repository, business errors in UseCase
7. **Use Repository interfaces** in domain layer, implementations in data layer

---

## Resources

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Architecture Guidelines](https://developer.android.com/topic/architecture)

---

## Summary

This Clean Architecture implementation provides:
- ✅ **Clear separation** between UI, business logic, and data
- ✅ **Testable** business logic independent of frameworks
- ✅ **Maintainable** codebase with clear boundaries
- ✅ **Scalable** structure for growing features
- ✅ **Consistent** patterns across all features

By following this architecture, the codebase remains clean, testable, and maintainable as the application grows.

