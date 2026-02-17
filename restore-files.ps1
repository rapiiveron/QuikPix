Get-ChildItem 'C:\Users\Admin\.openclaw\workspace\FastGalleryPro\app\src\main\java' -Recurse -Filter '*.bak' | ForEach-Object {
    $newName = $_.FullName -replace '\.bak$', '.kt'
    Rename-Item $_.FullName -NewName $newName
}
Write-Host "Files restored"
