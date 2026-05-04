# Anganwadi Management System

## Project Overview

The **Anganwadi Management System** is a modern Android application designed to digitize and streamline the operations of Anganwadi centers (rural child care centers in India). The app serves two primary user roles:

1. **Staff (Anganwadi Workers)** - Manage center operations, children records, attendance, health, diet, and reports
2. **Parents** - Monitor their child's daily and weekly progress, attendance, meals, health, and activities

Built with **Kotlin** and **Jetpack Compose**, the app follows modern Android development best practices with a clean architecture pattern and Firebase as the backend.

---

## App Flow & Navigation

```
Splash Screen
    │
    ▼
Role Selection Screen
    ├── Staff Path
    │       ├── Staff Auth Selection (Login / Register)
    │       ├── Staff Login Screen ──▶ Main Dashboard (Staff Mode)
    │       └── Staff Registration Screen ──▶ Login
    │
    └── Parent Path
            ├── Parent Login Screen ──▶ Parent Main Dashboard
            │                              ├── Daily Updates
            │                              │   ├── Attendance Preview
            │                              │   ├── Food Menu
            │                              │   ├── Activity Rating
            │                              │   └── Health Observation
            │                              │
            │                              └── Weekly Updates
            │                                  ├── Weekly Health Summary
            │                                  ├── Vaccination Updates
            │                                  ├── Activity Summary
            │                                  ├── Weekly Attendance Summary
            │                                  └── Nutrition Report
            │
            └── Guest Mode (Limited Access)
                    └── Main Dashboard (Read-Only)
```

---

## Module Documentation

### 1. Authentication Module

**Location:** `presentation/auth/`

| Screen | Description |
|--------|-------------|
| `RoleSelectionScreen` | Entry point where users choose between Staff or Parent role |
| `StaffAuthScreen` | Gateway screen for staff with options to Login or Register |
| `StaffLoginScreen` | Staff authentication via credentials stored in Firebase |
| `StaffRegistrationScreen` | New staff registration with details saved to Firestore |
| `ParentLoginScreen` | Parent login using Child ID and Center ID to access their child's data |

**ViewModels:**
- `StaffViewModel` - Handles staff authentication state, login/logout logic
- `ParentViewModel` - Manages parent login state, child details fetching

---

### 2. Staff Dashboard Module

**Location:** `presentation/main/`, `presentation/main/dashboard/`

#### MainScreen
The primary navigation container for staff after successful login. Provides access to all management features.

#### DashboardScreen
The home screen for staff showing an overview of center statistics and quick access to all modules.

**Features:**
- Total children count
- Today's attendance summary
- Quick action buttons to navigate to different modules
- Powered by `DashboardViewModel` for data aggregation

---

### 3. Student Management Module

**Location:** `presentation/students/`, `presentation/enrollment/`

| Screen | Description |
|--------|-------------|
| `StudentsScreen` | Displays list of all enrolled children with search and filter capabilities |
| `StudentEnrollmentScreen` | Form to enroll a new child with personal, health, and parent details |

**ViewModels:**
- `StudentsViewModel` - Manages student list state, CRUD operations
- `EnrollmentViewModel` - Handles new enrollment form validation and submission

---

### 4. Attendance Module

**Location:** `presentation/attendance/`

| Screen | Description |
|--------|-------------|
| `AttendanceMarkingScreen` | Daily attendance marking interface with present/absent toggle for each child |

**ViewModel:**
- `AttendanceViewModel` - Manages attendance state, saves to Firestore, calculates summaries

---

### 5. Health & Medical Module

**Location:** `presentation/health/`

| Screen | Description |
|--------|-------------|
| `HealthRecordScreen` | View and manage health records for children including weight, height, immunization status |

---

### 6. Diet & Nutrition Module

**Location:** `presentation/diet/`

| Screen | Description |
|--------|-------------|
| `DietManagementScreen` | Create, view, and manage diet plans for children based on age groups |

**ViewModel:**
- `DietPlanViewModel` - Handles diet plan creation, storage, and retrieval

---

### 7. Activity Planning Module

**Location:** `presentation/activity/`

| Screen | Description |
|--------|-------------|
| `WeeklyActivityPlanScreen` | Plan and schedule weekly educational and recreational activities |

**ViewModel:**
- `WeeklyActivityViewModel` - Manages activity plans and schedules

---

### 8. Stock Management Module

**Location:** `presentation/stock/`

| Screen | Description |
|--------|-------------|
| `StockManagementScreen` | Track inventory of supplies (food, medicines, educational materials) |

**ViewModel:**
- `StockViewModel` - Manages stock levels, alerts for low inventory

---

### 9. Reports Module

**Location:** `presentation/reports/`

| Screen | Description |
|--------|-------------|
| `ReportsScreen` | Generate and view reports for attendance, health, nutrition, and activities |

---

### 10. Settings Module

**Location:** `presentation/settings/`

| Screen | Description |
|--------|-------------|
| `SettingsScreen` | App configuration, profile management, and preferences |

---

### 11. Parent Module

**Location:** `presentation/parent/`

| Screen | Description |
|--------|-------------|
| `ParentMainScreen` | Parent dashboard showing child's overview and quick access to all updates |
| `ParentAttendancePreviewScreen` | View child's daily and historical attendance records |
| `ParentFoodMenuScreen` | View daily/weekly food menu and meals provided to the child |
| `ParentActivityRatingScreen` | View activity participation and performance ratings |
| `ParentHealthObservationScreen` | View health observations recorded by staff |
| `ParentWeeklyHealthSummaryScreen` | Weekly health summary including weight, height, and general health notes |
| `ParentVaccinationUpdatesScreen` | Track vaccination schedule and completed immunizations |
| `ParentActivitySummaryScreen` | Weekly summary of activities the child participated in |
| `ParentWeeklyAttendanceSummaryScreen` | Weekly attendance summary with days present/absent |
| `ParentNutritionReportScreen` | Detailed nutrition report based on diet and meal tracking |

---

## Architecture

The app follows **Clean Architecture** with three distinct layers:

```
┌─────────────────────────────────────────┐
│          Presentation Layer             │
│  (UI Screens, ViewModels, Components)   │
├─────────────────────────────────────────┤
│            Domain Layer                 │
│     (Models, Repository Interfaces)     │
├─────────────────────────────────────────┤
│              Data Layer                 │
│  (Repository Implementations, Remote)   │
└─────────────────────────────────────────┘
```

### Layer Breakdown

| Layer | Package | Responsibility |
|-------|---------|----------------|
| **Presentation** | `presentation/` | UI screens, ViewModels, Compose components, theme |
| **Domain** | `domain/` | Business models, repository interfaces |
| **Data** | `data/` | Repository implementations, Firestore data source, DTOs |
| **Core** | `core/` | Utilities, constants, common types, network config, preferences |
| **DI** | `di/` | Dependency injection modules (Hilt) |

---

## Technology Stack & Tools

### Development Tools

| Tool | Purpose | Description |
|------|---------|-------------|
| **Android Studio** | IDE | Official Integrated Development Environment for Android. Provides code editing, debugging, profiling, and UI design tools. Built on IntelliJ IDEA with Android-specific features. |
| **Android Emulator** | Testing | Virtual device that simulates Android hardware on your computer. Allows testing the app on different device configurations, Android versions, and screen sizes without physical devices. |
| **Gradle** | Build System | Automated build tool that handles dependency management, compilation, packaging, and testing. Uses Kotlin DSL (`.kts` files) for build configuration. |
| **Git** | Version Control | Distributed version control system for tracking code changes, collaborating with team members, and maintaining project history. |

### Programming Languages & Frameworks

| Technology | Purpose | Description |
|------------|---------|-------------|
| **Kotlin** | Programming Language | Modern, concise, and safe programming language officially recommended for Android development. Offers null safety, coroutines for async operations, and extension functions. |
| **Jetpack Compose** | UI Toolkit | Android's modern toolkit for building native UI declaratively. Replaces XML layouts with Kotlin-based composable functions. Enables faster development with less code and built-in animations. |
| **Coroutines** | Async Programming | Kotlin's solution for asynchronous programming. Enables writing non-blocking code in a sequential style, used for network calls, database operations, and background tasks. |

### Architecture & Dependency Injection

| Library | Purpose | Description |
|---------|---------|-------------|
| **Hilt (Dagger)** | Dependency Injection | Android-specific DI library built on top of Dagger. Provides a standardized way to inject dependencies (repositories, ViewModels, Firebase instances) across the app. Reduces boilerplate and improves testability. |
| **KSP (Kotlin Symbol Processing)** | Code Generation | Annotation processing tool used by Hilt to generate dependency injection code at compile time. Faster than older KAPT processor. |
| **MVVM Pattern** | Architecture Pattern | Model-View-ViewModel pattern separates UI logic from business logic. ViewModels survive configuration changes and provide data to Compose screens via StateFlow. |
| **Clean Architecture** | Architecture Pattern | Separates code into Presentation, Domain, and Data layers. Ensures separation of concerns, testability, and maintainability. |

### Backend & Cloud Services (Firebase)

| Firebase Service | Purpose | Description |
|------------------|---------|-------------|
| **Firebase Authentication** | User Authentication | Handles user sign-in, registration, and session management. Provides secure credential storage and automatic token refresh. Used for Staff login/registration. |
| **Cloud Firestore** | NoSQL Database | Cloud-hosted NoSQL database for storing and syncing app data. Used for children records, attendance, health data, diet plans, activities, and stock. Provides real-time listeners for live data updates. |
| **Firebase Storage** | File Storage | Cloud storage for files such as child photos, health record images, and documents. Provides secure upload/download with access control. |
| **Firebase Realtime Database** | Real-time Data | JSON-based database for real-time synchronization. Used for features requiring instant updates across devices. |
| **Firebase BOM** | Version Management | Bill of Materials ensures all Firebase libraries use compatible versions, preventing dependency conflicts. |

### Local Storage

| Library | Purpose | Description |
|---------|---------|-------------|
| **DataStore Preferences** | Local Storage | Modern Android solution for storing key-value pairs and typed objects. Replaces SharedPreferences. Used for storing login state, user preferences, and session data. Provides coroutine-based async API. |

### UI Components

| Library | Purpose | Description |
|---------|---------|-------------|
| **Material 3** | Design System | Latest Material Design components for Android. Provides themed buttons, cards, dialogs, text fields, and navigation components following modern design guidelines. |
| **Material Icons Extended** | Icon Library | Extended collection of Material Design icons for use throughout the app navigation and UI elements. |
| **Compose Foundation** | UI Building Blocks | Core building blocks for Compose UI including layouts, gestures, and drawing primitives. |

---

## Project Structure

```
AnganwadiApp/
├── app/
│   └── src/main/java/com/example/anganwadiapp/
│       ├── AnganwadiApp.kt              # Application class (Hilt entry point)
│       ├── presentation/
│       │   ├── MainActivity.kt          # Main activity with navigation logic
│       │   ├── splash/                  # Splash screen
│       │   ├── role_selection/          # Role selection (Staff/Parent)
│       │   ├── auth/
│       │   │   ├── staff/               # Staff login, registration, auth
│       │   │   └── parent/              # Parent login & ViewModel
│       │   ├── main/                    # Staff main screen & navigation
│       │   │   └── dashboard/           # Dashboard screen & ViewModel
│       │   ├── students/                # Student list & ViewModel
│       │   ├── enrollment/              # New student enrollment
│       │   ├── attendance/              # Daily attendance marking
│       │   ├── health/                  # Health records management
│       │   ├── diet/                    # Diet plan management
│       │   ├── activity/                # Weekly activity planning
│       │   ├── stock/                   # Stock/inventory management
│       │   ├── reports/                 # Report generation
│       │   ├── progress/                # Progress rating screens
│       │   ├── settings/                # App settings
│       │   ├── parent/                  # Parent-specific screens (10 screens)
│       │   ├── components/              # Reusable Compose components
│       │   └── theme/                   # App theme (colors, typography)
│       ├── domain/
│       │   ├── model/                   # Domain models (Child, Staff)
│       │   └── repository/              # Repository interfaces
│       ├── data/
│       │   ├── remote/                  # Firebase data source, DTOs
│       │   └── repository/              # Repository implementations
│       ├── core/
│       │   ├── common/                  # Result wrapper, BaseViewModel
│       │   ├── constants/               # App-wide constants
│       │   ├── network/                 # Network configuration
│       │   ├── preferences/             # DataStore preferences manager
│       │   └── util/                    # Utility functions (ToastHelper)
│       └── di/                          # Hilt dependency injection modules
├── build.gradle.kts                     # Project-level build config
└── settings.gradle.kts                  # Gradle settings
```

---

## Data Flow Examples

### Staff Login Flow
1. User opens app → Splash Screen displays
2. Role Selection → User selects "Staff"
3. Staff Auth Screen → User clicks "Login"
4. Staff Login Screen → Enters credentials
5. Credentials validated against Firebase Firestore
6. On success → Login state saved to DataStore
7. User navigated to Main Dashboard

### Parent Viewing Child Attendance
1. Parent opens app → Selects "Parent" role
2. Enters Child ID and Center ID
3. Credentials validated against Firestore
4. Parent Main Screen loads with child's name
5. Parent clicks "Attendance" under Daily Updates
6. Attendance Preview Screen fetches data from Firestore
7. Attendance records displayed to parent

### Staff Marking Attendance
1. Staff logs in → Navigates to Dashboard
2. Clicks "Attendance" module
3. Attendance Marking Screen loads student list
4. Staff marks each child as Present/Absent
5. Data saved to Firestore via Repository
6. Attendance count updates on Dashboard

---

## Key Features Summary

| Feature | Staff | Parent |
|---------|-------|--------|
| Authentication | Login/Register | Login (Child ID + Center ID) |
| Dashboard | Full management view | Child overview |
| Student Management | CRUD operations | View only (own child) |
| Attendance | Mark daily attendance | View attendance history |
| Health Records | Add/edit health data | View health summaries |
| Diet Plans | Create/manage plans | View nutrition reports |
| Activity Planning | Plan weekly activities | View activity summaries |
| Stock Management | Track inventory | - |
| Reports | Generate all reports | View child-specific reports |
| Vaccination Tracking | Update records | View vaccination status |

---

## Getting Started

### Prerequisites
- Android Studio (latest stable version)
- JDK 11 or higher
- Android SDK (minSdk 24, targetSdk 35, compileSdk 36)
- Firebase project with Authentication, Firestore, Storage, and Realtime Database enabled
- `google-services.json` file placed in `app/` directory

### Build & Run
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Connect a physical device or start an emulator
5. Run the app

---

## Configuration

### Firebase Setup
1. Create a Firebase project at https://console.firebase.google.com
2. Register the Android app with package name: `com.example.anganwadiapp`
3. Download `google-services.json` and place it in the `app/` directory
4. Enable the following Firebase services:
   - Authentication (Email/Password)
   - Cloud Firestore
   - Firebase Storage
   - Realtime Database

### Firestore Collections
The app uses the following Firestore collections:
- `staff` - Staff account data
- `children` - Children enrollment records
- `attendance` - Daily attendance records
- `health_records` - Health and medical data
- `diet_plans` - Diet and nutrition plans
- `activities` - Weekly activity plans
- `stock` - Inventory items

---

## Contact

For questions or support regarding this application, please contact the development team.
