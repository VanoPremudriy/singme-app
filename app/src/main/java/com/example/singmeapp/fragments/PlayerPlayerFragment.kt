package com.example.singmeapp.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.CreateNotification
import com.example.singmeapp.MainActivity
import com.example.singmeapp.OnClearFromRecentService
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentPlayerPlayerBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class PlayerPlayerFragment : Fragment(), View.OnClickListener, MediaPlayer.OnCompletionListener {

    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var fragActivity: AppCompatActivity
    lateinit var mainActivity: MainActivity
    var mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    lateinit var currentTrack: Track
    var previousTrack: Track? = null
    var currentList = ArrayList<Track>()



    lateinit var binding: FragmentPlayerPlayerBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.registerReceiver(broadcastReceiver, IntentFilter("TRACKSTRACKS"))
        context?.startService(Intent(context, OnClearFromRecentService::class.java))

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerPlayerBinding.inflate(layoutInflater)
        fragActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        val provider = ViewModelProvider(fragActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]

        playerPlaylistViewModel.currentTrackId.observeForever{it ->
            try{
                if (playerPlaylistViewModel.trackList.value != null && it != null) {
                    currentTrack = playerPlaylistViewModel.trackList.value!![it]
                    if (previousTrack == null) {
                        mPlayer = MediaPlayer()
                        mPlayer.setOnCompletionListener(this@PlayerPlayerFragment)
                        mPlayer.setDataSource(currentTrack.trackUrl)
                        mPlayer.prepare()
                        mPlayer.start()
                        playerPlaylistViewModel.isPlaying.value = true
                        binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                        previousTrack = currentTrack
                        currentList = playerPlaylistViewModel.trackList.value as ArrayList<Track>
                        Log.e("is", "One")
                        playerPlaylistViewModel.updateListeningCounter(currentTrack.uuid, currentTrack.albumUuid)
                    } else {
                        if (previousTrack == currentTrack && !currentList.equals(playerPlaylistViewModel.trackList.value)) {
                            mPlayer.stop()
                            mPlayer = MediaPlayer()
                            mPlayer.setOnCompletionListener(this@PlayerPlayerFragment)
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
                            Log.e("current track", currentTrack.name)
                            if (previousTrack != null)
                            Log.e("previous track", previousTrack!!.name)
                                mPlayer.stop()
                                mPlayer = MediaPlayer()
                                mPlayer.setOnCompletionListener(this@PlayerPlayerFragment)
                                mPlayer.setDataSource(currentTrack.trackUrl)
                                mPlayer.prepare()
                                mPlayer.start()
                                binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                                playerPlaylistViewModel.isPlaying.value = true
                                playerPlaylistViewModel.updateListeningCounter(currentTrack.uuid, currentTrack.albumUuid)
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
                    mainActivity.binding.player.tvBandNameUpMenu.text = currentTrack.band
                    mainActivity.binding.player.tvSongNameUpMenu.text = currentTrack.name

                    val playPause: Int = if (!mPlayer.isPlaying) {
                        R.drawable.ic_play
                    } else {
                        R.drawable.ic_pause
                    }
                    CreateNotification().createNotification(fragActivity, currentTrack, playPause)
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


    override fun onDestroy() {
        super.onDestroy()
        mPlayer.stop()
    }

    private fun initializeButtonsClickListeners(){
        val mainActivity = activity as MainActivity

        binding.ibPlay.setOnClickListener(this@PlayerPlayerFragment)
        binding.ibMusicRight.setOnClickListener(this@PlayerPlayerFragment)
        binding.ibMusicLeft.setOnClickListener(this@PlayerPlayerFragment)
        binding.ibTrackMenu.setOnClickListener(this@PlayerPlayerFragment)
        binding.tvPlayerBandName.setOnClickListener(this@PlayerPlayerFragment)

        mainActivity.binding.tvAddTrackToLoveInPLayer.setOnClickListener(this@PlayerPlayerFragment)
        mainActivity.binding.tvDeleteTrackFromLoveInPlayer.setOnClickListener(this@PlayerPlayerFragment)
        mainActivity.binding.tvGoToBandProfileInPlayer.setOnClickListener(this@PlayerPlayerFragment)
        mainActivity.binding.tvGoToAlbumInPlayer.setOnClickListener(this@PlayerPlayerFragment)
    }

    private fun initializeCover(coverUrl: String){
        Picasso.get().load(coverUrl).fit().into(binding.ivCover)
    }

    private fun initializeSeekBar() {
        binding.seekBar.max = mPlayer.seconds

        runnable = Runnable {
            binding.seekBar.progress = mPlayer.currentSeconds

            val curMin = TimeUnit.SECONDS.toMinutes(mPlayer.currentSeconds.toLong())
            val curSec = mPlayer.currentSeconds % 60

            val minusMin = TimeUnit.SECONDS.toMinutes(mPlayer.seconds.toLong() - mPlayer.currentSeconds.toLong())
            val minusSec = (mPlayer.seconds.toLong() - mPlayer.currentSeconds.toLong()) % 60

            binding.tvTrackCurrentTime.text =  String.format("%02d:%02d", curMin, curSec)
            binding.tvTrackMinusCurrentTime.text = String.format("-%02d:%02d", minusMin, minusSec)

            handler.postDelayed(runnable, 10)
        }
        handler.postDelayed(runnable, 10)
    }

    // Creating an extension property to get the media player time duration in seconds
    private val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    private val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }

    companion object {
        @JvmStatic
        fun newInstance() = PlayerPlayerFragment()

    }

    override fun onClick(p0: View?) {
        val mainActivity = activity as MainActivity
        when(p0?.id){
            binding.ibPlay.id -> {
                play()
            }

            binding.ibMusicLeft.id -> {
                nextLeft()
            }

            binding.ibMusicRight.id -> {
                nextRight()
            }

            binding.ibTrackMenu.id -> {
                if (currentTrack.isInLove){
                    mainActivity.binding.tvAddTrackToLoveInPLayer.visibility = View.GONE
                    mainActivity.binding.tvDeleteTrackFromLoveInPlayer.visibility = View.VISIBLE
                }
                else {
                    mainActivity.binding.tvAddTrackToLoveInPLayer.visibility = View.VISIBLE
                    mainActivity.binding.tvDeleteTrackFromLoveInPlayer.visibility = View.GONE
                }

                mainActivity.binding.playerTrackMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }

            mainActivity.binding.tvAddTrackToLoveInPLayer.id -> {
                playerPlaylistViewModel.addTrackToLove(currentTrack)
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }

            mainActivity.binding.tvDeleteTrackFromLoveInPlayer.id -> {
                playerPlaylistViewModel.deleteTrackFromLove(currentTrack)
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }

            mainActivity.binding.tvGoToBandProfileInPlayer.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                mainActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val bundle = Bundle()
                bundle.putString("bandUuid", currentTrack.bandUuid)
                findNavController().navigate(R.id.bandFragment, bundle)
            }

            mainActivity.binding.tvGoToAlbumInPlayer.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                mainActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val bundle = Bundle()
                bundle.putString("albumUuid", currentTrack.albumUuid)
                findNavController().navigate(R.id.albumFragment, bundle)
            }

            binding.tvPlayerBandName.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                mainActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val bundle = Bundle()
                bundle.putString("bandUuid", currentTrack.bandUuid)
                findNavController().navigate(R.id.bandFragment, bundle)
            }
        }

    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (playerPlaylistViewModel.currentTrackId.value?.plus(1) == playerPlaylistViewModel.trackList.value?.size)
            playerPlaylistViewModel.currentTrackId.value = 0
        else
            playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value?.plus(
                1
            )
    }

    fun play(){
        val playPause: Int
        if (!mPlayer.isPlaying) {
            binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
            mPlayer.start()
            playerPlaylistViewModel.isPlaying.value = true
            playPause = R.drawable.ic_pause
        } else {
            binding.ibPlay.setImageResource(android.R.drawable.ic_media_play)
            mPlayer.pause()
            playerPlaylistViewModel.isPlaying.value = false
            playPause = R.drawable.ic_play
        }
        CreateNotification().createNotification(fragActivity, currentTrack, playPause)
    }

    fun nextLeft(){
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

    fun nextRight(){
        if (playerPlaylistViewModel.currentTrackId.value?.plus(1) == playerPlaylistViewModel.trackList.value?.size)
            playerPlaylistViewModel.currentTrackId.value = 0
        else
            playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value?.plus(
                1
            )
    }

    private val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.getStringExtra("actionname")){
                CreateNotification().ACTION_PLAY -> {
                    play()
                    Log.e("ACTION", "PLAY")
                }
                CreateNotification().ACTION_NEXT -> {
                    nextRight()
                    Log.e("ACTION", "NEXT")
                }
                CreateNotification().ACTION_PREVIOUS -> {
                    nextLeft()
                    Log.e("ACTION", "PREVIOUS")
                }

            }
        }

    }

}