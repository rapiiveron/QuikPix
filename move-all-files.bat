@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro
move "app\src\main\java\com\fastgallery\data\local\database\GalleryDatabase.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\data\local\database\HiddenMediaDao.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\data\local\prefs\UserPreferences.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\data\model\MediaStoreItem.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\data\picker\PhotoPickerManager.kt" "backup-files\"
echo All remaining files moved to backup-files
