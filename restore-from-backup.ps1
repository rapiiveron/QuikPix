$sourceDir = 'C:\Users\Admin\.openclaw\workspace\FastGalleryPro\app\src\main\java'
$backupDir = 'C:\Users\Admin\.openclaw\workspace\FastGalleryPro\backup-files'

# Restore all .bak files
Get-ChildItem $sourceDir -Recurse -Filter '*.bak' | ForEach-Object {
    $ktFile = $_.FullName -replace '\.bak$', '.kt'
    if (!(Test-Path $ktFile)) {
        Copy-Item $_.FullName -Destination $ktFile
    }
}

Write-Host "Files restored from backup"
