package com.example.singmeapp.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.singmeapp.databinding.FragmentPlayerPlayerBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.squareup.picasso.Picasso

class PlayerPlayerFragment : Fragment() {

    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var fragActivity: AppCompatActivity
    var mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    lateinit var currentTrack: Track
    var previousTrack: Track? = null



    lateinit var binding: FragmentPlayerPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerPlayerBinding.inflate(layoutInflater)
        fragActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(fragActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]
        playerPlaylistViewModel.trackList.observe(viewLifecycleOwner){ it1 ->
            playerPlaylistViewModel.currentTrackId.observe(viewLifecycleOwner) { it2 ->
                if (it1 != null && it2 != null) {
                    currentTrack = it1[it2]
                    binding.tvPlayerTrackName.text = currentTrack.name
                    binding.tvPlayerBandName.text = currentTrack.band
                    Log.e("Player", previousTrack.toString())
                    if (previousTrack == null) {
                        mPlayer = MediaPlayer()
                        mPlayer.setDataSource(currentTrack.trackUrl)
                        mPlayer.prepare()
                        mPlayer.start()
                        playerPlaylistViewModel.isPlaying.value = true
                        binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                    } else {
                        if (previousTrack == currentTrack) {
                            if (!mPlayer.isPlaying) {
                                binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                                mPlayer.start()
                                playerPlaylistViewModel.isPlaying.value = true
                            } else {
                                binding.ibPlay.setImageResource(android.R.drawable.ic_media_play)
                                mPlayer.pause()
                                playerPlaylistViewModel.isPlaying.value = false
                            }
                        } else {
                            mPlayer.stop()
                            mPlayer = MediaPlayer()
                            mPlayer.setDataSource(currentTrack.trackUrl)
                            mPlayer.prepare()
                            mPlayer.start()
                            binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                            playerPlaylistViewModel.isPlaying.value = true
                        }
                    }
                    initializeCover(currentTrack.imageUrl)
                    initializeSeekBar()
                    initializeButtonsClickListeners()
                    previousTrack = currentTrack
                }
                else {
                    mPlayer.stop()
                    previousTrack = null
                    playerPlaylistViewModel.isPlaying.value = false
                }
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

    fun initializeButtonsClickListeners(){
        binding.ibPlay.setOnClickListener {

            if (!mPlayer.isPlaying) {
                binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                mPlayer.start()
                playerPlaylistViewModel.isPlaying.value = true
            } else {
                binding.ibPlay.setImageResource(android.R.drawable.ic_media_play)
                mPlayer.pause()
                playerPlaylistViewModel.isPlaying.value = false
            }

        }

        binding.ibMusicRight.setOnClickListener {
            if (playerPlaylistViewModel.currentTrackId.value?.plus(1) == playerPlaylistViewModel.trackList.value?.size)
                playerPlaylistViewModel.currentTrackId.value = 0
            else
            playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value?.plus(
                1
            )
        }

        binding.ibMusicLeft.setOnClickListener {
            if (playerPlaylistViewModel.currentTrackId.value!! > 0)
            playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value?.minus(
                1
            )
            else{
                playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.trackList.value?.size?.minus(
                    1
                )
            }
        }
    }

    fun initializeCover(coverUrl: String){
        Picasso.get().load(coverUrl).fit().into(binding.imageView6)
    }

    fun initializeSeekBar() {
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
        fun newInstance() = PlayerPlayerFragment()

    }

}