package com.quikpix.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quikpix.data.model.Category
import com.quikpix.data.repository.CategoryRepository
import com.quikpix.presentation.screens.CategoriesScreen
import com.quikpix.presentation.screens.CategoryDetailScreen
import com.quikpix.presentation.viewmodel.CategoriesViewModel
import com.quikpix.presentation.viewmodel.CategoryDetailViewModel

class QuikPixMainActivity : ComponentActivity() {
    private val TAG = "QuikPixMainActivity"
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Permission granted")
            // Permission granted, app will load categories
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Test if logging works
        android.util.Log.d("QuikPix", "MainActivity onCreate called")

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuikPixApp()
                }
            }
        }
    }
    
    @Composable
    fun QuikPixApp() {
        val context = LocalContext.current
        val hasPermission = remember { mutableStateOf(false) }
        
        // Check permission on start
        LaunchedEffect(Unit) {
            val permission = getRequiredPermission()
            hasPermission.value = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermission.value) {
                requestPermissionLauncher.launch(permission)
            }
        }
        
        if (!hasPermission.value) {
            // Show permission request screen
            PermissionScreen {
                val permission = getRequiredPermission()
                requestPermissionLauncher.launch(permission)
            }
        } else {
            // Main app with navigation
            MainAppContent()
        }
    }
    
    @Composable
    fun MainAppContent() {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Categories) }
        var selectedCategory by remember { mutableStateOf<Category?>(null) }
        var selectedImageIndex by remember { mutableStateOf(-1) }
        var imageList by remember { mutableStateOf<List<String>>(emptyList()) }
        
        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 300),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                Screen.Categories -> {
                    val viewModel: CategoriesViewModel = viewModel(
                        factory = CategoriesViewModelFactory(
                            CategoryRepository(LocalContext.current)
                        )
                    )
                    
                    CategoriesScreen(
                        onCategoryClick = { category ->
                            selectedCategory = category
                            currentScreen = Screen.CategoryDetail
                        },
                        onCameraClick = {
                            // Open camera
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            }
                        },
                        viewModel = viewModel
                    )
                }
                
                Screen.CategoryDetail -> {
                    selectedCategory?.let { category ->
                        val viewModel: CategoryDetailViewModel = viewModel(
                            factory = CategoryDetailViewModel.provideFactory(
                                CategoryRepository(LocalContext.current),
                                category.id
                            )
                        )
                        
                        CategoryDetailScreen(
                            category = category,
                            onBackClick = {
                                currentScreen = Screen.Categories
                                selectedCategory = null
                            },
                            onImageClick = { index, images ->
                                selectedImageIndex = index
                                imageList = images
                                currentScreen = Screen.ImageViewer
                            },
                            viewModel = viewModel
                        )
                    }
                }
                
                Screen.ImageViewer -> {
                    // Reuse the existing image viewer from Phase3ReadyActivity
                    // For now, go back to category detail
                    currentScreen = Screen.CategoryDetail
                }
            }
        }
    }
    
    @Composable
    fun PermissionScreen(onRequestPermission: () -> Unit) {
        androidx.compose.material3.Card(
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
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Permission")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = { finish() }
                ) {
                    Text("Cancel")
                }
            }
        }
    }
    
    sealed class Screen {
        object Categories : Screen()
        object CategoryDetail : Screen()
        object ImageViewer : Screen()
    }
}

// Factory for CategoriesViewModel
class CategoriesViewModelFactory(
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoriesViewModel(categoryRepository) as T
    }
}