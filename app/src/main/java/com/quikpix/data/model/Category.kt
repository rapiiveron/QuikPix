package com.quikpix.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Long,                    // BUCKET_ID from MediaStore
    val name: String,                // BUCKET_DISPLAY_NAME (folder name)
    val path: String,                // Full folder path
    val itemCount: Int,              // Number of items in folder
    val lastModified: Long,          // Last modified timestamp
    val thumbnailUris: List<String>, // URIs for thumbnail collage (1-4 images)
    val isPinned: Boolean = false,   // Pinned to top
    val isHidden: Boolean = false    // Hidden from view
) : Parcelable {
    
    companion object {
        // Common folder names to display nicely
        private val displayNameMap = mapOf(
            "DCIM/Camera" to "Camera",
            "Pictures/Screenshots" to "Screenshots",
            "Pictures/Instagram" to "Instagram",
            "Pictures/Telegram" to "Telegram",
            "Pictures/WhatsApp" to "WhatsApp",
            "Pictures/Messenger" to "Messenger",
            "Pictures/Facebook" to "Facebook",
            "Pictures/Twitter" to "Twitter",
            "Pictures/Downloads" to "Downloads",
            "Pictures/Download" to "Downloads",
            "DCIM/Screenshots" to "Screenshots",
            "DCIM/Instagram" to "Instagram"
        )
        
        fun getDisplayName(path: String, bucketName: String): String {
            // Try to get friendly name from map
            for ((key, value) in displayNameMap) {
                if (path.contains(key, ignoreCase = true)) {
                    return value
                }
            }
            
            // Use bucket name if available, otherwise extract from path
            return if (bucketName.isNotEmpty()) {
                bucketName
            } else {
                path.substringAfterLast("/").takeIf { it.isNotEmpty() } ?: "Uncategorized"
            }
        }
    }
    
    val displayName: String
        get() = getDisplayName(path, name)
    
    val formattedItemCount: String
        get() = when (itemCount) {
            0 -> "Empty"
            1 -> "1 item"
            else -> "$itemCount items"
        }
    
    val hasItems: Boolean
        get() = itemCount > 0
}