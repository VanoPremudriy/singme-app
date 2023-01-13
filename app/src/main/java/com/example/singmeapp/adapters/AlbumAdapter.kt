package com.example.singmeapp.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.AlbumItemBinding
import com.example.singmeapp.fragments.LoveAlbumsFragment
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.squareup.picasso.Picasso

class AlbumAdapter(val fragment: LoveAlbumsFragment): RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {

    var albumList = ArrayList<Album>()

    class AlbumHolder(item: View, val fragment: LoveAlbumsFragment): RecyclerView.ViewHolder(item){
        lateinit var albumViewModel: AlbumViewModel
        val binding = AlbumItemBinding.bind(item)
        fun bind(album: Album) = with(binding){
            tvItemAlbumName.text = album.name
            tvItemAlbumBandName.text = album.band
            tvItemYear.text = album.year.toString()
            Picasso.get().load(album.imageUrl).fit().into(ivItemAlbumCover)
            val provider = ViewModelProvider(fragment)
            albumViewModel = provider[AlbumViewModel::class.java]
            LinearLayoutCompat.setOnClickListener{
                albumViewModel.currentAlbum = album
                val bundle = Bundle()
                bundle.putInt("Back", R.id.loveAlbumsFragment)
                bundle.putSerializable("album", album)
                NavHostFragment.findNavController(fragment).navigate(R.id.albumFragment, bundle)
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