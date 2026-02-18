package com.quikpix.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.provider.MediaStore
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class Phase3ReadyActivity : ComponentActivity() {
    private val TAG = "Phase3ReadyActivity"
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Permission granted")
            // Restart activity to load photos
            finish()
            startActivity(intent)
        } else {
            Log.d(TAG, "Permission denied")
        }
    }
    
    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            } else {
                Manifest.permission.READ_MEDIA_IMAGES
            }
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    
    private fun loadDevicePhotos(context: android.content.Context, imageUrls: MutableList<String>) {
        // Query MediaStore for device photos
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.SIZE
        )
        
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val contentUri = android.net.Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                
                Log.d(TAG, "Found photo: $name, URI: $contentUri")
                
                // Add to image URLs list
                imageUrls.add(contentUri.toString())
                
                // Limit to 50 photos for performance
                if (imageUrls.size >= 50) {
                    break
                }
            }
        }
        
        cursor?.close()
        
        if (imageUrls.isEmpty()) {
            Log.d(TAG, "No photos found in MediaStore")
            // Fallback to sample images if no device photos found
            repeat(10) { index ->
                imageUrls.add("https://picsum.photos/400/600?random=$index")
            }
        }
    }
    
    private fun loadImagesFromStorage() {
        // For now, use sample images
        // Phase 3 will load real device photos
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GalleryApp()
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GalleryApp() {
        val context = LocalContext.current
        
        // State variables
        val imageUrls = remember { mutableStateListOf<String>() }
        val isLoading = remember { mutableStateOf(true) }
        val errorMessage = remember { mutableStateOf<String?>(null) }
        val hasPermission = remember { mutableStateOf(false) }
        var selectedImageIndex by remember { mutableIntStateOf(-1) }
        var showUI by remember { mutableStateOf(true) }
        var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
        var targetScale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        
        // Animated scale for smooth zoom transitions
        val animatedScale by animateFloatAsState(
            targetValue = targetScale,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "zoomAnimation"
        )
        
        // Check permission on start
        LaunchedEffect(Unit) {
            val permission = getRequiredPermission()
            hasPermission.value = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            
            if (hasPermission.value) {
                // Load real device photos
                loadDevicePhotos(context, imageUrls)
                isLoading.value = false
            } else {
                // Request permission
                requestPermissionLauncher.launch(permission)
            }
        }
        
        // Auto-hide UI after 5 seconds of inactivity
        LaunchedEffect(selectedImageIndex, showUI, lastInteractionTime) {
            if (selectedImageIndex >= 0 && showUI) {
                delay(5000)
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastInteractionTime >= 5000) {
                    showUI = false
                }
            }
        }
        
        // Handle back navigation properly
        BackHandler(enabled = selectedImageIndex >= 0) {
            // When in viewer mode, back should exit viewer
            selectedImageIndex = -1
            targetScale = 1f
            offsetX = 0f
            offsetY = 0f
            showUI = true
            lastInteractionTime = System.currentTimeMillis()
        }
        
        Scaffold(
            topBar = {
                if (selectedImageIndex < 0) {
                    TopAppBar(
                        title = { 
                            Text(
                                "QuikPix", 
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        actions = {
                            IconButton(
                                onClick = {
                                    showUI = !showUI
                                    lastInteractionTime = System.currentTimeMillis()
                                }
                            ) {
                                Icon(
                                    imageVector = if (showUI) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showUI) "Hide UI" else "Show UI",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    )
                }
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
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Loading gallery...", 
                            modifier = Modifier.padding(top = 16.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        )
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
                                errorMessage.value = null
                                isLoading.value = false
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                } else if (!hasPermission.value) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Permission Required", style = MaterialTheme.typography.titleMedium)
                        Text("This app needs permission to access your photos", 
                            modifier = Modifier.padding(16.dp))
                        Button(
                            onClick = { 
                                requestPermissionLauncher.launch(getRequiredPermission())
                            }
                        ) {
                            Text("Grant Permission")
                        }
                    }
                } else if (imageUrls.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No Images Found", style = MaterialTheme.typography.titleMedium)
                        Text("Try taking some photos or check permissions",
                            modifier = Modifier.padding(16.dp))
                        Button(
                            onClick = { 
                                isLoading.value = true
                                isLoading.value = false
                            }
                        ) {
                            Text("Reload")
                        }
                    }
                } else {
                    // Image Viewer Screen
                    if (selectedImageIndex >= 0) {
                        Crossfade(
                            targetState = selectedImageIndex,
                            animationSpec = tween(durationMillis = 300),
                            label = "ImageTransition"
                        ) { currentIndex ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { 
                                                showUI = !showUI
                                                lastInteractionTime = System.currentTimeMillis()
                                            },
                                            onDoubleTap = { 
                                                // Smooth zoom toggle with animation
                                                if (targetScale <= 1.1f) {
                                                    targetScale = 2.5f
                                                } else {
                                                    targetScale = 1f
                                                    offsetX = 0f
                                                    offsetY = 0f
                                                }
                                                lastInteractionTime = System.currentTimeMillis()
                                            }
                                        )
                                    }
                                    .pointerInput(targetScale) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            // Update target scale for smooth animation
                                            targetScale = (targetScale * zoom).coerceIn(1f, 5f)
                                            if (targetScale > 1.1f) {
                                                offsetX += pan.x
                                                offsetY += pan.y
                                            }
                                            lastInteractionTime = System.currentTimeMillis()
                                        }
                                    }
                                    .pointerInput(targetScale) {
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                offsetX = 0f
                                            }
                                        ) { change, dragAmount ->
                                            if (targetScale <= 1.1f) {
                                                offsetX += dragAmount
                                                change.consume()
                                                
                                                // Only navigate if drag exceeds threshold
                                                if (offsetX < -150f && selectedImageIndex < imageUrls.size - 1) {
                                                    selectedImageIndex++
                                                    targetScale = 1f
                                                    offsetX = 0f
                                                    offsetY = 0f
                                                } else if (offsetX > 150f && selectedImageIndex > 0) {
                                                    selectedImageIndex--
                                                    targetScale = 1f
                                                    offsetX = 0f
                                                    offsetY = 0f
                                                }
                                            }
                                            lastInteractionTime = System.currentTimeMillis()
                                        }
                                    }
                            ) {
                                // Main image with smooth zoom animation
                                AsyncImage(
                                    model = imageUrls[currentIndex],
                                    contentDescription = "Image $currentIndex",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            scaleX = animatedScale
                                            scaleY = animatedScale
                                            translationX = offsetX
                                            translationY = offsetY
                                        }
                                        .clipToBounds()
                                )
                                
                                // UI Overlay
                                AnimatedVisibility(
                                    visible = showUI,
                                    enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                                    exit = fadeOut(animationSpec = tween(durationMillis = 300))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        // Top bar with image counter
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .padding(top = 16.dp)
                                        ) {
                                            Text(
                                                text = "${currentIndex + 1}/${imageUrls.size}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        
                                        // Zoom hint (top right)
                                        if (targetScale > 1.1f) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(top = 16.dp, end = 16.dp)
                                            ) {
                                                Text(
                                                    text = "Zoomed ${String.format("%.1f", targetScale)}x",
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                        
                                        // Navigation buttons (left/right)
                                        // Left arrow (only show if not first image)
                                        if (currentIndex > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .padding(start = 16.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        selectedImageIndex--
                                                        targetScale = 1f
                                                        offsetX = 0f
                                                        offsetY = 0f
                                                        lastInteractionTime = System.currentTimeMillis()
                                                    },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Previous image",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Right arrow (only show if not last image)
                                        if (currentIndex < imageUrls.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd)
                                                    .padding(end = 16.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        selectedImageIndex++
                                                        targetScale = 1f
                                                        offsetX = 0f
                                                        offsetY = 0f
                                                        lastInteractionTime = System.currentTimeMillis()
                                                    },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = "Next image",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Bottom controls
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(bottom = 80.dp)
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                // Swipe hint
                                                Text(
                                                    text = "← Swipe to navigate →",
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                                
                                                Row {
                                                    // Close button (X)
                                                    IconButton(
                                                        onClick = {
                                                            selectedImageIndex = -1
                                                            targetScale = 1f
                                                            offsetX = 0f
                                                            offsetY = 0f
                                                            showUI = true
                                                            lastInteractionTime = System.currentTimeMillis()
                                                        },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Close viewer",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(32.dp)
                                                        )
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    
                                                    // Hide UI button
                                                    IconButton(
                                                        onClick = {
                                                            showUI = false
                                                            lastInteractionTime = System.currentTimeMillis()
                                                        },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.VisibilityOff,
                                                            contentDescription = "Hide UI",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Gallery Grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(imageUrls) { index, imageUrl ->
                                Card(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    selectedImageIndex = index
                                                    Log.d(TAG, "Clicked image $index")
                                                }
                                            )
                                        }
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Gallery image $index",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .aspectRatio(0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
