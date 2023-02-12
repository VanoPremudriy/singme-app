package com.example.singmeapp.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
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
        holder.bind(trackList[position])
        val mainActivity = fragmentActivity as MainActivity
        val provider = ViewModelProvider(fragmentActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]

        holder.binding.SongLayout.setOnClickListener {
            if (trackList[position].trackUrl != "") {
                if (playerPlaylistViewModel.trackList.value == null || playerPlaylistViewModel.trackList.value?.equals(
                        trackList
                    ) == false
                ) {
                    playerPlaylistViewModel.trackList.value = trackList

                }

                playerPlaylistViewModel.currentTrackId.value = position
                if (mainActivity.bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
                    fragmentActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                fragmentActivity.binding.player.tvSongNameUpMenu.text = trackList[position].name
                fragmentActivity.binding.player.tvBandNameUpMenu.text = trackList[position].band
            }
            else Toast.makeText(fragmentActivity.applicationContext, "Загрузка", Toast.LENGTH_SHORT).show()
        }

        mainActivity.binding.player.ibClose.setOnClickListener {
            playerPlaylistViewModel.currentTrackId.value = null
            fragmentActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        playerPlaylistViewModel.isPlaying.observe(fragmentActivity){
            if (it){
                mainActivity.binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_pause)
            }
            else mainActivity.binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_play)
        }

        mainActivity.binding.player.ibPlayUpMenu.setOnClickListener{
            playerPlaylistViewModel.currentTrackId.value = playerPlaylistViewModel.currentTrackId.value
        }

        holder.binding.ibItemTrackMenu.setOnClickListener{
            if (trackList[position].isInLove){
                mainActivity.binding.tvAddTrackToLove.visibility = View.GONE
                mainActivity.binding.tvDeleteTrackFromLove.visibility = View.VISIBLE
            }
            else {
                mainActivity.binding.tvAddTrackToLove.visibility = View.VISIBLE
                mainActivity.binding.tvDeleteTrackFromLove.visibility = View.GONE
            }
            mainActivity.binding.trackMenu.visibility = View.VISIBLE
            fragmentActivity.binding.view15.visibility = View.VISIBLE
            fragmentActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED


            mainActivity.binding.tvAddTrackToLove.setOnClickListener {
                playerPlaylistViewModel.addTrackToLove(trackList[position])
                fragmentActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }

            mainActivity.binding.tvDeleteTrackFromLove.setOnClickListener {
                playerPlaylistViewModel.deleteTrackFromLove(trackList[position])
                fragmentActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }

            mainActivity.binding.tvGoToBandProfile.setOnClickListener {
                fragmentActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                val bundle = Bundle()
                bundle.putString("bandUuid", trackList[position].bandUuid)
                NavHostFragment.findNavController(fragment).navigate(R.id.bandFragment, bundle)
            }

            mainActivity.binding.tvGoToAlbum.setOnClickListener {
                fragmentActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                val bundle = Bundle()
                bundle.putString("albumUuid", trackList[position].albumUuid)
                NavHostFragment.findNavController(fragment).navigate(R.id.albumFragment, bundle)
            }
        }






    }

    override fun getItemCount(): Int {
       return trackList.size
    }


}