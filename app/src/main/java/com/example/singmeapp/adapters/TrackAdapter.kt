package com.example.singmeapp.adapters

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.CreateNotification
import com.example.singmeapp.MainActivity
import com.example.singmeapp.OnClearFromRecentService
import com.example.singmeapp.R
import com.example.singmeapp.databinding.TrackItemBinding
import com.example.singmeapp.fragments.AlbumFragment
import com.example.singmeapp.fragments.DiscographyFragment
import com.example.singmeapp.fragments.MyLibraryFragment
import com.example.singmeapp.fragments.PlayerPlaylistFragment
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso


class TrackAdapter(val fragmentActivity: AppCompatActivity, val fragment: Fragment): RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    public var trackList = ArrayList<Track>()
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var curTrack: Track
    val mainActivity = fragmentActivity as MainActivity


    class TrackHolder(item: View, private val fragmentActivity: AppCompatActivity): RecyclerView.ViewHolder(item){


        lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
        val binding = TrackItemBinding.bind(item)


        fun bind(track: Track) = with(binding){
            tvItemTrackName.text = track.name
            tvItemTrackBandName.text = track.band
            if (track.imageUrl!="")
            Picasso.get().load(track.imageUrl).fit().into(ivItemTrackCover)
            val provider = ViewModelProvider(fragmentActivity)
            playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]




        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        when(fragment::class.java){
            DiscographyFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_2, parent, false)
            }
            MyLibraryFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            }
            AlbumFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            }
            PlayerPlaylistFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            }
        }
        return  TrackHolder(view, fragmentActivity)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        fragmentActivity.registerReceiver(broadcastReceiver, IntentFilter("TRACKSTRACKS"))
        fragmentActivity.startService(Intent(fragmentActivity, OnClearFromRecentService::class.java))

        holder.bind(trackList[position])

        val provider = ViewModelProvider(fragmentActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]

        holder.binding.SongLayout.setOnClickListener {
            setTrack(position)
        }

        mainActivity.binding.player.ibClose.setOnClickListener {
            closePlayer()
        }

        playerPlaylistViewModel.isPlaying.observe(fragmentActivity){
            if (it){
                mainActivity.binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_pause)
            }
            else mainActivity.binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_play)
        }

        mainActivity.binding.player.ibPlayUpMenu.setOnClickListener{
            playFromUpMenu()
        }

        holder.binding.ibItemTrackMenu.setOnClickListener{
            openTrackMenu(position)

            mainActivity.binding.tvAddTrackToLove.setOnClickListener {
                addTrackToLove(position)
            }

            mainActivity.binding.tvDeleteTrackFromLove.setOnClickListener {
                deleteTrackFromLove(position)
            }

            mainActivity.binding.tvGoToBandProfile.setOnClickListener {
              goToBandProfile(position)
            }

            mainActivity.binding.tvGoToAlbum.setOnClickListener {
                goToAlbum(position)
            }
        }

    }

    fun setTrack(position: Int){
        if (trackList[position].trackUrl != "") {
            if (playerPlaylistViewModel.trackList.value == null || playerPlaylistViewModel.trackList.value?.equals(
                    trackList
                ) == false
            ) {
                playerPlaylistViewModel.trackList.value = trackList

            }

            playerPlaylistViewModel.currentTrackId.value = position
            if (mainActivity.bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
                mainActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            mainActivity.binding.player.tvSongNameUpMenu.text = trackList[position].name
            mainActivity.binding.player.tvBandNameUpMenu.text = trackList[position].band
        }
        else Toast.makeText(fragmentActivity.applicationContext, "Загрузка", Toast.LENGTH_SHORT).show()
    }

    fun playFromUpMenu(){
        playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value
    }

    fun closePlayer(){
        playerPlaylistViewModel.currentTrackId.value = null
        mainActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mainActivity.notificationManagerCompat.cancelAll()
    }

    fun addTrackToLove(position: Int){
        playerPlaylistViewModel.addTrackToLove(trackList[position])
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun deleteTrackFromLove(position: Int){
        playerPlaylistViewModel.deleteTrackFromLove(trackList[position])
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun goToBandProfile(position: Int){
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
        val bundle = Bundle()
        bundle.putString("bandUuid", trackList[position].bandUuid)
        NavHostFragment.findNavController(fragment).navigate(R.id.bandFragment, bundle)
    }

    fun goToAlbum(position: Int){
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
        val bundle = Bundle()
        bundle.putString("albumUuid", trackList[position].albumUuid)
        NavHostFragment.findNavController(fragment).navigate(R.id.albumFragment, bundle)
    }

    fun openTrackMenu(position: Int){
        if (trackList[position].isInLove){
            mainActivity.binding.tvAddTrackToLove.visibility = View.GONE
            mainActivity.binding.tvDeleteTrackFromLove.visibility = View.VISIBLE
        }
        else {
            mainActivity.binding.tvAddTrackToLove.visibility = View.VISIBLE
            mainActivity.binding.tvDeleteTrackFromLove.visibility = View.GONE
        }
        mainActivity.binding.trackMenu.visibility = View.VISIBLE
        mainActivity.binding.view15.visibility = View.VISIBLE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
    }


    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.getStringExtra("actionname")){
                CreateNotification().ACTION_CLOSE -> {
                    closePlayer()
                }

            }
        }

    }



    override fun getItemCount(): Int {
       return trackList.size
    }


}