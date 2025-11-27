# PocketLedger

PocketLedger is a personal local expense tracker for Android. It is an offline-first, single-user app with no login required.

## Features
- **Track Expenses & Income**: Categorize and tag your transactions.
- **Multiple Accounts**: Manage Cash, Bank, and other accounts.
- **Loans**: Track money lent to or borrowed from friends.
- **Offline & Secure**: All data is stored locally on your device, encrypted with SQLCipher.
- **Backup & Restore**: Create local backups of your data.
- **Export**: Export your transactions to CSV.
- **Material 3 Design**: Modern UI with dark mode support.
- **Quick Add**: Fast transaction entry from the main screen.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Clean Architecture principles
- **Dependency Injection**: Hilt
- **Local Storage**: Room Database (with SQLCipher encryption)
- **Asynchronicity**: Coroutines & Flow
- **Navigation**: Compose Navigation

## Build Instructions
1. Open the project in Android Studio.
2. Sync Gradle files.
3. Run the app on an emulator or physical device (Android 7.0+ / API 24+).

## Quick Start
1. **First Launch**: The app will start with an empty database.
2. **Add Sample Data**: Long-press the total balance card on the dashboard to access debug tools, then tap "Seed Sample Data".
3. **Add Accounts**: Navigate to Accounts screen to add your financial accounts.
4. **Add Transactions**: Use the floating action button (+) to add transactions quickly.
5. **Track Loans**: Visit the Loans screen to manage money lent/borrowed.

## Testing
- **Unit Tests**: Run `./gradlew test` to execute unit tests.
- **UI Tests**: Run `./gradlew connectedAndroidTest` to execute instrumented tests.
- **Debug Tools**: Long-press total balance on dashboard to access debug screen for data seeding/clearing.

## Data Model
- **Account**: Cash/Bank accounts with balances
- **Transaction**: Expenses, income, and transfers with categories and tags
- **LoanRecord**: Track loans with outstanding amounts
- **RecurringRule**: Set up recurring transactions (future feature)

## Security
- All data is encrypted using SQLCipher
- No network permissions required
- Local-only storage with optional backup

## Backup & Restore
- **Backup**: Go to Settings to create encrypted local backups.
- **Restore**: Use Settings to restore from backup files.
- **Warning**: Restore replaces all current data.

## Development Status
This is a work-in-progress implementation with:
- ✅ Core data models and database
- ✅ Basic dashboard with Material 3 design
- ✅ Navigation structure
- ✅ Debug tools for testing
- 🚧 Transaction management UI
- 🚧 Account management UI
- 🚧 Loan tracking UI
- 🚧 Settings and backup features

## License
MIT
