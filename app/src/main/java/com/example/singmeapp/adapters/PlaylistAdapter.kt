package com.example.singmeapp.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.AlbumItemBinding
import com.example.singmeapp.fragments.DiscographyFragment
import com.example.singmeapp.fragments.MyLibraryFragment
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso

class PlaylistAdapter(val fragment: Fragment): RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {
    var playlistList = ArrayList<Album>()

    class PlaylistHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item), View.OnClickListener{
        val binding = AlbumItemBinding.bind(item)
        val mainActivity = fragment.activity as MainActivity
        val fragmentActivity = fragment.activity as AppCompatActivity
        lateinit var curPlaylist: Album
        val provider = ViewModelProvider(fragment)
        val playlistViewModel = provider[PlaylistViewModel::class.java]


        fun bind(playlist: Album){
            curPlaylist = playlist
            binding.tvItemAlbumName.text = playlist.name
            if (playlist.isAuthor){
                binding.tvItemAlbumBandName.text = fragment.getString(R.string.my_music)
            } else binding.tvItemAlbumBandName.text = playlist.band
            /*binding.tvItemAlbumBandName.text = {
                if (playlist.isAuthor) fragment.getString(R.string.my_music)
                else playlist.band
            }.toString()*/
            binding.tvItemYear.text = playlist.year.toString()
            if (playlist.imageUrl != "")
                Picasso.get().load(playlist.imageUrl).fit().into(binding.ivItemAlbumCover)

            binding.LinearLayoutCompat.setOnClickListener{

                    val bundle = Bundle()
                    bundle.putString("playlistUuid", playlist.uuid)
                    NavHostFragment.findNavController(fragment).navigate(R.id.playlistFragment, bundle)

                //else Toast.makeText(fragment.context, "Загрузка", Toast.LENGTH_SHORT).show()
            }

            binding.ibItemAlbumMenu.setOnClickListener {
                    menu(playlist)
            }

            mainActivity.binding.tvDeletePlaylist.setOnClickListener{
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                playlistViewModel.deletePlaylist(playlist.uuid)
            }

        }

        fun menu(playlist: Album){
            mainActivity.binding.tvChangePlaylist.visibility = View.GONE
            if (playlist.isAuthor){
                mainActivity.binding.tvDeletePlaylist.visibility = View.VISIBLE
                mainActivity.binding.tvAddPlaylisToLove.visibility = View.GONE
                mainActivity.binding.tvDeletePlaylistFromLove.visibility = View.GONE
            } else {
                mainActivity.binding.tvDeletePlaylist.visibility = View.GONE
                if (playlist.isInLove){
                    mainActivity.binding.tvDeletePlaylistFromLove.visibility = View.VISIBLE
                    mainActivity.binding.tvAddPlaylisToLove.visibility = View.GONE
                } else {
                    mainActivity.binding.tvDeletePlaylistFromLove.visibility = View.GONE
                    mainActivity.binding.tvAddPlaylisToLove.visibility = View.VISIBLE
                }
            }


            mainActivity.binding.playlistMenu.visibility = View.VISIBLE
            mainActivity.binding.view15.visibility = View.VISIBLE
            mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
        }

        override fun onClick(p0: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        when(fragment::class.java){
            DiscographyFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
            MyLibraryFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
        }
        return PlaylistHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(playlistList[position])
    }

    override fun getItemCount(): Int {
     return playlistList.size
    }

}