package com.example.floplayer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class ExoPlayerService: Service() {
    private lateinit var player: SimpleExoPlayer

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)


    override fun onCreate() {
        super.onCreate()
        player = SimpleExoPlayer.Builder(this).build()
    }

    override fun onBind(intent: Intent?): IBinder {
        return ExoPlayerBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        player.release()
    }

    inner class ExoPlayerBinder: Binder(), ExoPlayerProvider {
        override fun getExoPlayerInstance() = player
    }
}

interface ExoPlayerProvider {
    fun getExoPlayerInstance(): SimpleExoPlayer
}