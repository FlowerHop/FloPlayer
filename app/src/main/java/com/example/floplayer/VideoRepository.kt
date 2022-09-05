package com.example.floplayer

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository(
    private val dataSource: ContentResolver
) {

    suspend fun fetchVideos(): List<VideoMetaData> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.DATA
        )
        val selection = ""
        val selectionArgs = arrayOf<String>()
        val sortOrder = ""

        val cursor: Cursor = dataSource.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        ) ?: return@withContext emptyList<VideoMetaData>()

        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

        val result = mutableListOf<VideoMetaData>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val displayName = cursor.getString(nameColumn)
            val duration = cursor.getInt(durationColumn)
            val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            val path = cursor.getString(dataColumn)
            result += VideoMetaData(
                id,
                displayName,
                duration,
                contentUri,
                path
            )
        }

        return@withContext result
    }
}