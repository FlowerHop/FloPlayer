package com.example.floplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.floplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.SimpleExoPlayer

class MainActivity : AppCompatActivity(), ExoPlayerProvider {
    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: SimpleExoPlayer? = null
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRequestPermission.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (PackageManager.PERMISSION_GRANTED == checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            afterStorePermissionGranted()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, ExoPlayerService::class.java).also { intent ->
            bindService(intent, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    exoPlayer = (service as? ExoPlayerService.ExoPlayerBinder)?.getExoPlayerInstance()

                    playerViewModel = ViewModelProvider(this@MainActivity, object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PlayerViewModel(exoPlayer!!,
                                VideoRepository(contentResolver)
                            ) as T
                        }

                    })[PlayerViewModel::class.java]
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    exoPlayer = null
                }
            }, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer = null
    }

    private fun afterStorePermissionGranted() {
        if (supportFragmentManager.findFragmentByTag(HomeFragment.TAG) != null) return
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, HomeFragment::class.java, null, HomeFragment.TAG)
            commit()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                afterStorePermissionGranted()
            } else {
                Toast.makeText(this, "No permission to get the files.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun getExoPlayerInstance(): SimpleExoPlayer {
        return exoPlayer!!
    }
}
