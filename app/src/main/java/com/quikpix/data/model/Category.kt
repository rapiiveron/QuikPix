package com.quikpix.data.model

import android.net.Uri

data class Category(
    val name: String,
    val path: String,
    val thumbnailUris: List<Uri>,
    val itemCount: Int,
    val lastModified: Long
)
