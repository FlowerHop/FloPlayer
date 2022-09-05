package com.example.floplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.floplayer.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: VideoListAdapter
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    VideoRepository(
                        requireActivity().contentResolver
                    )
                ) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VideoListAdapter {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, PlayerFragment::class.java, null, PlayerFragment.TAG)
                commit()
                addToBackStack(PlayerFragment.TAG)
            }
        }

        homeViewModel.videoList.observe(viewLifecycleOwner) { videoList ->
            adapter.submitList(videoList.toMutableList())
        }

        binding.recyclerView.adapter = adapter
    }

    companion object {
        const val TAG: String = "HomeFragment"
    }
}