package com.example.floplayer

import android.net.Uri

data class VideoMetaData(
    val id: Long,
    val displayName: String,
    val duration: Int,
    val contentUri: Uri,
    val path: String
)