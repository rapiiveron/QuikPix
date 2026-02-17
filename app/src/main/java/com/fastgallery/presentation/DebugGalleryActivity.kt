package com.fastgallery.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fastgallery.ui.theme.FastGalleryTheme

class DebugGalleryActivity : ComponentActivity() {
    private val TAG = "FastGalleryDebug"
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "Permission granted: $isGranted")
        if (isGranted) {
            loadImagesFromStorage()
        }
    }
    
    private fun loadImagesFromStorage() {
        try {
            Log.d(TAG, "Loading images from storage")
            
            Thread {
                try {
                    val imageUris = mutableListOf<String>()
                    
                    // Try different permission approaches
                    val permissionToRequest = if (android.os.Build.VERSION.SDK_INT >= 33) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    
                    Log.d(TAG, "Using permission: $permissionToRequest")
                    Log.d(TAG, "SDK Version: ${android.os.Build.VERSION.SDK_INT}")
                    
                    // Check if we have permission
                    if (checkSelfPermission(permissionToRequest) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "No permission, requesting...")
                        runOnUiThread {
                            requestPermissionLauncher.launch(permissionToRequest)
                        }
                        return@Thread
                    }
                    
                    Log.d(TAG, "Permission granted, querying MediaStore")
                    
                    val projection = arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA
                    )

                    val selection = "${MediaStore.Images.Media.MIME_TYPE} IN (?, ?)"
                    val selectionArgs = arrayOf("image/jpeg", "image/png")

                    val cursor = contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        "${MediaStore.Images.Media.DATE_ADDED} DESC"
                    )
                    
                    if (cursor == null) {
                        Log.e(TAG, "Cursor is null")
                        return@Thread
                    }
                    
                    Log.d(TAG, "Cursor has ${cursor.count} items")
                    
                    cursor.use { 
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                        while (cursor.moveToNext()) {
                            try {
                                val id = cursor.getLong(idColumn)
                                val data = cursor.getString(dataColumn)
                                imageUris.add(data)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error reading cursor row: ${e.message}")
                            }
                        }
                    }

                    Log.d(TAG, "Found ${imageUris.size} images")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading images: ${e.message}", e)
                }
            }.start()
        } catch (e: Exception) {
            Log.e(TAG, "Fatal error in loadImagesFromStorage: ${e.message}", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity created")
        setContent {
            FastGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DebugGalleryScreen()
                }
            }
        }
    }

    @Composable
    private fun DebugGalleryScreen() {
        val isLoading = remember { mutableStateOf(true) }
        val errorMessage = remember { mutableStateOf<String?>(null) }
        val imageCount = remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            try {
                Log.d(TAG, "LaunchedEffect started")
                // Just show a simple screen for now
                isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "LaunchedEffect error: ${e.message}", e)
                errorMessage.value = "Startup error: ${e.message}"
                isLoading.value = false
            }
        }

        Scaffold(
            topBar = {
                Text(
                    text = "FastGallery Debug",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading.value) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text("Loading...", modifier = Modifier.padding(top = 16.dp))
                    }
                } else if (errorMessage.value != null) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error:", style = MaterialTheme.typography.titleMedium)
                        Text(errorMessage.value ?: "Unknown error", modifier = Modifier.padding(16.dp))
                        Button(
                            onClick = { 
                                isLoading.value = true
                                loadImagesFromStorage()
                                isLoading.value = false
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("FastGallery Debug", 
                            style = MaterialTheme.typography.headlineSmall)
                        Text("App is running!", 
                            modifier = Modifier.padding(top = 16.dp))
                        Text("If you see this, the app launched successfully",
                            modifier = Modifier.padding(top = 8.dp))
                        Button(
                            onClick = { loadImagesFromStorage() },
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            Text("Test Load Images")
                        }
                        Button(
                            onClick = { 
                                errorMessage.value = "Test error message"
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Test Error Display")
                        }
                    }
                }
            }
        }
    }
}
