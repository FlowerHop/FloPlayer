package com.example.floplayer

import android.view.SurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.*

class PlayerViewModel(
    private val player: SimpleExoPlayer,
    private val videoRepository: VideoRepository
    ): ViewModel() {

    private val _playing: MutableLiveData<Boolean> = MutableLiveData(false)
    val playing: LiveData<Boolean> = _playing

    private val _currTime: MutableLiveData<Long> = MutableLiveData(0)
    val currTime: LiveData<Long> = _currTime

    private val currPlayingIndex = 0
    private val _currPlayingVideo: MutableLiveData<VideoMetaData> = MutableLiveData()
    val currPlayingVideo: LiveData<VideoMetaData?> = _currPlayingVideo

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _playing.postValue(isPlaying)
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
        }
    }

    init {
        viewModelScope.launch {
            val mediaItemList = videoRepository.fetchVideos().map { videoMetaData ->
                MediaItem.Builder()
                    .setUri(videoMetaData.contentUri)
                    .setMediaId(videoMetaData.id.toString())
                    .setTag(videoMetaData)
                    .build()
            }
            player.addMediaItems(mediaItemList)
            player.prepare()
            player.addListener(playerListener)

//            _currPlayingVideo.postValue(videoRepository.fetchVideos()[0])
//
//            while(true) {
//                if (_currTime.value != player.currentPosition/1000)
//                    _currTime.postValue(player.currentPosition/1000)
//                delay(1000)
//            }
        }
    }

    fun attach(surfaceView: SurfaceView) {
        player.setVideoSurfaceView(surfaceView)
    }

    fun detach(surfaceView: SurfaceView) {
        player.clearVideoSurfaceView(surfaceView)
    }

    fun choose(video: VideoMetaData) {

    }

    fun togglePlayPause() {
        if (_playing.value == false) {
            player.play()
        } else {
            player.pause()
        }
    }

    fun skipToNext() {
        player.seekToNext()
    }

    fun skipToPrevious() {
        player.seekToPrevious()
    }

    fun seekForward() {
        player.seekForward()
    }

    fun seekReplay() {
        player.seekBack()
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(playerListener)
    }
}