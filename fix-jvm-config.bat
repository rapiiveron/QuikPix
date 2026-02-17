@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro

echo Cleaning build artifacts...
call gradlew.bat clean

echo Building without daemon JVM options...
call gradlew.bat assembleDebug --no-daemon

echo Build complete!
pause
