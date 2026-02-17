# Delete all .kt files except MinimalMainActivity.kt, Theme.kt, Color.kt, Type.kt
$basePath = "C:\Users\Admin\.openclaw\workspace\FastGalleryPro\app\src\main\java"
$keepFiles = @("MinimalMainActivity.kt", "Theme.kt", "Color.kt", "Type.kt")

Get-ChildItem $basePath -Recurse -Filter "*.kt" | ForEach-Object {
    if ($keepFiles -notcontains $_.Name) {
        Remove-Item $_.FullName -Force
        Write-Host "Deleted: $($_.Name)"
    }
}

Write-Host "Cleanup complete!"
