# HabitShare Development Plan

## Executive Summary

This document outlines a comprehensive plan to build HabitShare, a habit tracking app with social sharing capabilities. The app allows users to create habits with flexible scheduling, track progress, and share achievements with friends.

---

## Architecture Overview

### Recommended Architecture: **MVVM + Clean Architecture**

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │   Screens   │  │  ViewModels │  │     UI State Classes    │ │
│  │  (Compose)  │◄─┤  (StateFlow)│◄─┤  (Data/Sealed Classes)  │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DOMAIN LAYER                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │  Use Cases  │  │  Entities   │  │  Repository Interfaces  │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         DATA LAYER                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ Repositories│  │   Room DB   │  │    Data Models/DTOs     │ │
│  │   (Impl)    │  │   (DAOs)    │  │                         │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### Tech Stack

| Layer | Technology |
|-------|------------|
| UI Framework | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| State Management | ViewModel + StateFlow |
| Dependency Injection | Hilt |
| Local Database | Room |
| Background Work | WorkManager |
| Notifications | AlarmManager + NotificationCompat |
| Date/Time | java.time (API 26+) / ThreeTenABP |
| Testing | JUnit5 + MockK + Compose Testing |

---

## Project Structure

```
app/src/main/java/com/habit/habitshare/
├── di/                          # Dependency Injection
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── data/                        # Data Layer
│   ├── local/
│   │   ├── HabitShareDatabase.kt
│   │   ├── dao/
│   │   │   ├── HabitDao.kt
│   │   │   ├── CheckInDao.kt
│   │   │   └── ReminderDao.kt
│   │   ├── entity/
│   │   │   ├── HabitEntity.kt
│   │   │   ├── CheckInEntity.kt
│   │   │   └── ReminderEntity.kt
│   │   └── converter/
│   │       └── TypeConverters.kt
│   └── repository/
│       ├── HabitRepositoryImpl.kt
│       └── CheckInRepositoryImpl.kt
│
├── domain/                      # Domain Layer
│   ├── model/
│   │   ├── Habit.kt
│   │   ├── CheckIn.kt
│   │   ├── Reminder.kt
│   │   ├── FrequencyType.kt
│   │   └── CheckInStatus.kt
│   ├── repository/
│   │   ├── HabitRepository.kt
│   │   └── CheckInRepository.kt
│   └── usecase/
│       ├── habit/
│       │   ├── CreateHabitUseCase.kt
│       │   ├── GetHabitsUseCase.kt
│       │   ├── UpdateHabitUseCase.kt
│       │   └── DeleteHabitUseCase.kt
│       ├── checkin/
│       │   ├── LogCheckInUseCase.kt
│       │   ├── GetCheckInsUseCase.kt
│       │   └── CalculateStreakUseCase.kt
│       └── stats/
│           ├── CalculateOverallPercentageUseCase.kt
│           └── GetMonthlyStatsUseCase.kt
│
├── presentation/                # Presentation Layer
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   └── Screen.kt
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   └── components/
│   │   │       ├── HabitCard.kt
│   │   │       ├── WeekRow.kt
│   │   │       └── QuickActionMenu.kt
│   │   ├── create/
│   │   │   ├── CreateHabitScreen.kt
│   │   │   ├── CreateHabitViewModel.kt
│   │   │   └── components/
│   │   │       ├── FrequencySelector.kt
│   │   │       ├── DayPicker.kt
│   │   │       └── ReminderSection.kt
│   │   ├── checkin/
│   │   │   ├── CheckInScreen.kt
│   │   │   ├── CheckInViewModel.kt
│   │   │   └── components/
│   │   │       └── StatusSelector.kt
│   │   └── calendar/
│   │       ├── CalendarScreen.kt
│   │       ├── CalendarViewModel.kt
│   │       └── components/
│   │           ├── MonthGrid.kt
│   │           └── DayCell.kt
│   └── components/              # Shared Components
│       ├── HabitShareTopBar.kt
│       └── LoadingIndicator.kt
│
├── notification/                # Notification System
│   ├── ReminderScheduler.kt
│   ├── ReminderReceiver.kt
│   └── NotificationHelper.kt
│
├── ui/theme/                    # Theme (existing)
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
├── util/                        # Utilities
│   ├── DateUtils.kt
│   └── Extensions.kt
│
├── HabitShareApp.kt             # Application class
└── MainActivity.kt              # Entry point
```

---

## Phase 1: Foundation & Core Infrastructure

### 1.1 Setup Dependencies

Add to `gradle/libs.versions.toml`:

```toml
[versions]
# Existing...
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"
room = "2.6.1"
navigationCompose = "2.8.9"
lifecycleViewModelCompose = "2.8.7"
coroutines = "1.8.1"

[libraries]
# Existing...
# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# Navigation
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Lifecycle
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewModelCompose" }

# Coroutines
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

[plugins]
# Existing...
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "2.0.21-1.0.28" }
```

### 1.2 Database Schema

```kotlin
// HabitEntity.kt
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    val frequencyType: FrequencyType,      // DAILY, SPECIFIC_DAYS, PER_WEEK, PER_MONTH
    val frequencyValue: Int?,               // For PER_WEEK (1-7) or PER_MONTH (1-31)
    val specificDays: String?,              // JSON: [0,1,3,5] for Mon,Tue,Thu,Sat
    val isPrivate: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

// CheckInEntity.kt
@Entity(
    tableName = "check_ins",
    foreignKeys = [ForeignKey(
        entity = HabitEntity::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: Long,                         // Epoch day (LocalDate.toEpochDay)
    val status: CheckInStatus,              // SUCCESS, FAIL, SKIP
    val comment: String?,
    val createdAt: Long,
    val updatedAt: Long
)

// ReminderEntity.kt
@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(
        entity = HabitEntity::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val timeHour: Int,                      // 0-23
    val timeMinute: Int,                    // 0-59
    val days: String,                       // JSON: [0,1,2,3,4,5,6] for days
    val isEnabled: Boolean = true
)
```

### 1.3 Domain Models & Enums

```kotlin
// FrequencyType.kt
enum class FrequencyType {
    DAILY,          // Every day
    SPECIFIC_DAYS,  // Selected days of week
    PER_WEEK,       // X times per week
    PER_MONTH       // X times per month
}

// CheckInStatus.kt
enum class CheckInStatus {
    SUCCESS,        // Green - Completed
    FAIL,           // Red - Missed
    SKIP            // Grey - Excused
}
```

### 1.4 Deliverables
- [ ] Hilt DI setup with AppModule, DatabaseModule, RepositoryModule
- [ ] Room database with all entities, DAOs, and type converters
- [ ] Repository interfaces and implementations
- [ ] Base domain models
- [ ] Navigation graph skeleton

---

## Phase 2: Habit Creation & Configuration

### 2.1 Create Habit Screen

**UI Components:**

```
┌────────────────────────────────────────┐
│  ←  Create Habit              [Save]   │  ◄── TopAppBar
├────────────────────────────────────────┤
│                                        │
│  Habit Title *                         │
│  ┌────────────────────────────────┐   │
│  │ Enter habit name...            │   │
│  └────────────────────────────────┘   │
│                                        │
│  Description                           │
│  ┌────────────────────────────────┐   │
│  │ Optional details...            │   │  ◄── Multi-line
│  └────────────────────────────────┘   │
│                                        │
│  ─────────── Frequency ───────────    │
│                                        │
│  ┌──────┐ ┌────────┐ ┌────┐ ┌────┐   │
│  │Daily │ │Specific│ │/Wk │ │/Mo │   │  ◄── SegmentedButton
│  └──────┘ └────────┘ └────┘ └────┘   │
│                                        │
│  [Dynamic content based on selection]  │
│                                        │
│  ─────────── Reminders ───────────    │
│                                        │
│  ┌────────────────────────────────┐   │
│  │  ⏰ 08:00 AM    [S M T W T F S]│   │
│  │                          [✕]   │   │
│  └────────────────────────────────┘   │
│                                        │
│  [+ Add Reminder]                      │
│                                        │
│  ─────────── Privacy ─────────────    │
│                                        │
│  ┌────────────────────────────────┐   │
│  │  🔒 Private  ○  👥 Shared  ○   │   │
│  └────────────────────────────────┘   │
│                                        │
└────────────────────────────────────────┘
```

### 2.2 Frequency Selector Logic

| Type | UI Component | Data Stored |
|------|--------------|-------------|
| Daily | Simple label "Every day" | `frequencyType = DAILY` |
| Specific Days | 7 toggle buttons (S M T W T F S) | `specificDays = "[1,3,5]"` |
| Per Week | Dropdown 1-7 | `frequencyValue = 3` |
| Per Month | Dropdown 1-31 | `frequencyValue = 15` |

### 2.3 Reminder Time Picker

```kotlin
// State for reminders
data class ReminderUiState(
    val id: Long = 0,
    val hour: Int = 8,
    val minute: Int = 0,
    val selectedDays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val isEnabled: Boolean = true
)
```

### 2.4 Deliverables
- [ ] CreateHabitScreen with all form fields
- [ ] CreateHabitViewModel with form validation
- [ ] FrequencySelector component with 4 modes
- [ ] DayPicker toggle component
- [ ] TimePicker dialog integration
- [ ] ReminderSection with add/remove capability
- [ ] Privacy toggle
- [ ] CreateHabitUseCase implementation
- [ ] Input validation (title required, valid frequency)

---

## Phase 3: Home Dashboard & Habit Display

### 3.1 Home Screen Layout

```
┌────────────────────────────────────────┐
│  HabitShare                      [+]   │  ◄── TopAppBar with FAB
├────────────────────────────────────────┤
│                                        │
│  ┌────────────────────────────────┐   │
│  │ 🏃 Morning Run          🔒    │   │  ◄── Habit Card
│  │ 🔥 12 Streak   📊 87% Overall │   │
│  │                                │   │
│  │  M   T   W   T   F   S   S    │   │  ◄── Week header
│  │ [●] [●] [●] [○] [○] [○] [○]   │   │  ◄── Tappable circles
│  │  ✓   ✓   ✗                    │   │
│  └────────────────────────────────┘   │
│                                        │
│  ┌────────────────────────────────┐   │
│  │ 📚 Read 30 Minutes      👥    │   │
│  │ 🔥 5 Streak    📊 72% Overall │   │
│  │                                │   │
│  │  M   T   W   T   F   S   S    │   │
│  │ [●] [●] [●] [○] [○] [○] [○]   │   │
│  │  ✓   ─   ✓                    │   │
│  └────────────────────────────────┘   │
│                                        │
│  ┌────────────────────────────────┐   │
│  │ 💧 Drink 8 Glasses      🔒    │   │
│  │ 🔥 30 Streak   📊 95% Overall │   │
│  │                                │   │
│  │  M   T   W   T   F   S   S    │   │
│  │ [●] [●] [●] [○] [○] [○] [○]   │   │
│  │  ✓   ✓   ✓                    │   │
│  └────────────────────────────────┘   │
│                                        │
└────────────────────────────────────────┘
```

### 3.2 Week Row Component

```kotlin
@Composable
fun WeekRow(
    startOfWeek: LocalDate,
    checkIns: Map<LocalDate, CheckInStatus>,
    onDayClick: (LocalDate) -> Unit
)
```

**Day Circle States:**
| State | Background | Icon | Border |
|-------|------------|------|--------|
| Success | Green | ✓ | None |
| Fail | Red | ✗ | None |
| Skip | Grey | ─ | None |
| Future | Transparent | None | Grey dashed |
| Today (empty) | Transparent | None | Blue solid |
| Past (empty) | Light grey | None | None |

### 3.3 Quick Action Menu (Popup)

```
        ┌─────────────────┐
        │  ✓   ✗   ─  ... │
        │ Suc Fail Skip   │
        └─────────────────┘
              ▲
              │
            [Day]
```

### 3.4 Deliverables
- [ ] HomeScreen with LazyColumn of habits
- [ ] HomeViewModel with habits + checkIns flow
- [ ] HabitCard component with metrics
- [ ] WeekRow component with interactive days
- [ ] QuickActionMenu popup
- [ ] GetHabitsUseCase implementation
- [ ] GetCheckInsForWeekUseCase implementation
- [ ] CalculateStreakUseCase implementation
- [ ] CalculateOverallPercentageUseCase implementation

---

## Phase 4: Check-In System

### 4.1 Quick Check-In Flow

```
User taps day → QuickActionMenu appears → User selects status → Status saved
```

### 4.2 Detailed Check-In Screen

```
┌────────────────────────────────────────┐
│  ←  Check-In                  [Save]   │
├────────────────────────────────────────┤
│                                        │
│           Thursday, Jan 15             │
│                                        │
│  ─────────────── Status ─────────────  │
│                                        │
│   ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐  │
│   │  ✓  │  │  ✗  │  │  ─  │  │  🗑  │  │
│   │     │  │     │  │     │  │     │  │
│   │ Suc │  │Fail │  │Skip │  │Clear│  │
│   └─────┘  └─────┘  └─────┘  └─────┘  │
│                                        │
│  ─────────────── Notes ──────────────  │
│                                        │
│  ┌────────────────────────────────┐   │
│  │                                │   │
│  │ Add notes about today...      │   │
│  │                                │   │
│  │                                │   │
│  └────────────────────────────────┘   │
│                                        │
└────────────────────────────────────────┘
```

### 4.3 Deliverables
- [ ] CheckInScreen with status selector
- [ ] CheckInViewModel
- [ ] StatusSelector component (4 options)
- [ ] Comment text field
- [ ] LogCheckInUseCase (create/update/delete)
- [ ] Quick action integration with HomeScreen

---

## Phase 5: Calendar & Progress View

### 5.1 Calendar Screen

```
┌────────────────────────────────────────┐
│  ←  Morning Run              [Edit]    │
├────────────────────────────────────────┤
│                                        │
│     ◄    January 2026    ►            │
│                                        │
│   S   M   T   W   T   F   S           │
│  ┌───┬───┬───┬───┬───┬───┬───┐       │
│  │   │   │   │ 1 │ 2 │ 3 │ 4 │       │
│  │   │   │   │ ● │ ● │ ● │ ● │       │
│  ├───┼───┼───┼───┼───┼───┼───┤       │
│  │ 5 │ 6 │ 7 │ 8 │ 9 │10 │11 │       │
│  │ ● │ ● │ ✗ │ ● │ ● │ ─ │ ● │       │
│  ├───┼───┼───┼───┼───┼───┼───┤       │
│  │12 │13 │14 │15 │16 │17 │18 │       │
│  │ ● │ ● │ ● │[●]│   │   │   │       │ ◄── [●] = Selected
│  ├───┼───┼───┼───┼───┼───┼───┤       │
│  │19 │20 │21 │22 │23 │24 │25 │       │
│  │   │   │   │   │   │   │   │       │
│  ├───┼───┼───┼───┼───┼───┼───┤       │
│  │26 │27 │28 │29 │30 │31 │   │       │
│  │   │   │   │   │   │   │   │       │
│  └───┴───┴───┴───┴───┴───┴───┘       │
│                                        │
│  ───────── January 15 ─────────       │
│                                        │
│  ✓ Success                            │
│  "Had a great morning session!"        │
│                                        │
└────────────────────────────────────────┘
```

### 5.2 Calendar Legend
- Green dot (●): Success
- Red dot (✗): Fail
- Grey dash (─): Skip
- Empty: No check-in or future

### 5.3 Deliverables
- [ ] CalendarScreen with month navigation
- [ ] CalendarViewModel with month data
- [ ] MonthGrid component
- [ ] DayCell component with status indicator
- [ ] Selected day detail panel
- [ ] GetMonthlyStatsUseCase implementation

---

## Phase 6: Reminder & Notification System

### 6.1 Notification Architecture

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│ ReminderScheduler│────►│   AlarmManager   │────►│ ReminderReceiver │
│  (Schedule/Cancel) │     │ (System Service) │     │ (BroadcastReceiver) │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                                                          │
                                                          ▼
                                                 ┌─────────────────┐
                                                 │NotificationHelper│
                                                 │ (Show notification)│
                                                 └─────────────────┘
```

### 6.2 Notification Content

```
┌────────────────────────────────────────┐
│ 🏃 Time for: Morning Run              │
│ Keep your streak going! Day 12 🔥      │
│                        [Check In]      │
└────────────────────────────────────────┘
```

### 6.3 Permissions Required

```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

### 6.4 Deliverables
- [ ] ReminderScheduler service
- [ ] ReminderReceiver BroadcastReceiver
- [ ] NotificationHelper with channel setup
- [ ] BootReceiver to reschedule after reboot
- [ ] Permission handling flow
- [ ] Deep link to check-in from notification

---

## Phase 7: Edit & Delete Habits

### 7.1 Edit Habit Screen
- Reuse CreateHabitScreen with pre-filled data
- Add delete button with confirmation dialog

### 7.2 Swipe Actions on Home
- Swipe left to delete
- Swipe right to edit

### 7.3 Deliverables
- [ ] Edit mode for CreateHabitScreen
- [ ] UpdateHabitUseCase implementation
- [ ] DeleteHabitUseCase implementation
- [ ] Confirmation dialog component
- [ ] SwipeToDismiss integration

---

## Phase 8: Theme & Polish

### 8.1 Color System Update

```kotlin
// Color.kt - HabitShare specific colors
object HabitShareColors {
    // Status colors
    val Success = Color(0xFF4CAF50)      // Green
    val Fail = Color(0xFFF44336)         // Red
    val Skip = Color(0xFF9E9E9E)         // Grey
    val Empty = Color(0xFFE0E0E0)        // Light Grey

    // UI colors
    val Primary = Color(0xFF2196F3)      // Blue
    val PrimaryDark = Color(0xFF1976D2)
    val Accent = Color(0xFF03A9F4)

    // Background
    val Surface = Color(0xFFFAFAFA)
    val SurfaceDark = Color(0xFF121212)
}
```

### 8.2 Deliverables
- [ ] Custom color scheme implementation
- [ ] Dark mode support
- [ ] Animations (card expand, status change)
- [ ] Empty states with illustrations
- [ ] Loading skeletons
- [ ] Error handling UI

---

## Phase 9: Testing

### 9.1 Unit Tests
- [ ] All UseCases
- [ ] ViewModels
- [ ] Repository implementations
- [ ] Date/calculation utilities

### 9.2 Integration Tests
- [ ] Room database operations
- [ ] Navigation flows

### 9.3 UI Tests
- [ ] Habit creation flow
- [ ] Check-in flow
- [ ] Calendar navigation

---

## Phase 10: Future Enhancements (Post-MVP)

### 10.1 Social Features
- Friend system with invitations
- Shared habit visibility
- Activity feed
- Accountability partners

### 10.2 Advanced Analytics
- Weekly/monthly reports
- Trend graphs
- Best performing days
- Habit correlations

### 10.3 Gamification
- Achievements/badges
- Milestone celebrations
- Longest streak records

### 10.4 Data Sync
- Cloud backup (Firebase)
- Cross-device sync
- Export/import data

---

## Development Timeline

| Phase | Description | Priority |
|-------|-------------|----------|
| Phase 1 | Foundation & Infrastructure | Critical |
| Phase 2 | Habit Creation | Critical |
| Phase 3 | Home Dashboard | Critical |
| Phase 4 | Check-In System | Critical |
| Phase 5 | Calendar View | High |
| Phase 6 | Notifications | High |
| Phase 7 | Edit/Delete | Medium |
| Phase 8 | Theme & Polish | Medium |
| Phase 9 | Testing | High |
| Phase 10 | Future (Post-MVP) | Low |

---

## Key Implementation Notes

### State Management Pattern

```kotlin
// ViewModel pattern
class HomeViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val logCheckInUseCase: LogCheckInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().collect { habits ->
                _uiState.update { it.copy(habits = habits, isLoading = false) }
            }
        }
    }
}
```

### Navigation Structure

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateHabit : Screen("create_habit")
    object EditHabit : Screen("edit_habit/{habitId}")
    object CheckIn : Screen("check_in/{habitId}/{date}")
    object Calendar : Screen("calendar/{habitId}")
}
```

---

## Success Criteria

1. User can create habits with all 4 frequency types
2. User can set multiple reminders per habit
3. User can quickly log check-ins from home screen
4. User can view detailed check-in with comments
5. User can view monthly calendar with color-coded status
6. Streak and overall percentage calculate correctly
7. Notifications fire at scheduled times
8. App handles configuration changes gracefully
9. Dark mode works correctly
10. All critical paths have test coverage

---

*Document Version: 1.0*
*Created: 2026-02-02*
