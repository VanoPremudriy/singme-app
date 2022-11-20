package com.example.singmeapp.fragments

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentPlayerBinding
import android.widget.SeekBar
import com.google.firebase.storage.FirebaseStorage


class PlayerFragment : Fragment() {

    lateinit var storage: FirebaseStorage
    lateinit var binding: FragmentPlayerBinding
    lateinit var mPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storage = FirebaseStorage.getInstance()
        mPlayer =  MediaPlayer.create(context, R.raw.addicted)

        binding = FragmentPlayerBinding.inflate(layoutInflater)

        initializeSeekBar()

        binding.imageButton.setOnClickListener{
            if (!mPlayer.isPlaying){
                binding.imageButton.setImageResource(android.R.drawable.ic_media_pause)
                mPlayer.start()
            }
            else{
                binding.imageButton.setImageResource(android.R.drawable.ic_media_play)
                mPlayer.pause()
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                if(b) {
                    mPlayer.seekTo(i * 1000)
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initializeSeekBar() {
        binding.seekBar.max = mPlayer.seconds

        runnable = Runnable {
            binding.seekBar.progress = mPlayer.currentSeconds
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }

    companion object {

        @JvmStatic
        fun newInstance() = PlayerFragment()

    }
}