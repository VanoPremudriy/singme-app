package com.example.singmeapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.TrackItemBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso


class TrackAdapter(val fragmentActivity: AppCompatActivity): RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    public var trackList = ArrayList<Track>()
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel

    class TrackHolder(item: View, private val fragmentActivity: AppCompatActivity): RecyclerView.ViewHolder(item){


        lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
        val binding = TrackItemBinding.bind(item)

        fun bind(track: Track) = with(binding){
            tvItemTrackName.text = track.name
            tvItemTrackBandName.text = track.band
            Picasso.get().load(track.imageUrl).fit().into(ivItemTrackCover)
            val provider = ViewModelProvider(fragmentActivity)
            playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return  TrackHolder(view, fragmentActivity)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(trackList[position])
        val provider = ViewModelProvider(fragmentActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]

        holder.binding.SongLayout.setOnClickListener {
            if (playerPlaylistViewModel.trackList.value == null || playerPlaylistViewModel.trackList.value?.equals(trackList) == false){
                Log.e("IsEq", playerPlaylistViewModel.trackList.value?.equals(trackList).toString())
                playerPlaylistViewModel.trackList.value = trackList
                //playerPlaylistViewModel.trackList.postValue(trackList)
                //Log.e("SIZE", (playerPlaylistViewModel.trackList.value as ArrayList<Track>).size.toString())
                //Log.e("IsNull", position.toString())
            }

            //playerPlaylistViewModel.currentTrackId.value = position
            playerPlaylistViewModel.currentTrackId.value = position
            Log.e("POS ADAPTER", position.toString())
            if ((fragmentActivity as MainActivity).bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
            fragmentActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            fragmentActivity.binding.player.tvSongNameUpMenu.text = trackList[position].name
            fragmentActivity.binding.player.tvBandNameUpMenu.text = trackList[position].band
        }

        (fragmentActivity as MainActivity).binding.player.ibClose.setOnClickListener {
            //playerPlaylistViewModel.songList.value = null
            playerPlaylistViewModel.currentTrackId.value = null
            fragmentActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        playerPlaylistViewModel.isPlaying.observe(fragmentActivity){
            if (it){
                (fragmentActivity as MainActivity).binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_pause)
            }
            else (fragmentActivity as MainActivity).binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_play)
        }

        (fragmentActivity as MainActivity).binding.player.ibPlayUpMenu.setOnClickListener{
            playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value
        }


    }

    override fun getItemCount(): Int {
       return trackList.size
    }


}