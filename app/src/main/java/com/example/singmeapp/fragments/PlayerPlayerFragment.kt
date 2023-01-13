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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.singmeapp.databinding.FragmentPlayerPlayerBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Delay

class PlayerPlayerFragment : Fragment() {

    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var fragActivity: AppCompatActivity
    var mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    lateinit var currentTrack: Track
    var previousTrack: Track? = null
    var currentList = ArrayList<Track>()


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
        //val pairMediatorLiveData = PairMediatorLiveData<List<Track>, Int>(playerPlaylistViewModel.trackList, playerPlaylistViewModel.currentTrackId)
        //pairMediatorLiveData.observe(viewLifecycleOwner){
        playerPlaylistViewModel.currentTrackId.observe(viewLifecycleOwner){it ->
            try{
                if (playerPlaylistViewModel.trackList.value != null && it != null) {
                    currentTrack = playerPlaylistViewModel.trackList.value!![it!!]
                    if (previousTrack == null) {
                        mPlayer = MediaPlayer()
                        mPlayer.setDataSource(currentTrack.trackUrl)
                        mPlayer.prepare()
                        mPlayer.start()
                        playerPlaylistViewModel.isPlaying.value = true
                        binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                        previousTrack = currentTrack
                        currentList = playerPlaylistViewModel.trackList.value as ArrayList<Track>
                        Log.e("is", "One")
                    } else {
                        if (previousTrack == currentTrack && !currentList.equals(playerPlaylistViewModel.trackList.value)) {
                            mPlayer.stop()
                            mPlayer = MediaPlayer()
                            mPlayer.setDataSource(currentTrack.trackUrl)
                            mPlayer.prepare()
                            mPlayer.start()
                            playerPlaylistViewModel.isPlaying.value = true
                            previousTrack = currentTrack
                            Log.e("is", "Two")/* = java.util.ArrayList<com.example.singmeapp.items.Track> */
                        } else if (previousTrack == currentTrack && currentList.equals(playerPlaylistViewModel.trackList.value)) {
                                if (!mPlayer.isPlaying) {
                                    binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                                    mPlayer.start()
                                    playerPlaylistViewModel.isPlaying.value = true
                                } else {
                                    binding.ibPlay.setImageResource(android.R.drawable.ic_media_play)
                                    mPlayer.pause()
                                    playerPlaylistViewModel.isPlaying.value = false
                                }
                            Log.e("is", "Tree")
                            Log.e("Size", playerPlaylistViewModel.trackList.value!!.size.toString())
                            } else if (previousTrack != currentTrack) {
                                mPlayer.stop()
                                mPlayer = MediaPlayer()
                                mPlayer.setDataSource(currentTrack.trackUrl)
                                mPlayer.prepare()
                                mPlayer.start()
                                binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                                playerPlaylistViewModel.isPlaying.value = true
                            Log.e("is", "four")
                            }
                        currentList = playerPlaylistViewModel.trackList.value as ArrayList<Track>

                    }
                    initializeButtonsClickListeners()
                    initializeCover(currentTrack.imageUrl)
                    initializeSeekBar()
                    previousTrack = currentTrack
                    Log.e("ee", "ee")
                    binding.tvPlayerBandName.text = currentTrack.band
                    binding.tvPlayerTrackName.text = currentTrack.name
                }

                else {
                    mPlayer.stop()
                    previousTrack = null
                    playerPlaylistViewModel.isPlaying.value = false
                }
            } catch (e: Throwable){

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
        Picasso.get().load(coverUrl).fit().into(binding.ivCover)
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