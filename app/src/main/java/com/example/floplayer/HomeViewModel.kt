package com.example.floplayer

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val videoRepository: VideoRepository
): ViewModel() {

    private val _videoList: MutableLiveData<List<VideoMetaData>> = MutableLiveData()
    val videoList: LiveData<List<VideoMetaData>> = _videoList

    private var isLoading = false

    init {
        isLoading = true
        viewModelScope.launch {
            _videoList.postValue(
                videoRepository.fetchVideos()
            )
            isLoading = false
        }
    }

    fun scrollToLoadMoreIfNeed(position: Int) {
        if (isLoading) return
        if (position > videoList.value!!.size - 20) {
            isLoading = true
            viewModelScope.launch {
                _videoList.postValue(_videoList.value !! + videoRepository.fetchVideos())
                isLoading = false
            }
        }
    }
}