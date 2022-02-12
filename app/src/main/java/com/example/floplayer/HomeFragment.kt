package com.example.floplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.floplayer.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val videoList: MutableList<VideoMetaData> = mutableListOf()
    private lateinit var adapter: VideoListAdapter
    private lateinit var homeViewModel: HomeViewModel

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
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(VideoRepository(requireActivity().contentResolver)) as T
            }
        }

        homeViewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
        adapter = VideoListAdapter { videoMetaData ->
            Toast.makeText(requireContext(), "click: ${videoMetaData.displayName}", Toast.LENGTH_SHORT).show()
        }

        homeViewModel.videoList.observe(viewLifecycleOwner) { videoList ->
            adapter.submitList(videoList.toMutableList())
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object  : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                homeViewModel.scrollToLoadMoreIfNeed((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
            }
        })
    }

    companion object {
        const val TAG: String = "HomeFragment"
    }
}