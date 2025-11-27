@echo off
echo PocketLedger Build Setup Check
echo ==============================
echo.

echo Checking Java installation...
java -version >nul 2>&1
if %ERRORLEVEL% == 0 (
    echo [✓] Java is installed
    java -version
) else (
    echo [✗] Java is NOT installed or not in PATH
    echo Please install JDK 17+ and set JAVA_HOME
)

echo.
echo Checking JAVA_HOME environment variable...
if defined JAVA_HOME (
    echo [✓] JAVA_HOME is set to: %JAVA_HOME%
) else (
    echo [✗] JAVA_HOME is NOT set
    echo Please set JAVA_HOME environment variable
)

echo.
echo Checking ANDROID_HOME environment variable...
if defined ANDROID_HOME (
    echo [✓] ANDROID_HOME is set to: %ANDROID_HOME%
) else (
    echo [✗] ANDROID_HOME is NOT set
    echo Please install Android Studio or SDK tools
)

echo.
echo Checking Gradle wrapper...
if exist "gradlew.bat" (
    echo [✓] Gradle wrapper exists (gradlew.bat)
) else (
    echo [✗] Gradle wrapper NOT found
)

if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo [✓] Gradle wrapper JAR exists
) else (
    echo [✗] Gradle wrapper JAR NOT found
)

echo.
echo Checking project structure...
if exist "app\src\main\java\com\example\pocketledger" (
    echo [✓] Source code directory exists
) else (
    echo [✗] Source code directory NOT found
)

if exist "app\build.gradle.kts" (
    echo [✓] App build file exists
) else (
    echo [✗] App build file NOT found
)

echo.
echo ==============================
echo Build Commands:
echo ------------------------------
echo To build debug APK:
echo   .\gradlew.bat assembleDebug
echo.
echo To build release APK:
echo   .\gradlew.bat assembleRelease
echo.
echo To clean and rebuild:
echo   .\gradlew.bat clean build
echo.
echo APK will be located at:
echo   app\build\outputs\apk\debug\app-debug.apk
echo ==============================
echo.
pause
