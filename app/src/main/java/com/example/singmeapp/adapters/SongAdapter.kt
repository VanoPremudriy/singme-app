package com.example.singmeapp.adapters

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.SongItemBinding
import com.example.singmeapp.items.Song
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException


class SongAdapter(val fragmentActivity: AppCompatActivity, val url: String): RecyclerView.Adapter<SongAdapter.SongHolder>() {

    public var songList = ArrayList<Song>()
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel

    class SongHolder(item: View, val songList: ArrayList<Song>, val fragmentActivity: AppCompatActivity,val url: String): RecyclerView.ViewHolder(item){//, View.OnClickListener  {


        lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
        val binding = SongItemBinding.bind(item)

        fun bind(song: Song) = with(binding){
            tvSongName.text = song.name
            tvBandName.text = song.band
            Picasso.get().load(song.imageUrl).fit().into(ivSongCover)
            val provider = ViewModelProvider(fragmentActivity)
            playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return  SongHolder(view, songList, fragmentActivity, url)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        holder.bind(songList[position])
        val provider = ViewModelProvider(fragmentActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]

        holder.binding.SongLayout.setOnClickListener {
            if (playerPlaylistViewModel.songList.value != songList){
                playerPlaylistViewModel.songList.value = songList
            }
            playerPlaylistViewModel.url.value = url
            playerPlaylistViewModel.currentSongId.value = position
            if ((fragmentActivity as MainActivity).bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
            fragmentActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            fragmentActivity.binding.player.tvSongNameUpMenu.text = songList[position].name
            fragmentActivity.binding.player.tvBandNameUpMenu.text = songList[position].band
        }

        (fragmentActivity as MainActivity).binding.player.ibClose.setOnClickListener {
            //playerPlaylistViewModel.songList.value = null
            playerPlaylistViewModel.currentSongId.value = null
            fragmentActivity.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        playerPlaylistViewModel.isPlaying.observe(fragmentActivity){
            if (it){
                (fragmentActivity as MainActivity).binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_pause)
            }
            else (fragmentActivity as MainActivity).binding.player.ibPlayUpMenu.setImageResource(android.R.drawable.ic_media_play)
        }

        (fragmentActivity as MainActivity).binding.player.ibPlayUpMenu.setOnClickListener{
            playerPlaylistViewModel.currentSongId.value = playerPlaylistViewModel.currentSongId.value
        }


    }

    override fun getItemCount(): Int {
       return songList.size
    }


}