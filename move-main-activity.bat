@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro
move "app\src\main\java\com\fastgallery\presentation\MainActivity.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\presentation\SimpleMainActivity.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\presentation\screens\GalleryScreen.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\presentation\screens\ViewerScreen.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\presentation\components\PickerToolbar.kt" "backup-files\"
echo MainActivity files moved to backup-files
