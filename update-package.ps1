# PowerShell script to update package names in all Kotlin files
$kotlinFiles = Get-ChildItem -Path ".\app\src\main\java\com\quikpix" -Recurse -Filter "*.kt"

foreach ($file in $kotlinFiles) {
    Write-Host "Updating: $($file.FullName)"
    
    # Read file content
    $content = Get-Content $file.FullName -Raw
    
    # Replace package declaration
    $content = $content -replace 'package com\.fastgallery', 'package com.quikpix'
    
    # Replace import statements
    $content = $content -replace 'import com\.fastgallery', 'import com.quikpix'
    
    # Write updated content back
    $content | Set-Content $file.FullName -NoNewline
}

Write-Host "Package update complete!"