# PocketLedger - Build Guide

## Prerequisites

Before you can build the PocketLedger app, you need to install the following:

### 1. Java Development Kit (JDK)
- **Required Version**: JDK 17 or higher
- **Download**: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
- **Installation**: 
  - Download and install JDK 17+
  - Set JAVA_HOME environment variable
  - Add JDK bin directory to PATH

### 2. Android SDK
- **Option A**: Install Android Studio (Recommended)
  - Download [Android Studio](https://developer.android.com/studio)
  - Install and open Android Studio
  - Install required SDK components (API level 34 recommended)
  
- **Option B**: Command Line Setup
  - Download [Command Line Tools](https://developer.android.com/studio#command-tools)
  - Set ANDROID_HOME environment variable
  - Add platform-tools to PATH

### 3. Environment Variables Setup
```bash
# Windows (Command Prompt)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set ANDROID_HOME=C:\Users\YourUser\AppData\Local\Android\Sdk
set PATH=%PATH%;%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools

# Windows (PowerShell)
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:ANDROID_HOME="C:\Users\YourUser\AppData\Local\Android\Sdk"
$env:PATH="$env:PATH;$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools"
```

## Building the App

### Option 1: Using Gradle Wrapper (Recommended)

1. **Open Command Prompt or PowerShell**
2. **Navigate to Project Directory**
   ```bash
   cd C:\Users\USER\Desktop\pocket
   ```

3. **Build Debug APK**
   ```bash
   # Windows
   .\gradlew.bat assembleDebug
   
   # Or if you have Git Bash/WSL
   ./gradlew assembleDebug
   ```

4. **Build Release APK (Requires signing)**
   ```bash
   .\gradlew.bat assembleRelease
   ```

### Option 2: Using Android Studio

1. **Open Android Studio**
2. **Select "Open an Existing Project"**
3. **Navigate to**: `C:\Users\USER\Desktop\pocket`
4. **Wait for Gradle sync to complete**
5. **Build → Build Bundle(s) / APK(s) → Build APK(s)**

## APK Locations

After building successfully, you'll find the APK files at:

### Debug APK
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```
app/build/outputs/apk/release/app-release.apk
```

## Quick Test Build

To test if everything is set up correctly:

1. **Test Gradle Wrapper**
   ```bash
   .\gradlew.bat --version
   ```

2. **Check Dependencies**
   ```bash
   .\gradlew.bat dependencies
   ```

3. **Clean Build**
   ```bash
   .\gradlew.bat clean build
   ```

## Troubleshooting

### Common Issues

1. **"JAVA_HOME is not set"**
   - Install JDK 17+
   - Set JAVA_HOME environment variable
   - Restart Command Prompt/PowerShell

2. **"ANDROID_HOME is not set"**
   - Install Android Studio or SDK tools
   - Set ANDROID_HOME environment variable
   - Add platform-tools to PATH

3. **"Failed to find target with version string"**
   - Install required Android SDK platform in Android Studio SDK Manager
   - Required: API level 34 (Android 14)

4. **"Gradle sync failed"**
   - Check internet connection
   - Try `.\gradlew.bat clean`
   - Delete `.gradle` folder and retry

5. **"Build failed with compilation errors"**
   - Check that all files are present
   - Run `.\gradlew.bat clean build --stacktrace` for detailed errors

## Installation

### Installing Debug APK
1. Enable "Install from unknown sources" on your Android device
2. Transfer `app-debug.apk` to your device
3. Tap on the APK file to install

### Installing Release APK
1. Same as debug APK
2. Release APK is optimized and smaller
3. Requires signing configuration for Play Store

## Project Structure

```
pocket/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/pocketledger/
│   │   │   ├── data/          # Data models, DAOs, repositories
│   │   │   ├── di/            # Dependency injection
│   │   │   ├── ui/            # UI screens and components
│   │   │   └── MainActivity.kt
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts       # App-level build configuration
├── gradle/
│   └── wrapper/               # Gradle wrapper files
├── gradlew                    # Unix/Linux/Mac gradle script
├── gradlew.bat                # Windows gradle script
├── build.gradle.kts           # Root build configuration
└── settings.gradle.kts        # Gradle settings
```

## Features Included

- ✅ Dashboard with balance overview
- ✅ Account management (bank accounts, cash)
- ✅ Transaction tracking (income, expense, transfers)
- ✅ Loan management (lend/borrow tracking)
- ✅ Search and filter transactions
- ✅ Data export to CSV
- ✅ Encrypted local storage (SQLCipher)
- ✅ Material 3 UI design
- ✅ Debug tools for testing

## Next Steps

1. Install prerequisites (JDK 17+, Android SDK)
2. Set up environment variables
3. Run `.\gradlew.bat assembleDebug`
4. Install and test the APK
5. Customize the app as needed

## Support

If you encounter any issues:
1. Check the troubleshooting section above
2. Review the build logs for specific error messages
3. Ensure all prerequisites are properly installed
4. Verify environment variables are set correctly
