package com.example.floplayer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.floplayer.databinding.LayoutControllerViewBinding

class PlayerControllerView: ConstraintLayout {
    interface ActionHandler {
        fun onPlay()
        fun onPause()
        fun onSkipNext()
        fun onSkipPrevious()
        fun onForward()
        fun onReplay()
        fun onSeeking(progress: Int)
    }

    private lateinit var binding: LayoutControllerViewBinding
    var actionHandler: ActionHandler? = null

    var isPlaying = false
    set(value) {
        field = value
        binding.iconPlayPause.setImageResource(
            if (value)
                R.drawable.icon_pause
            else
                R.drawable.icon_play
        )
    }

    var duration: Long = 0L // ms
    set(value) {
        field = value
        binding.totalTimeText.text = duration.toString()
        binding.seekBar.max = (duration/1000).toInt()
    }

    var currTime: Long = 0L // ms
    set(value) {
        field = value
        binding.currTimeText.text = currTime.toString()
        binding.seekBar.progress = (value/1000).toInt()
    }

    constructor(context: Context): super(context) {
        initView(context)
    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initView(context)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int): super(context, attributeSet, defStyle) {
        initView(context)
    }

    private fun initView(context: Context) {
        binding = LayoutControllerViewBinding.inflate(LayoutInflater.from(context), this).apply {
            iconPlayPause.setOnClickListener {
                if (isPlaying) actionHandler?.onPause()
                else actionHandler?.onPlay()
            }

            iconSkipNext.setOnClickListener { actionHandler?.onSkipNext() }
            iconSkipPrevious.setOnClickListener { actionHandler?.onSkipPrevious() }
            iconForward10.setOnClickListener { actionHandler?.onForward() }
            iconReplay10.setOnClickListener { actionHandler?.onReplay() }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return
                    actionHandler?.onSeeking(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
        }
    }
}