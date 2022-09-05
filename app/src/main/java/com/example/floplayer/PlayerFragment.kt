package com.example.floplayer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.floplayer.databinding.FragmentPlayerBinding

class PlayerFragment: Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerViewModel.attach(binding.surfaceView)
        playerViewModel.playing.observe(viewLifecycleOwner) {
            binding.playerControllerView.isPlaying = it
        }

        playerViewModel.currPlayingVideo.observe(viewLifecycleOwner) {
            binding.playerControllerView.duration = it!!.duration.toLong()/1000
        }

        playerViewModel.currTime.observe(viewLifecycleOwner) {
            binding.playerControllerView.currTime = it
        }

        binding.playerControllerView.actionHandler = object : PlayerControllerView.ActionHandler {
            override fun onPlay() {
                playerViewModel.togglePlayPause()
            }

            override fun onPause() {
                playerViewModel.togglePlayPause()
            }

            override fun onSkipNext() {
                playerViewModel.skipToNext()
            }

            override fun onSkipPrevious() {
                playerViewModel.skipToPrevious()
            }

            override fun onForward() {
                playerViewModel.seekForward()
            }

            override fun onReplay() {
                playerViewModel.seekReplay()
            }

            override fun onSeeking(progress: Int) {
                Toast.makeText(requireContext(), "click seeking: ", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerViewModel.detach(binding.surfaceView)
    }

    companion object {
        const val TAG = "PlayerFragment"
    }
}