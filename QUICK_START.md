# PocketLedger - Quick Start Guide

## 🚀 Get Your APK in 4 Steps

### Step 1: Install Java (Required)
1. Download JDK 17+ from [Adoptium](https://adoptium.net/)
2. Install it on your computer
3. Restart Command Prompt/PowerShell

### Step 2: Install Android Studio (Recommended)
1. Download [Android Studio](https://developer.android.com/studio)
2. Install and open it
3. Let it install the required SDK components

### Step 3: Build the App
1. Open Command Prompt or PowerShell
2. Navigate to project folder:
   ```bash
   cd C:\Users\USER\Desktop\pocket
   ```
3. Run the build command:
   ```bash
   .\gradlew.bat assembleDebug
   ```

### Step 4: Find Your APK
Your APK will be at:
```
C:\Users\USER\Desktop\pocket\app\build\outputs\apk\debug\app-debug.apk
```

## 🔧 Quick Check

Run this to check your setup:
```bash
check_setup.bat
```

## 📱 Install on Your Phone

1. Copy `app-debug.apk` to your phone
2. Enable "Install from unknown sources" in settings
3. Tap the APK file to install

## ❓ Need Help?

- Check `BUILD_GUIDE.md` for detailed instructions
- Run `check_setup.bat` to verify setup
- Ensure Java 17+ and Android Studio are installed

## 🎯 What You'll Get

- Complete expense tracking app
- Account management
- Transaction history
- Loan tracking
- Data export features
- Encrypted local storage

The app is ready to build - you just need the development tools installed!
