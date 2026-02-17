@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro

echo Restoring full features from backup-files...

REM Restore Hilt and dependency injection files
move "backup-files\FastGalleryApp.kt" "app\src\main\java\com\fastgallery\"
move "backup-files\AppModule.kt" "app\src\main\java\com\fastgallery\di\"
move "backup-files\GalleryViewModel.kt" "app\src\main\java\com\fastgallery\presentation\viewmodel\"
move "backup-files\PickerViewModel.kt" "app\src\main\java\com\fastgallery\presentation\viewmodel\"

REM Restore Compose UI files
move "backup-files\UnifiedGalleryScreen.kt" "app\src\main\java\com\fastgallery\presentation\screens\"
move "backup-files\GalleryScreen.kt" "app\src\main\java\com\fastgallery\presentation\screens\"
move "backup-files\ViewerScreen.kt" "app\src\main\java\com\fastgallery\presentation\screens\"
move "backup-files\PickerToggle.kt" "app\src\main\java\com\fastgallery\presentation\components\"
move "backup-files\AlbumCard.kt" "app\src\main\java\com\fastgallery\presentation\components\"
move "backup-files\MediaGrid.kt" "app\src\main\java\com\fastgallery\presentation\components\"

REM Restore domain files
move "backup-files\Album.kt" "app\src\main\java\com\fastgallery\domain\model\"
move "backup-files\MediaItem.kt" "app\src\main\java\com\fastgallery\domain\model\"
move "backup-files\DeleteMediaUseCase.kt" "app\src\main\java\com\fastgallery\domain\usecase\"
move "backup-files\GetAlbumsUseCase.kt" "app\src\main\java\com\fastgallery\domain\usecase\"
move "backup-files\GetMediaUseCase.kt" "app\src\main\java\com\fastgallery\domain\usecase\"

REM Restore data files
move "backup-files\MediaRepositoryImpl.kt" "app\src\main\java\com\fastgallery\data\repository\"
move "backup-files\MediaPagingSource.kt" "app\src\main\java\com\fastgallery\data\repository\"
move "backup-files\MediaRepository.kt" "app\src\main\java\com\fastgallery\data\repository\"
move "backup-files\PhotoPickerManager.kt" "app\src\main\java\com\fastgallery\data\picker\"

REM Restore Room database files
move "backup-files\GalleryDatabase.kt" "app\src\main\java\com\fastgallery\data\local\database\"
move "backup-files\HiddenMediaDao.kt" "app\src\main\java\com\fastgallery\data\local\database\"
move "backup-files\UserPreferences.kt" "app\src\main\java\com\fastgallery\data\local\prefs\"
move "backup-files\MediaStoreItem.kt" "app\src\main\java\com\fastgallery\data\model\"

echo Full features restored!
echo.
echo Re-enabling Hilt and Room dependencies...
echo.

echo Restoring build.gradle.kts...
copy "backup-files\build.gradle.kts" "app\build.gradle.kts" /Y

echo.
echo Restoring proguard-rules.pro...
copy "backup-files\proguard-rules.pro" "app\proguard-rules.pro" /Y

echo.
echo Restoring AndroidManifest.xml...
copy "backup-files\AndroidManifest.xml" "app\src\main\AndroidManifest.xml" /Y

echo.
echo Restoring launcher icons...
xcopy "backup-files\res" "app\src\main\res" /E /I /Y

echo.
echo Restoring theme resources...
xcopy "backup-files\res" "app\src\main\res" /E /I /Y

echo.
echo Restoring strings.xml...
copy "backup-files\res\values\strings.xml" "app\src\main\res\values\strings.xml" /Y

echo.
echo Restoring colors.xml...
copy "backup-files\res\values\colors.xml" "app\src\main\res\values\colors.xml" /Y

echo.
echo Restoring themes.xml...
copy "backup-files\res\values\themes.xml" "app\src\main\res\values\themes.xml" /Y

echo.
echo All files restored successfully!
echo.
echo Next steps:
echo 1. Open in Android Studio to sync dependencies
echo 2. Build: gradlew.bat clean assembleDebug
echo 3. Install: gradlew.bat installDebug
echo 4. Run: gradlew.bat app:installDebug
echo.
pause
