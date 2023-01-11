package com.example.singmeapp.fragments

import android.graphics.BitmapFactory
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
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentPlayerPlayerBinding
import com.example.singmeapp.items.Song
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

class PlayerPlayerFragment : Fragment() {

    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var fragActivity: AppCompatActivity
    lateinit var storage: FirebaseStorage
    var mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    lateinit var currentSong: Song
    var previousSong: Song? = null



    lateinit var binding: FragmentPlayerPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerPlayerBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()
        fragActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(fragActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]
        playerPlaylistViewModel.songList.observe(viewLifecycleOwner){ it1 ->
            playerPlaylistViewModel.currentSongId.observe(viewLifecycleOwner) { it2 ->
                if (it1 != null && it2 != null) {
                    currentSong = it1[it2]
                    binding.tvPlayerTrackName.text = currentSong.name
                    binding.tvPlayerBandName.text = currentSong.band
                    Log.e("Player", previousSong.toString())
                    if (previousSong == null) {
                        //mPlayer = MediaPlayer.create(context, R.raw.addicted)
                        mPlayer = MediaPlayer()
                        mPlayer.setDataSource(currentSong.songUrl)
                        mPlayer.prepare()
                        mPlayer.start()
                        playerPlaylistViewModel.isPlaying.value = true
                        binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                    } else {
                        if (previousSong == currentSong) {
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
                           // mPlayer = MediaPlayer.create(context, R.raw.addicted)
                            mPlayer = MediaPlayer()
                            mPlayer.setDataSource(currentSong.songUrl)
                            mPlayer.prepare()
                            mPlayer.start()
                            binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                            playerPlaylistViewModel.isPlaying.value = true
                        }
                    }
                    initializeCover(currentSong.imageUrl)
                    initializeSeekBar()
                    initializeButtonsClickListeners()
                    previousSong = currentSong
                }
                else {
                    mPlayer.stop()
                    previousSong = null
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
            } else {
                binding.ibPlay.setImageResource(android.R.drawable.ic_media_play)
                mPlayer.pause()
            }

        }

        binding.ibMusicRight.setOnClickListener {
            if (playerPlaylistViewModel.currentSongId.value?.plus(1) == playerPlaylistViewModel.songList.value?.size)
                playerPlaylistViewModel.currentSongId.value = 0
            else
            playerPlaylistViewModel.currentSongId.value = playerPlaylistViewModel.currentSongId.value?.plus(
                1
            )
        }

        binding.ibMusicLeft.setOnClickListener {
            if (playerPlaylistViewModel.currentSongId.value!! > 0)
            playerPlaylistViewModel.currentSongId.value = playerPlaylistViewModel.currentSongId.value?.minus(
                1
            )
            else{
                playerPlaylistViewModel.currentSongId.value = playerPlaylistViewModel.songList.value?.size?.minus(
                    1
                )
            }
        }
    }

    fun initializeCover(coverName: String){
        try {
            val localFile: File = File.createTempFile("default_picture", "jpg")
            storage.getReferenceFromUrl(coverName).getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    binding.imageView6.setImageBitmap(bitmap)
                }.addOnFailureListener { }
        } catch (e: IOException) {
        }
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