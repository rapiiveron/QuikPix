package com.fastgallery.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fastgallery.ui.theme.FastGalleryTheme

class SimpleGalleryActivity : ComponentActivity() {
    private var onLoadImagesCallback: (() -> Unit)? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onLoadImagesCallback?.invoke()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FastGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GalleryScreen()
                }
            }
        }
    }

    @Composable
    private fun GalleryScreen() {
        val images = remember { mutableStateOf<List<String>>(emptyList()) }
        val isLoading = remember { mutableStateOf(true) }

        fun loadImagesFromStorage() {
            isLoading.value = true
            Thread {
                val imageUris = mutableListOf<String>()
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA
                )

                val selection = "${MediaStore.Images.Media.MIME_TYPE} IN (?, ?)"
                val selectionArgs = arrayOf("image/jpeg", "image/png")

                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.Images.Media.DATE_ADDED} DESC"
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val data = cursor.getString(dataColumn)
                        imageUris.add(data)
                    }
                }

                runOnUiThread {
                    images.value = imageUris
                    isLoading.value = false
                }
            }.start()
        }

        onLoadImagesCallback = { loadImagesFromStorage() }

        LaunchedEffect(Unit) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadImagesFromStorage()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        Scaffold(
            topBar = {
                Text(
                    text = "My Gallery",
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (images.value.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No images found")
                        Button(
                            onClick = { loadImagesFromStorage() }
                        ) {
                            Text("Refresh")
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(images.value) { imageUri ->
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
