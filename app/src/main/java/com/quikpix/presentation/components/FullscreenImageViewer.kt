package com.quikpix.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.max
import kotlin.math.min

@Composable
fun FullscreenImageViewer(
    imageUrl: String,
    contentDescription: String? = null,
    onDismiss: () -> Unit,
    backgroundColor: Color = Color.Black
) {
    // Use a fullscreen overlay instead of Dialog for better gesture handling
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        var showControls by remember { mutableStateOf(true) }
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset(0f, 0f)) }

        // Handle back button
        BackHandler(onBack = {
            if (scale > 1.1f) {
                // Reset zoom first if zoomed in
                scale = 1f
                offset = Offset(0f, 0f)
            } else {
                onDismiss()
            }
        })

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            // Double tap to zoom in/out
                            if (scale > 1.5f) {
                                // Reset zoom
                                scale = 1f
                                offset = Offset(0f, 0f)
                            } else {
                                // Zoom to 3x at tap location
                                scale = 3f
                                // Center on tap point
                                val containerSize = size
                                val centerX = containerSize.width / 2
                                val centerY = containerSize.height / 2
                                
                                offset = Offset(
                                    x = (tapOffset.x - centerX) * (1f - 1f / 3f),
                                    y = (tapOffset.y - centerY) * (1f - 1f / 3f)
                                )
                            }
                        },
                        onTap = {
                            // Toggle controls visibility
                            showControls = !showControls
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        // Handle pinch-to-zoom
                        val newScale = max(1f, min(5f, scale * zoom))

                        // Handle panning
                        val newOffset = if (newScale == 1f) {
                            Offset(0f, 0f)
                        } else {
                            val containerSize = size
                            val maxX = (containerSize.width * (newScale - 1)) / 2
                            val maxY = (containerSize.height * (newScale - 1)) / 2

                            // Calculate new offset with panning
                            val newOffsetX = offset.x + pan.x * newScale
                            val newOffsetY = offset.y + pan.y * newScale

                            Offset(
                                x = max(-maxX, min(maxX, newOffsetX)),
                                y = max(-maxY, min(maxY, newOffsetY))
                            )
                        }

                        scale = newScale
                        offset = newOffset
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Image with zoom and pan applied
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
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

            // Close button overlay
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300)),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}