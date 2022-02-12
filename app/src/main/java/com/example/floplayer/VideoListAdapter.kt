package com.example.floplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.floplayer.databinding.ItemVideoBinding

class VideoListAdapter(private var onClick: (VideoMetaData) -> Unit) : ListAdapter<VideoMetaData, VideoListAdapter.VideoViewHolder>(
    object : DiffUtil.ItemCallback<VideoMetaData>() {
        override fun areItemsTheSame(oldItem: VideoMetaData, newItem: VideoMetaData): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: VideoMetaData, newItem: VideoMetaData): Boolean {
            if (oldItem.displayName != newItem.displayName) return false
            if (oldItem.duration != newItem.duration) return false
            if (oldItem.contentUri != newItem.contentUri) return false
            return true
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.text.text = getItem(position).displayName
        Glide.with(holder.image).load(getItem(position).contentUri).into(holder.image)
        holder.itemView.setOnClickListener { onClick(getItem(position)) }
    }

    class VideoViewHolder(binding: ItemVideoBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val text = binding.text
    }
}