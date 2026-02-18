package com.quikpix.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quikpix.presentation.screens.CategoriesScreen
import com.quikpix.presentation.screens.CategoryDetailScreen
import com.quikpix.presentation.screens.FullscreenImageScreen
import com.quikpix.ui.theme.QuikPixTheme

class SimpleCategoriesActivity : ComponentActivity() {

    private var hasPermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions.values.any { it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasPermission = checkPermissions()
        if (!hasPermission) requestPermissions()

        setContent {
            QuikPixTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "categories") {
                        composable("categories") {
                            CategoriesScreen(
                                hasPermission = hasPermission,
                                onRequestPermission = { requestPermissions() },
                                onOpenSettings = { openAppSettings() },
                                onCategoryClick = { categoryName ->
                                    navController.navigate(
                                        "category_detail/${Uri.encode(categoryName)}"
                                    )
                                }
                            )
                        }

                        composable(
                            route = "category_detail/{categoryName}",
                            arguments = listOf(
                                navArgument("categoryName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val categoryName =
                                backStackEntry.arguments?.getString("categoryName") ?: ""
                            CategoryDetailScreen(
                                categoryName = Uri.decode(categoryName),
                                onBack = { navController.popBackStack() },
                                onImageClick = { imageUri ->
                                    navController.navigate(
                                        "fullscreen/${Uri.encode(imageUri.toString())}"
                                    )
                                }
                            )
                        }

                        composable(
                            route = "fullscreen/{imageUri}",
                            arguments = listOf(
                                navArgument("imageUri") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val encodedUri =
                                backStackEntry.arguments?.getString("imageUri") ?: ""
                            val imageUri = Uri.parse(Uri.decode(encodedUri))
                            FullscreenImageScreen(
                                imageUri = imageUri,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            this, Manifest.permission.READ_MEDIA_IMAGES
                        ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun requestPermissions() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }

    private fun openAppSettings() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        })
    }
}
