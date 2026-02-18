package com.quikpix.presentation.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.quikpix.viewmodel.CategoryDetailUiState
import com.quikpix.viewmodel.CategoryDetailViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullscreenImageScreen(
    categoryName: String,
    initialIndex: Int,
    onBack: () -> Unit,
    viewModel: CategoryDetailViewModel = viewModel()
) {
    // Only load if data is not already present (avoids double-load when ViewModel is
    // shared with the CategoryDetail back-stack entry)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(categoryName) {
        if (uiState !is CategoryDetailUiState.Success) {
            viewModel.loadImages(categoryName)
        }
    }

    val images = (uiState as? CategoryDetailUiState.Success)?.images ?: emptyList()

    // rememberPagerState must be called unconditionally (Compose rules)
    val pagerState = rememberPagerState(
        initialPage = if (images.isNotEmpty()) initialIndex.coerceIn(0, images.size - 1) else 0
    ) { images.size }

    // Tracks the zoom level of the current page so the pager swipe can be disabled
    // while zoomed in (the pan gesture takes over horizontal movement instead).
    var currentPageScale by remember { mutableStateOf(1f) }
    LaunchedEffect(pagerState.currentPage) {
        currentPageScale = 1f  // defensive reset whenever the page settles
    }

    var showControls by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = currentPageScale <= 1f,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            if (images.isNotEmpty()) {
                ZoomablePage(
                    uri = images[page],
                    onScaleChanged = { scale ->
                        if (page == pagerState.currentPage) currentPageScale = scale
                    },
                    onTap = { showControls = !showControls }
                )
            }
        }

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

/**
 * A single zoomable image page inside the pager.
 *
 * Gesture layering (outer Box for taps, inner Box for pinch/pan, graphicsLayer on image):
 *  - Outer Box  -> detectTapGestures (single-tap toggles UI, double-tap zooms)
 *  - Inner Box  -> transformable (pinch-to-zoom + pan while zoomed)
 *  - AsyncImage -> graphicsLayer (visual scale/translation)
 */
@Composable
private fun ZoomablePage(
    uri: Uri,
    onScaleChanged: (Float) -> Unit,
    onTap: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        offset = if (scale <= 1f) Offset.Zero else offset + panChange
        onScaleChanged(scale)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                        onScaleChanged(scale)
                    },
                    onTap = { onTap() }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformableState)
        ) {
            AsyncImage(
                model = uri,
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
    }
}
