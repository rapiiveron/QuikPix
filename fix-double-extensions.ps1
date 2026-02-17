Get-ChildItem 'C:\Users\Admin\.openclaw\workspace\FastGalleryPro\app\src\main\java' -Recurse -Filter '*.kt.kt' | ForEach-Object {
    $newName = $_.FullName -replace '\.kt\.kt$', '.kt'
    Rename-Item $_.FullName -NewName $newName
}
Write-Host "Fixed double extensions"
