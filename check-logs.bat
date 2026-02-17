@echo off
echo Checking for crash logs...
echo.

echo 1. Checking for logcat output...
echo Run this in your terminal to see crash logs:
echo adb logcat | findstr "FastGallery"

echo.
echo 2. Checking if app is installed...
adb shell pm list packages | findstr "com.fastgallery"

echo.
echo 3. Checking app permissions...
adb shell dumpsys package com.fastgallery | findstr "runtime permissions"

echo.
echo 4. Checking if activity is registered...
adb shell dumpsys package com.fastgallery | findstr "MinimalMainActivity"

echo.
echo If the app is installed but won't open, try:
echo   a. Uninstall the app: adb uninstall com.fastgallery
echo   b. Rebuild and reinstall: gradlew.bat clean assembleDebug installDebug
echo   c. Check for runtime errors using adb logcat
pause
