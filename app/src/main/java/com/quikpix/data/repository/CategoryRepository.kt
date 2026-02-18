package com.quikpix.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.quikpix.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val context: Context) {

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        val categoryMap = mutableMapOf<String, MutableList<Pair<Uri, Long>>>()

        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val bucketName = cursor.getString(bucketNameColumn) ?: "Unknown"
                val dateModified = cursor.getLong(dateModifiedColumn)
                val uri = ContentUris.withAppendedId(collection, id)

                val list = categoryMap.getOrPut(bucketName) { mutableListOf() }
                list.add(Pair(uri, dateModified))
            }
        }

        categoryMap.map { (name, items) ->
            Category(
                name = name,
                path = name,
                thumbnailUris = items.take(4).map { it.first },
                itemCount = items.size,
                lastModified = items.firstOrNull()?.second ?: 0L
            )
        }.sortedByDescending { it.lastModified }.take(50)
    }

    suspend fun getImagesInCategory(categoryName: String): List<Uri> =
        withContext(Dispatchers.IO) {
            val images = mutableListOf<Uri>()
            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
            )
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(categoryName)
            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

            context.contentResolver.query(
                collection, projection, selection, selectionArgs, sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    images.add(ContentUris.withAppendedId(collection, id))
                }
            }

            images
        }
}
