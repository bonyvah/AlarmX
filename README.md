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
