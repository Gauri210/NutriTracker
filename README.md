# NutriTracker

NutriTracker is an Android nutrition tracking app built with Java, AndroidX, and Room.  
It helps users set calorie goals, log meals, track activity, and view daily nutrition progress.

## Features

- 3-step onboarding flow to collect profile and goals
- Automatic navigation to dashboard when user profile already exists
- Meal logging with food search and serving unit selection
- Daily macro and calorie aggregation
- Workout/activity logging with estimated or direct calories burned
- Progress tracking (water and calories burned)
- Local-first persistence using Room database with seeded food data

## Tech Stack

- **Language:** Java 17
- **UI:** Android Views + ViewBinding + Material Components
- **Architecture:** Activity/Fragment + ViewModel + Repository + Room
- **Database:** Room (`UserEntity`, `FoodItem`, `ServingUnit`, `MealEntry`, `DailyProgress`)
- **Build:** Gradle + Android Gradle Plugin 8.1.3

## Project Structure

```text
app/src/main/java/com/nutritracker/
├── data/
│   ├── local/           # Room DB, entities, DAOs
│   └── repository/      # NutritionRepository
├── ui/
│   ├── onboarding/      # OnboardingActivity + 3 fragments
│   ├── dashboard/       # MainActivity, HomeFragment, ProfileFragment
│   └── logging/         # LogMealActivity, WorkoutActivity
├── utils/               # NutritionCalculator, custom GraphView
└── viewmodel/           # Onboarding, dashboard, and meal logging VMs
```

## Requirements

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK:
  - `compileSdk 34`
  - `targetSdk 34`
  - `minSdk 26`

## Getting Started

1. Clone or download this project.
2. Open the root folder in Android Studio.
3. Let Gradle sync complete.
4. Build and run on an emulator/device:
   - `./gradlew assembleDebug`
   - `./gradlew installDebug` (optional)

## App Flow

1. App launches into `OnboardingActivity`.
2. If user profile exists in Room, app skips onboarding and opens dashboard.
3. User logs meals and activities.
4. Dashboard reads Room-backed `LiveData` for daily and historical progress.

## Data Notes

- Database name: `nutritracker_database`
- Room is configured with:
  - `createFromAsset("databases/nutritracker_database.db")`
  - fallback to destructive migration for schema changes
- Initial food samples are seeded for quick testing.

## Testing

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Build Config Snapshot

- Application ID: `com.nutritracker`
- Version: `1.0` (`versionCode 1`)

## License

No explicit license file is included yet. Add a `LICENSE` file if you plan to distribute this project publicly.
