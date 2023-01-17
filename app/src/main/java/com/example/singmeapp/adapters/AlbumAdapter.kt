package com.example.singmeapp.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.AlbumItemBinding
import com.example.singmeapp.fragments.AlbumFragment
import com.example.singmeapp.fragments.LoveAlbumsFragment
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.squareup.picasso.Picasso

class AlbumAdapter(val fragment: Fragment): RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {

    var albumList = ArrayList<Album>()

    class AlbumHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item){
        lateinit var albumViewModel: AlbumViewModel
        val binding = AlbumItemBinding.bind(item)

        fun bind(album: Album) = with(binding){
            tvItemAlbumName.text = album.name
            tvItemAlbumBandName.text = album.band
            tvItemYear.text = album.year.toString()
            if (album.imageUrl != "")
            Picasso.get().load(album.imageUrl).fit().into(ivItemAlbumCover)
            val provider = ViewModelProvider(fragment)
            albumViewModel = provider[AlbumViewModel::class.java]
            LinearLayoutCompat.setOnClickListener{
                if (album.imageUrl != "") {
                    albumViewModel.currentAlbum = album
                    val bundle = Bundle()
                    bundle.putInt("Back", R.id.loveAlbumsFragment)
                    bundle.putSerializable("album", album)
                    NavHostFragment.findNavController(fragment).navigate(R.id.albumFragment, bundle)
                }
                else Toast.makeText(fragment.context, "Загрузка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return  AlbumHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(albumList[position])
    }

    override fun getItemCount(): Int {
     return albumList.size
    }
}