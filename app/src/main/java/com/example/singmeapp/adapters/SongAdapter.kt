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
import com.example.singmeapp.R
import com.example.singmeapp.databinding.SongItemBinding
import com.example.singmeapp.items.Song
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException


class SongAdapter(val fragmentActivity: AppCompatActivity, val url: String): RecyclerView.Adapter<SongAdapter.SongHolder>() {

    public var songList = ArrayList<Song>()

    class SongHolder(item: View, val songList: ArrayList<Song>, val fragmentActivity: AppCompatActivity,val url: String): RecyclerView.ViewHolder(item){//, View.OnClickListener  {


        lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
        var storage = FirebaseStorage.getInstance()
        val binding = SongItemBinding.bind(item)

        fun bind(song: Song) = with(binding){
            tvSongName.text = song.name
            tvBandName.text = song.band


            try {
                val localFile: File = File.createTempFile("default_picture", "jpg")
                storage.getReferenceFromUrl(song.imageUrl).getFile(localFile)
                    .addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        ivSongCover.setImageBitmap(bitmap)
                    }.addOnFailureListener { }
            } catch (e: IOException) {
            }

           val provider = ViewModelProvider(fragmentActivity)
            playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]

            SongLayout.setOnClickListener {
                if (playerPlaylistViewModel.songList.value != songList){
                    playerPlaylistViewModel.songList.value = songList
                    playerPlaylistViewModel.url.value = url
                }
                Log.d("Adapter", "check " + tvSongName.text + " " + playerPlaylistViewModel.url.value )
                tvBandName.text = "fds"

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return  SongHolder(view, songList, fragmentActivity, url)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        holder.bind(songList[position])
    }

    override fun getItemCount(): Int {
       return songList.size
    }


}