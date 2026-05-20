AlarmX is a mobile alarm application that requires users to complete cognitive challenges (e.g., arithmetic problems) to dismiss alarms.
The app allows users to create and manage alarms, customize difficulty levels, and configure preferences such as snooze behavior and sound settings.

When an alarm triggers, a full-screen interface appears over the lock screen, forcing the user to solve dynamically generated tasks. The application ensures reliable alarm execution even under background restrictions and device idle states, while maintaining a clean and scalable architecture.

Kotlin
Jetpack Compose — UI
ViewModel — state management
Navigation (Compose Navigation)
MVVM + Repository Pattern
Separation of Concerns
Room Database — storing alarms
DataStore — user preferences (difficulty, settings)
WorkManager — rescheduling alarms (e.g., after reboot)
AlarmManager + BroadcastReceiver — alarm triggering
Hilt — dependency management

# AlarmX Stage Presentation Guide

This guide is structured to help you present the AlarmX codebase on stage. It is organized from the data layer up to the user interface, followed by the main entry points.

---

## 🌟 Pitch (The "Why")
> "AlarmX isn't just an alarm app; it's a wake-up call for your brain. To dismiss an alarm, the user has to solve randomly generated arithmetic tasks. It is built using modern Android development practices: Clean Architecture, Jetpack Compose, Dagger/Hilt, and Kotlin Coroutines/Flows."

---

## 📂 The Folders: Data to UI (Alphabetical Order)

### 1. `data/` — The Storage & Persistence Layer
This folder handles data retrieval and local storage.
* **`db/` (Local Database):**
  * **`AppDatabase.kt`:** Declares the SQLite Room database schema (`AlarmXDatabase`).
  * **`AlarmEntity.kt` & `AlarmDao.kt`:** Defines the table schema for alarms and translates Kotlin operations into SQL queries (insert, query, delete).
  * **`AlarmMapper.kt`:** Translates the database-friendly `AlarmEntity` to the pure-Kotlin business logic model `Alarm` (separating details like DB IDs from domain concepts).
  * **`RepeatDaysConverters.kt`:** Converts repeat day lists (e.g., Monday, Wednesday) into a single database-friendly string.
* **`prefs/` (App Settings):**
  * **`PreferencesRepositoryImpl.kt`:** Stores global configurations like default snooze duration, overall challenge difficulty (EASY, MEDIUM, HARD), and dark theme settings.
* **`repository/` (Repository Orchestrator):**
  * **`DefaultAlarmRepository.kt`:** Binds the database and the system scheduler together. When an alarm is updated in the DB, this repo automatically reschedules it in the Android OS.

### 2. `di/` — Dependency Injection
Keeps the code loosely coupled, modular, and easy to test.
* **`DataModule.kt` / `DomainModule.kt` / `RepositoryModule.kt`:** Uses Dagger Hilt to declare how dependencies (like the Room database instance, repositories, and the random math task generator) are instantiated and injected into ViewModels and services.

### 3. `domain/` — The Core Business Logic (Framework-Free)
This folder is pure Kotlin. It has no Android dependencies and describes the business rules.
* **`challenge/` (The Puzzles):**
  * **`ArithmeticTaskGenerator.kt`:** The brain behind the challenges. Based on difficulty level, it generates random addition, subtraction, or multiplication puzzles (e.g., single digits for EASY, double digits for HARD).
* **`model/` (The Blueprint):**
  * **`Alarm.kt` & `ArithmeticTask.kt`:** Data structures representing what an alarm is (time, label, repeat days) and what a puzzle consists of (operands, operators, and the expected answer).
* **`repository/` (The Contracts):**
  * Defines repository interfaces so the domain logic doesn't care whether the database is Room, Firestore, or in-memory.
* **`usecase/` (The User Actions):**
  * Single-purpose classes like `SubmitAnswerUseCase` (validates the puzzle answer and dismisses the alarm if correct), `SnoozeUseCase`, and `CreateAlarmUseCase`.
* **`util/` (Calculators):**
  * **`AlarmScheduling.kt`:** Calculates the exact epoch millisecond timestamp for when the alarm should fire next, factoring in day-of-week recurrence.

### 4. `system/` — Android OS Integration
Bridges our clean domain logic with Android services.
* **`alarm/` (Triggering & Audio):**
  * **`AlarmManagerScheduler.kt`:** Schedules precise wakes with Android's system `AlarmManager` using `setAlarmClock` or fallback APIs.
  * **`AlarmBroadcastReceiver.kt`:** Catches the OS alarm broadcast signal and wakes up the application when it's time.
  * **`AlarmRingtoneService.kt`:** A background Android service that plays the alarm sound and handles device vibration with a foreground notification.
* **`boot/` (Survivability):**
  * **`BootCompletedReceiver.kt` & `AlarmRescheduleWorker.kt`:** Ensures alarms persist across device reboots. As soon as the phone turns on, this re-registers all active alarms in `AlarmManager`.

### 5. `ui/` — The Presentation Layer (Jetpack Compose)
Where everything is styled and made interactive.
* **`alarm/` (ViewModels & Screens):**
  * **`AlarmViewModel.kt`:** Manages app states (e.g., active alarm, current math question, state of user input) and processes UI events.
  * **`list/AlarmListScreen.kt`:** The main screen listing alarms with quick toggles to enable/disable them.
  * **`editor/AlarmEditorScreen.kt`:** Custom UI with circular time pickers and repeat day checkmarks.
  * **`dismiss/DismissScreen.kt`:** The lock screen overlay containing the math problem and an input display.
* **`common/` (Shared Widgets):**
  * **`AxNumberPad.kt`:** A custom numerical keyboard built inside Compose to let users quickly type mathematical solutions.
* **`theme/` (Aesthetics):**
  * Curated colors, Outfit fonts, and modern layouts for a cohesive, premium look.

---

## ⚡ The Main Files (The Orchestrators)

These two files tie the entire system together at startup.

### 🚀 `AlarmXApp.kt` (The Launcher)
* **What it is:** The application context class.
* **What it does:** Instantiates Dagger Hilt dependency trees and sets up the Android system notification channel (so system overlays work correctly).

### 🎬 `MainActivity.kt` (The Stage)
* **What it is:** The entry point screen activity.
* **What it does:** 
  1. Configures the window manager to bypass the lock screen (`setShowWhenLocked` and `setTurnScreenOn`), allowing the alarm UI to take over immediately even when locked.
  2. Dynamically reads system themes (Dark/Light Mode).
  3. Detects specific manufacturer behavior (like Xiaomi & Huawei) and guides users to enable "Autostart" permissions, ensuring the alarm isn't aggressively terminated by OEM battery savers.
