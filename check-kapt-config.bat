@echo off
echo Checking kapt configuration...
echo.

echo 1. Checking Hilt plugin version...
type app\build.gradle.kts | findstr "hilt"

echo.
echo 2. Checking kapt configuration...
type app\build.gradle.kts | findstr "kapt"

echo.
echo 3. Checking Room dependencies...
type app\build.gradle.kts | findstr "room"

echo.
echo 4. Checking coroutine dependencies...
type app\build.gradle.kts | findstr "coroutines"

echo.
echo 5. Listing all .kt files with @Database or @Dao annotations...
findstr /S /M "@Database" "app\src\main\java\com\fastgallery\*.kt"
findstr /S /M "@Dao" "app\src\main\java\com\fastgallery\*.kt"

echo.
echo 6. Checking for kaptGenerateStubsDebugKotlin task logs...
echo Run with --stacktrace for more details:
echo gradlew.bat clean assembleDebug --stacktrace

pause
