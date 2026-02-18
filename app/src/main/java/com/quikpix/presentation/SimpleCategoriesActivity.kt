package com.quikpix.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.quikpix.data.model.Category
import com.quikpix.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SimpleCategoriesActivity : ComponentActivity() {
    private val TAG = "SimpleCategoriesActivity"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Permission granted: $isGranted")
            // Restart to load categories
            recreate()
        } else {
            Log.d(TAG, "Permission denied: $isGranted")
            // Show a toast message to user
            runOnUiThread {
                android.widget.Toast.makeText(
                    this@SimpleCategoriesActivity,
                    "Permission denied. Please grant photo access in your phone settings to use this app.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
                
                // Show a dialog to open app settings
                val alertDialog = android.app.AlertDialog.Builder(this@SimpleCategoriesActivity)
                    .setTitle("Permission Required")
                    .setMessage("Photo access is required to view your images. Would you like to open app settings to grant permission?")
                    .setPositiveButton("Open Settings") { _, _ ->
                        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.parse("package:${this@SimpleCategoriesActivity.packageName}")
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                alertDialog.show()
            }
        }
    }

    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+: READ_MEDIA_VISUAL_USER_SELECTED
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13: READ_MEDIA_IMAGES
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            // Android 12 and below: READ_EXTERNAL_STORAGE
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d(TAG, "SimpleCategoriesActivity onCreate called")
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onCreate", e)
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimpleCategoriesScreen()
                }
            }
        }
    }

    @Composable
    fun SimpleCategoriesScreen() {
        val context = LocalContext.current
        val hasPermission = remember { mutableStateOf(false) }
        val categories = remember { mutableStateOf<List<Category>>(emptyList()) }
        val isLoading = remember { mutableStateOf(false) }
        val error = remember { mutableStateOf<String?>(null) }

        // Check permission on start
        LaunchedEffect(Unit) {
            val permission = getRequiredPermission()
            hasPermission.value = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission.value) {
                loadCategories(context, categories, isLoading, error)
            }
        }

        if (!hasPermission.value) {
            Log.d(TAG, "Showing PermissionScreen")
            PermissionScreen(
                onPermissionGranted = {
                    val permission = getRequiredPermission()
                    requestPermissionLauncher.launch(permission)
                }
            )
        } else {
            Log.d(TAG, "Showing MainContent with ${categories.value.size} categories")
            val coroutineScope = rememberCoroutineScope()

            MainContent(
                categories = categories.value,
                isLoading = isLoading.value,
                error = null,
                onRetry = {
                    coroutineScope.launch {
                        loadCategories(context, categories, isLoading, error)
                    }
                }
            )
        }
    }

    private suspend fun loadCategories(
        context: android.content.Context,
        categories: androidx.compose.runtime.MutableState<List<Category>>,
        isLoading: androidx.compose.runtime.MutableState<Boolean>,
        errorMessage: androidx.compose.runtime.MutableState<String?>
    ) {
        isLoading.value = true
        errorMessage.value = null

        try {
            val categoryRepository = CategoryRepository(context)
            val loadedCategories = withContext(Dispatchers.IO) {
                categoryRepository.getCategories()
            }
            categories.value = loadedCategories
        } catch (e: Exception) {
            errorMessage.value = "Failed to load categories: ${e.message}"
            Log.e(TAG, "Error loading categories", e)
        } finally {
            isLoading.value = false
        }
    }

    @Composable
    fun PermissionScreen(onPermissionGranted: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Photo Library",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Photo Access Required",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "QuikPix needs permission to access your photos to organize them into categories and albums.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        android.util.Log.d(TAG, "Grant Permission button clicked")
                        onPermissionGranted()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Permission")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        android.util.Log.d(TAG, "Cancel clicked")
                        finish()
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    }

    @Composable
    fun MainContent(
        categories: List<Category>,
        isLoading: Boolean,
        error: String?,
        onRetry: () -> Unit
    ) {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = {
                        Text(
                            "QuikPix Categories",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Loading categories...",
                                modifier = Modifier.padding(top = 16.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    error != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Error loading categories",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                error,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Button(
                                onClick = onRetry
                            ) {
                                Text("Retry")
                            }
                        }
                    }

                    categories.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No categories found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Take some photos or check permissions",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Button(
                                onClick = onRetry
                            ) {
                                Text("Refresh")
                            }
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { category ->
                                SimpleCategoryCard(
                                    category = category,
                                    onClick = {
                                        android.util.Log.d(TAG, "Navigating to category: ${category.displayName}, ID: ${category.id}")
                                        val intent = Intent(this@SimpleCategoriesActivity, CategoryDetailActivity::class.java).apply {
                                            putExtra("CATEGORY_ID", category.id)
                                            putExtra("CATEGORY_NAME", category.displayName)
                                            putStringArrayListExtra("CATEGORY_IMAGES", ArrayList(category.thumbnailUris))
                                        }
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SimpleCategoryCard(category: Category, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.9f)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Thumbnail
                if (category.thumbnailUris.isNotEmpty()) {
                    AsyncImage(
                        model = category.thumbnailUris.first(),
                        contentDescription = "Category thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Gradient overlay for text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0.6f
                            )
                        )
                )

                // Category info at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Folder name
                    Text(
                        text = category.displayName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Item count
                    Text(
                        text = category.formattedItemCount,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}