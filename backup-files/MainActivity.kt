package com.fastgallery.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.fastgallery.presentation.screens.GalleryScreen
import com.fastgallery.ui.theme.FastGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for FastGallery Pro
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher: androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize permission launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Some permissions were denied. Please grant them in settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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

        // Request permissions on app start
        requestPermissionsIfNeeded()
    }

    private fun getRequiredPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun hasAllPermissions(): Boolean {
        val permissions = getRequiredPermissions()
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionsIfNeeded() {
        if (!hasAllPermissions()) {
            permissionLauncher.launch(getRequiredPermissions().toTypedArray())
        }
    }
}
