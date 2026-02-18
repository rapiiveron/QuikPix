package com.quikpix.presentation.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Full-screen image viewer with pinch-to-zoom, pan, and double-tap zoom.
 *
 * Previously removed due to compilation issues caused by:
 *  1. Stacking `transformable` and `pointerInput` on the same composable — fixed by
 *     separating gesture surfaces: transformable lives on the inner image Box, while
 *     detectTapGestures lives on the outer container Box so they never compete for events.
 *  2. Using `mutableFloatStateOf` (Compose 1.5+ only) without the correct BOM — now
 *     using plain `mutableStateOf` for the scale Float to be safe.
 *  3. Wrong import for `Icons.AutoMirrored` — requires `material-icons-extended` dep.
 */
@Composable
fun FullscreenImageScreen(
    imageUri: Uri,
    onBack: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showControls by remember { mutableStateOf(true) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        // Reset offset when back at 1x so the image re-centers cleanly
        offset = if (scale <= 1f) Offset.Zero else offset + panChange
    }

    // Outer Box: intercepts single/double taps without conflicting with pinch-to-zoom
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                    },
                    onTap = {
                        showControls = !showControls
                    }
                )
            }
    ) {
        // Inner Box: owns transformable so pan/pinch events are fully isolated from taps
        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformableState)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Fullscreen image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        }

        // Overlay back button — fades in/out on tap
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .systemBarsPadding()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}
