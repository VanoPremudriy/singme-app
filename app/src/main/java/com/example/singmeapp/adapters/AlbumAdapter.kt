package com.example.singmeapp.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.AlbumItemBinding
import com.example.singmeapp.fragments.*
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso

class AlbumAdapter(val fragment: Fragment): RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {

    var albumList = ArrayList<Album>()

    class AlbumHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item), View.OnClickListener{
        lateinit var albumViewModel: AlbumViewModel
        val binding = AlbumItemBinding.bind(item)
        val mainActivity = fragment.activity as MainActivity
        val fragmentActivity = fragment.activity as AppCompatActivity
        lateinit var curAlbum: Album

        fun bind(album: Album) = with(binding){
            curAlbum = album
            tvItemAlbumName.text = album.name
            tvItemAlbumBandName.text = album.band
            tvItemYear.text = album.year.toString()
            if (album.imageUrl != "")
            Picasso.get().load(album.imageUrl).fit().into(ivItemAlbumCover)
            val provider = ViewModelProvider(fragment)
            albumViewModel = provider[AlbumViewModel::class.java]
            LinearLayoutCompat.setOnClickListener{
                if (album.imageUrl != "") {

                    val bundle = Bundle()
                    bundle.putSerializable("albumUuid", album.uuid)
                    NavHostFragment.findNavController(fragment).navigate(R.id.albumFragment, bundle)
                }
                else Toast.makeText(fragment.context, "Загрузка", Toast.LENGTH_SHORT).show()
            }



            ibItemAlbumMenu.setOnClickListener {
                if (album.isAuthor) mainActivity.binding.tvDeleteAlbum.visibility = View.VISIBLE
                else mainActivity.binding.tvDeleteAlbum.visibility = View.GONE

                if (album.isInLove){
                    mainActivity.binding.tvDeleteAlbumFromLove.visibility = View.VISIBLE
                    mainActivity.binding.tvAddAlbumInLove.visibility = View.GONE
                } else {
                    mainActivity.binding.tvDeleteAlbumFromLove.visibility = View.GONE
                    mainActivity.binding.tvAddAlbumInLove.visibility = View.VISIBLE
                }
                mainActivity.binding.inAlbumMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED

                mainActivity.binding.tvAddAlbumInLove.setOnClickListener(this@AlbumHolder)
                mainActivity.binding.tvDeleteAlbumFromLove.setOnClickListener(this@AlbumHolder)

                mainActivity.binding.tvGoToBandProfileFromAlbum.visibility = View.GONE
                //mainActivity.binding.tvGoToBandProfileFromAlbum.setOnClickListener(this@AlbumHolder)

            }
        }

        override fun onClick(p0: View?) {
            when (p0?.id){
                mainActivity.binding.tvAddAlbumInLove.id -> {
                    albumViewModel.addAlbumInLove(curAlbum.uuid)
                    mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                }


                mainActivity.binding.tvDeleteAlbumFromLove.id -> {
                    albumViewModel.deleteAlbumFromLove(curAlbum.uuid)
                    mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                }

                /*mainActivity.binding.tvGoToBandProfileFromAlbum.id -> {
                    val bundle = Bundle()
                    bundle.putString("bandUuid", curAlbum.uuid)
                    fragmentActivity.supportActionBar?.show()
                    mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    fragment.findNavController().navigate(R.id.bandFragment, bundle)
                }*/

                mainActivity.binding.tvDeleteAlbum.id -> {
                    albumViewModel.deleteAlbum(curAlbum.uuid)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        when(fragment::class.java){
            DiscographyFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
            CatalogueNewsFragment:: class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
            CataloguePopularFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
            CatalogueFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
            MyLibraryFragment::class.java -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.album_item_2, parent, false)
            }
        }
        return  AlbumHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(albumList[position])


    }

    override fun getItemCount(): Int {
     return albumList.size
    }
}