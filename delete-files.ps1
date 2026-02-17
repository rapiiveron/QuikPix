$filesToDelete = @(
    "Album.kt",
    "AlbumCard.kt",
    "AppModule.kt",
    "DeleteMediaUseCase.kt",
    "FastGalleryApp.kt",
    "GalleryDatabase.kt",
    "GalleryScreen.kt",
    "GalleryViewModel.kt",
    "GetAlbumsUseCase.kt",
    "GetMediaUseCase.kt",
    "HiddenMediaDao.kt",
    "MediaGrid.kt",
    "MediaItem.kt",
    "MediaPagingSource.kt",
    "MediaRepository.kt",
    "MediaRepositoryImpl.kt",
    "MediaStoreItem.kt",
    "PhotoPickerManager.kt",
    "PickerToggle.kt",
    "PickerViewModel.kt",
    "UnifiedGalleryScreen.kt",
    "UserPreferences.kt",
    "ViewerScreen.kt"
)

$baseDir = "C:\Users\Admin\.openclaw\workspace\FastGalleryPro\app\src\main\java"

foreach ($file in $filesToDelete) {
    $path = Join-Path $baseDir $file
    if (Test-Path $path) {
        Remove-Item $path -Force
        Write-Host "Deleted: $file"
    }
}

Write-Host "Done!"
