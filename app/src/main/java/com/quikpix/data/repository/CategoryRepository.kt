package com.quikpix.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.quikpix.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val context: Context) {
    private val TAG = "CategoryRepository"
    
    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        val categories = mutableListOf<Category>()
        val categoryMap = mutableMapOf<Long, MutableCategoryData>()
        
        // Query for images
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_TAKEN
        )
        
        val imageSortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        
        val imageCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )
        
        imageCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val bucketId = cursor.getLong(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn) ?: ""
                val data = cursor.getString(dataColumn) ?: ""
                val dateModified = cursor.getLong(dateModifiedColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                
                // Extract folder path from file path
                val folderPath = if (data.contains("/")) {
                    data.substring(0, data.lastIndexOf("/"))
                } else {
                    ""
                }
                
                // Get or create category data
                val categoryData = categoryMap.getOrPut(bucketId) {
                    MutableCategoryData(
                        id = bucketId,
                        name = bucketName,
                        path = folderPath,
                        itemCount = 0,
                        lastModified = 0L,
                        thumbnailUris = mutableListOf()
                    )
                }
                
                // Update category data
                categoryData.itemCount++
                categoryData.lastModified = maxOf(categoryData.lastModified, dateModified, dateTaken)

                // Add image URIs (limit to 20 per category to prevent crashes)
                if (categoryData.thumbnailUris.size < 20) {
                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        .buildUpon()
                        .appendPath(id.toString())
                        .build()
                        .toString()
                    categoryData.thumbnailUris.add(contentUri)
                }
            }
        }
        
        imageCursor?.close()
        
        // Query for videos (optional - can be added later)
        // Similar logic for videos...
        
        // Convert mutable data to Category objects
        for ((bucketId, data) in categoryMap) {
            if (data.itemCount > 0 && data.path.isNotEmpty()) {
                categories.add(
                    Category(
                        id = data.id,
                        name = data.name,
                        path = data.path,
                        itemCount = data.itemCount,
                        lastModified = data.lastModified,
                        thumbnailUris = data.thumbnailUris,
                        isPinned = false,
                        isHidden = false
                    )
                )
            }
        }
        
        Log.d(TAG, "Found ${categories.size} categories")
        
        // Sort by last modified (most recent first)
        return@withContext categories.sortedByDescending { it.lastModified }
    }
    
    private data class MutableCategoryData(
        val id: Long,
        val name: String,
        val path: String,
        var itemCount: Int,
        var lastModified: Long,
        val thumbnailUris: MutableList<String>
    )
    
    suspend fun getImagesInCategory(categoryId: Long): List<String> = withContext(Dispatchers.IO) {
        val imageUris = mutableListOf<String>()
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID
        )
        
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(categoryId.toString())
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val limit = 500  // Limit to 500 images to prevent crashes
        
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "$sortOrder LIMIT $limit"
        )
        
        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    .buildUpon()
                    .appendPath(id.toString())
                    .build()
                    .toString()
                imageUris.add(contentUri)
            }
        }
        
        cursor?.close()
        
        return@withContext imageUris
    }
}