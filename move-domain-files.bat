@echo off
cd C:\Users\Admin\.openclaw\workspace\FastGalleryPro
move "app\src\main\java\com\fastgallery\di\AppModule.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\domain\model\Album.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\domain\model\MediaItem.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\domain\usecase\DeleteMediaUseCase.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\domain\usecase\GetAlbumsUseCase.kt" "backup-files\"
move "app\src\main\java\com\fastgallery\domain\usecase\GetMediaUseCase.kt" "backup-files\"
echo Domain files moved to backup-files
