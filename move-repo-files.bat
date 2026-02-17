@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro
move "app\src\main\java\com\fastgallery\data\repository\MediaPagingSource.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\data\repository\MediaRepository.kt" "backup-files\"
echo Repository files moved to backup-files
