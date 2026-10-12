Kitabu — Android Book Rental & Library Management System

---

Key Features

User Authentication & Account Management
- Registration & Login: User onboarding with client-side form validation (email format, password security).
- Persistent User Session: Active user profile state management via `UserRepository` and Room `UserEntity`.
- Account Actions: Support for logging out and account deletion with cascading cleanup.

Interactive Book Catalog (`CatalogScreen`)
- Real-Time Search: Search through the library catalog instantly by book title, author, or category.
- Book Item Cards: Material 3 cards with custom badge indicators (Available vs. Borrowed) and cover image display.
- Add New Book (FAB): Floating Action Button opens a Modal Bottom Sheet form allowing users to insert new books into the SQLite database.
- Reservation Modal Sheet: Clicking any available book presents full book details, auto-calculates reservation start and 2-week return deadline, and lets the user reserve it instantly.

Reservation Dashboard (`ReservationDashboardScreen`)
- Active & Pending Bookings Overview: Relational data fetching maps reserved books directly to their respective user bookings.
- Countdown & Due Date Tracking: Real-time calculation of remaining days until return deadline (`getDaysBetween`).
- One-Tap Reservation Actions*:
  - Renew: Extends the reservation deadline by +14 days atomically via Room transactions.
  - Return: Returns the book, updates status to `RETURNED`, and restores book availability (`isAvailable = true`) in the library.
  - Cancel: Cancels pending/active reservations and restores book availability.

---

Architecture & Tech Stack

Kitabu follows Android's recommended MVVM (Model-View-ViewModel) pattern combined with the Repository Pattern and Unidirectional Data Flow (UDF).

graph TD
    A[Jetpack Compose UI Screens] -->|Events / Intents| B[AuthViewModel]
    B -->|Exposes StateFlow| A
    B --> C[LibraryRepository / UserRepository]
    C --> D[UserDao & LibraryDao]
    D --> E[(Room SQLite Database kitabu_database)]

Tech Stack & Libraries
- Language: Kotlin 2.2+
- UI Framework: Jetpack Compose & Material 3
- Navigation: Navigation Compose & Type-Safe Navigation 3 (`@Serializable`)
- Image Loading: [Coil Compose](https://coil-kt.github.io/coil/) for loading and rendering binary `BLOB` cover images (`AsyncImage`).
- Database: Room Database 2.8 (`room-runtime`, `room-ktx`, `ksp`) with pre-packaged SQLite asset integration (`createFromAsset("kitabu.db")`).
- Asynchronous & State: Kotlin Coroutines, `StateFlow`, `collectAsStateWithLifecycle()`.
- Build System: Gradle Version Catalog (`libs.versions.toml`) with KSP (Kotlin Symbol Processing).

---

Database Schema & Entities

Kitabu utilizes a relational SQLite schema powered by Room:

ER Diagram
    users ||--o{ bookings : "makes"
    books ||--o{ bookings : "reserved in"

    users {
        int userID PK
        string userName
        string email
        string password
        double outstandingFines
    }

    books {
        int bookID PK
        string title
        string author
        string category
        BLOB coverImage
        boolean isAvailable
    }

    bookings {
        int bookingID PK
        int bookOwnerID FK
        string userName
        int bookID FK
        string bookingDate
        string returnDeadline
        Status status
    }

Entities
1. `UserEntity` (`users` table): Holds user profiles and credentials.
2. `BookEntity` (`books` table): Holds book metadata and binary image cover data (`BLOB`). Features custom `equals()` and `hashCode()` implementations for Kotlin `ByteArray` comparison.
3. `BookingEntity` (`bookings` table): Holds booking records with foreign key constraints (`CASCADE` on delete/update) linking `UserEntity` and `BookEntity`. Uses `Status` enum (`PENDING`, `ACTIVE`, `RETURNED`).
4. `ActiveBookingEntity`: Relational Room embedding model mapping `BookingEntity` with its related `BookEntity` via `@Embedded` and `@Relation`.

---

Project Directory Structure

com.stadiolinks.kitabu/
├── components/               # Reusable UI components (Navigation Drawer, Top Bars)
├── data/
│   ├── database/
│   │   ├── dao/              # LibraryDao.kt, UserDao.kt
│   │   ├── entities/         # UserEntity.kt, BookEntity.kt, BookingEntity.kt, ActiveBookingEntity.kt
│   │   ├── AppDatabase.kt    # RoomDatabase singleton with pre-populated asset DB
│   │   └── Converters.kt     # TypeConverters for Enum <-> String
│   └── repository/           # LibraryRepository.kt, UserRepository.kt
├── screens/                  # Compose UI Screens
│   ├── LoginScreen.kt        # User Authentication
│   ├── RegisterScreen.kt     # User Registration
│   ├── CatalogScreen.kt      # Book browsing, filtering, search & adding books
│   └── ReservationDashboardScreen.kt # Active reservation management
├── AuthViewModel.kt          # Main ViewModel handling Auth & Library operations
└── MainActivity.kt           # App Entry Point & Navigation Setup

---

Getting Started

Prerequisites
- Android Studio: Ladybug (2024.2.1) or newer
- JDK: Java 11 or Java 17
- Android SDK: Min SDK 26 (Android 8.0) | Target SDK 35/37

Installation & Setup

1. Clone the Repository:

   git clone https://github.com/your-username/kitabu.git
   cd kitabu

2. Open in Android Studio:
   Open Android Studio, choose Open, and select the `kitabu` project directory.

3. Build & Sync Project:
   Let Gradle sync dependencies automatically. If needed, click Sync Project with Gradle Files.

4. Run the Application:
   Select an Android Emulator or physical device (API Level 26+) and click Run 'app' (`Shift + F10`).

---

Author & Acknowledgments

Developed by Connor Curry as a native Android project demonstrating modern Android architecture with Jetpack Compose & Room.
