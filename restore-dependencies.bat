@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro

echo Re-enabling Hilt and Room dependencies...

REM Restore build.gradle.kts
copy "backup-files\build.gradle.kts" "app\build.gradle.kts" /Y

REM Restore proguard-rules.pro
copy "backup-files\proguard-rules.pro" "app\proguard-rules.pro" /Y

REM Restore AndroidManifest.xml
copy "backup-files\AndroidManifest.xml" "app\src\main\AndroidManifest.xml" /Y

REM Restore theme resources
xcopy "backup-files\res" "app\src\main\res" /E /I /Y

echo Dependencies restored!
echo.
pause
