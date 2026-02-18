@echo off
cd /d "C:\Users\Admin\.openclaw\workspace\FastGalleryPro\app\src\main\java\com\quikpix\presentation"

echo Cleaning up old activity files...
for %%f in (*.kt) do (
    if not "%%f"=="Phase3ReadyActivity.kt" (
        echo Deleting: %%f
        del "%%f"
    )
)

echo Cleanup complete!
pause