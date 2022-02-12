package com.example.floplayer

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.coroutineContext

class VideoRepository(private val dataSource: ContentResolver) {
    private val videoList: MutableList<VideoMetaData> = mutableListOf()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var cursor: Cursor? = null

    suspend fun fetchVideos(): List<VideoMetaData> {
        if (cursor == null) {
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.DATA
            )
            val selection = ""
            val selectionArgs = arrayOf<String>()
            val sortOrder = ""

            dataSource.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.let {
                cursor = it
            }
        }
        cursor?.let {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            val result = mutableListOf<VideoMetaData>()
            var size = 25

            while (size >= 0 && it.moveToNext()) {
                size--

                val id = it.getLong(idColumn)
                val displayName = it.getString(nameColumn)
                val duration = it.getInt(durationColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val path = it.getString(dataColumn)

                result += VideoMetaData(
                    id,
                    displayName,
                    duration,
                    contentUri,
                    path
                )
            }

            return result
        } ?: return listOf()
    }
}