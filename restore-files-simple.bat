@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro

echo Restoring files from backup-files...

REM Restore Hilt and ViewModel files
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

echo All files restored!
echo.
pause
