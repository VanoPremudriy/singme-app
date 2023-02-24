package com.example.singmeapp.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.AlbumItemBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.ChoosePlaylistForTrackViewModel
import com.squareup.picasso.Picasso

class ChoosePlaylistForTrackAdapter(val fragment: Fragment): RecyclerView.Adapter<ChoosePlaylistForTrackAdapter.ChoosePlaylistForTrackHolder>() {
    var playlistList = ArrayList<Album>()

    class ChoosePlaylistForTrackHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item){
        val binding = AlbumItemBinding.bind(item)
        val activity = fragment.activity
        val provider = ViewModelProvider(fragment)
        val choosePlaylistForTrackViewModel = provider[ChoosePlaylistForTrackViewModel::class.java]

        fun bind(playlist: Album){
            binding.ibItemAlbumMenu.visibility = View.GONE
            binding.tvItemAlbumName.text = playlist.name
            binding.tvItemAlbumBandName.text = fragment.getString(R.string.my_music)
            binding.tvItemYear.text = playlist.year.toString()
            if (playlist.imageUrl != ""){
                Picasso.get().load(playlist.imageUrl).fit().into(binding.ivItemAlbumCover)
            }

            binding.LinearLayoutCompat.setOnClickListener{
                choosePlaylistForTrackViewModel.setTrackToPlaylist(playlist.uuid)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoosePlaylistForTrackHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return ChoosePlaylistForTrackHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ChoosePlaylistForTrackHolder, position: Int) {
        holder.bind(playlistList[position])
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
}