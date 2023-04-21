package com.example.singmeapp.adapters

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.CreateNotification
import com.example.singmeapp.MainActivity
import com.example.singmeapp.OnClearFromRecentService
import com.example.singmeapp.R
import com.example.singmeapp.databinding.TrackItemBinding
import com.example.singmeapp.fragments.*
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.example.singmeapp.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso


class TrackAdapter(val fragmentActivity: AppCompatActivity, val fragment: Fragment): RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    public var trackList = ArrayList<Track>()
    var trackListDefaultCopy = ArrayList<Track>()
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var curTrack: Track
    val mainActivity = fragmentActivity as MainActivity
    var prevItem:TrackHolder? = null

    var prevId: Int? = null


    val playlistViewModelProvider = ViewModelProvider(fragment)
    val playlistViewModel = playlistViewModelProvider[PlaylistViewModel::class.java]


    class TrackHolder(item: View, private val fragmentActivity: AppCompatActivity): RecyclerView.ViewHolder(item){
        val binding = TrackItemBinding.bind(item)


        @SuppressLint("SuspiciousIndentation")
        fun bind(track: Track) = with(binding){
            tvItemTrackName.text = track.name
            tvItemTrackBandName.text = track.band
            if (track.imageUrl!="")
            Picasso.get().load(track.imageUrl).fit().into(ivItemTrackCover)

        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        when(fragment::class.java){
            DiscographyFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_2, parent, false)
            }
            MyLibraryFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
                if ((fragment as MyLibraryFragment).binding.libraryGSLayout.visibility == View.VISIBLE){
                    view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_2, parent, false)
                }

            }
            AlbumFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            }
            PlayerPlaylistFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            }
            CatalogueNewsFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_2, parent, false)
            }
            CataloguePopularFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_2, parent, false)
            }
            CatalogueFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_2, parent, false)
            }
        }
        return  TrackHolder(view, fragmentActivity)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(trackList[position])

        fragmentActivity.registerReceiver(broadcastReceiver, IntentFilter("TRACKSTRACKS"))
        fragmentActivity.startService(Intent(fragmentActivity, OnClearFromRecentService::class.java))

        val provider = ViewModelProvider(fragmentActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]


        playerPlaylistViewModel.currentTrackId.observe(fragment.viewLifecycleOwner){
            if (fragment.javaClass == PlayerPlaylistFragment::class.java){
                if (prevId == null){
                    prevId = playerPlaylistViewModel.prevId.value
                    prevItem = playerPlaylistViewModel.prevItem.value
                    prevItem?.binding?.ivPlayPauseTrackItem?.visibility = View.VISIBLE
                    prevItem?.binding?.ivPlayPauseTrackItem?.setImageResource(R.drawable.ic_pause)
                }
            }
            if (it != null) {

                if (it != prevId && prevItem != null && prevId != null) {

                    prevItem?.binding?.ivPlayPauseTrackItem?.visibility = View.GONE
                    holder.binding.ivPlayPauseTrackItem.visibility = View.VISIBLE
                    holder.binding.ivPlayPauseTrackItem.setImageResource(R.drawable.ic_pause)
                    prevId = position
                    prevItem = holder
                } else if (it == prevId && position == it) {
                    holder.binding.ivPlayPauseTrackItem.visibility = View.VISIBLE
                    if (playerPlaylistViewModel.isPlaying.value == true) {
                        holder.binding.ivPlayPauseTrackItem.setImageResource(R.drawable.ic_pause)
                    }
                    else holder.binding.ivPlayPauseTrackItem.setImageResource(R.drawable.ic_play)
                    prevId = position
                    prevItem = holder
                }

            } else {
                if (position < trackList.size)
                    holder.binding.ivPlayPauseTrackItem.visibility = View.GONE
            }
            playerPlaylistViewModel.prevItem.value = prevItem
            playerPlaylistViewModel.prevId.value = prevId
        }

        holder.binding.SongLayout.setOnClickListener {
            //playerPlaylistViewModel.updateListeningCounter(trackList[position].uuid)
            setTrack(position)
            holder.binding.ivPlayPauseTrackItem.visibility = View.VISIBLE
            if (playerPlaylistViewModel.isPlaying.value == true)
                holder.binding.ivPlayPauseTrackItem.setImageResource(R.drawable.ic_pause)
            else holder.binding.ivPlayPauseTrackItem.setImageResource(R.drawable.ic_play)
            prevId = position
            prevItem = holder
            playerPlaylistViewModel.prevItem.value = prevItem
            playerPlaylistViewModel.prevId.value = prevId
        }



        mainActivity.binding.player.ibClose.setOnClickListener {
            if (prevId != null && prevId!! < trackList.size && trackList[prevId!!].imageUrl != "")
                prevItem?.binding?.ivPlayPauseTrackItem?.visibility = View.GONE
            closePlayer()
        }


        playerPlaylistViewModel.isPlaying.observe(fragment.viewLifecycleOwner){
            if (it){
                prevItem?.binding?.ivPlayPauseTrackItem?.setImageResource(R.drawable.ic_pause)
                mainActivity.binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_pause)
            }
            else {
                prevItem?.binding?.ivPlayPauseTrackItem?.setImageResource(R.drawable.ic_play)
                mainActivity.binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_play)
            }

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
            mainActivity.binding.tvAddToPlaylist.setOnClickListener{
                addToPlaylist(position)
            }
            mainActivity.binding.tvDeleteFromPlaylist.setOnClickListener{
                deleteFromPlaylist(position)
            }
        }

    }

    fun setTrack(position: Int){
            if (trackList[position].trackUrl != "") {
                CreateNotification().createNotification(
                    fragmentActivity,
                    trackList[position],
                    R.drawable.ic_pause
                )
                if (playerPlaylistViewModel.trackList.value == null || playerPlaylistViewModel.trackList.value?.equals(
                        trackList
                    ) == false
                ) {
                    if (playerPlaylistViewModel.trackList.value != null){
                    (playerPlaylistViewModel.trackList.value as ArrayList<Track>).clear()
                    (playerPlaylistViewModel.trackList.value as ArrayList<Track>).addAll(trackList)
                        playerPlaylistViewModel.trackList.value = playerPlaylistViewModel.trackList.value
                    } else {
                        playerPlaylistViewModel.trackList.value = ArrayList<Track>()
                        (playerPlaylistViewModel.trackList.value as ArrayList<Track>).addAll(trackList)
                        playerPlaylistViewModel.trackList.value = playerPlaylistViewModel.trackList.value
                    }
                    prevId = null
                    prevItem = null

                }

                playerPlaylistViewModel.currentTrackId.value = position
                if (mainActivity.bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
                    mainActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mainActivity.binding.player.tvSongNameUpMenu.text = trackList[position].name
                mainActivity.binding.player.tvBandNameUpMenu.text = trackList[position].band
            } else Toast.makeText(
                fragmentActivity.applicationContext,
                "Загрузка",
                Toast.LENGTH_SHORT
            ).show()

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
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
        val track = trackList[position]
        playerPlaylistViewModel.deleteTrackFromLove(track)
        //if (fragment.javaClass == MyLibraryFragment::class.java)
        //setTrack(position)
         if (fragment.javaClass == MyLibraryFragment::class.java) {
            val frag = fragment as MyLibraryFragment
             frag.trackAdapter.trackList.remove(track)
             frag.binding.rcView.adapter = frag.trackAdapter
        }
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
        if (fragment.javaClass == PlaylistFragment::class.java){
            mainActivity.binding.tvDeleteFromPlaylist.visibility = View.VISIBLE
            mainActivity.binding.tvAddToPlaylist.visibility = View.GONE
        }
        else {
            mainActivity.binding.tvDeleteFromPlaylist.visibility = View.GONE
            mainActivity.binding.tvAddToPlaylist.visibility = View.VISIBLE
        }
        mainActivity.binding.trackMenu.visibility = View.VISIBLE
        mainActivity.binding.view15.visibility = View.VISIBLE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun addToPlaylist(position: Int){
        val bundle = Bundle()
        bundle.putString("trackUuid", trackList[position].uuid)
        fragment.findNavController().navigate(R.id.choosePlaylistForTrackFragment, bundle)
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun deleteFromPlaylist(position: Int){
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
        playlistViewModel.deleteTrack(trackList[position].uuid)
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

    fun initList(tracks: List<Track>){
        trackList.clear()
        trackList.addAll(tracks.reversed())
        trackListDefaultCopy.clear()
        trackListDefaultCopy.addAll(tracks.reversed())
    }

    fun sortByDefault(){
        if (playerPlaylistViewModel.trackList.value?.equals(trackList) == true) {
            closePlayer()
        }
        trackList.clear()
        trackList.addAll(trackListDefaultCopy)
    }
    fun sortByName(){
        if (playerPlaylistViewModel.trackList.value?.equals(trackList) == true) {
            closePlayer()
        }
        trackList.sortBy { track: Track ->  track.name}
    }

    fun sortByAlbum(){
        if (playerPlaylistViewModel.trackList.value?.equals(trackList) == true) {
            closePlayer()
        }
        trackList.sortBy { track: Track ->  track.album}

    }
    fun sortByBand(){
        if (playerPlaylistViewModel.trackList.value?.equals(trackList) == true) {
            closePlayer()
        }
        trackList.sortBy { track: Track ->  track.band}

    }

    fun sortByDate(){
        if (playerPlaylistViewModel.trackList.value?.equals(trackList) == true) {
            closePlayer()
        }
        trackList.sortBy { track: Track ->  track.date}

    }


    override fun getItemCount(): Int {
       return trackList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}