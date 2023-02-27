package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentCatalogueBinding
import com.example.singmeapp.databinding.FragmentCatalogueNewsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.CatalogueNewsViewModel

class CatalogueNewsFragment : Fragment() {

    lateinit var fragActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueNewsBinding
    lateinit var catalogueNewsViewModel: CatalogueNewsViewModel
    lateinit var newTrackAdapter: TrackAdapter
    lateinit var newAlbumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity

        newTrackAdapter = TrackAdapter(fragActivity, this)
        newAlbumAdapter = AlbumAdapter(this)

        val provider = ViewModelProvider(this)
        catalogueNewsViewModel = provider[CatalogueNewsViewModel::class.java]
        catalogueNewsViewModel.getTracks()
        catalogueNewsViewModel.getAlbums()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragActivity.title = getString(R.string.catalogue)

        binding = FragmentCatalogueNewsBinding.inflate(layoutInflater)

        binding.rvCatalogueNewTracks.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvCatalogueNewAlbums.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        catalogueNewsViewModel.listNewTrack.observe(viewLifecycleOwner){
            newTrackAdapter.trackList = it as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rvCatalogueNewTracks.adapter = newTrackAdapter
        }

        catalogueNewsViewModel.listNewAlbum.observe(viewLifecycleOwner){
            newAlbumAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rvCatalogueNewAlbums.adapter = newAlbumAdapter
        }

        binding.tvAllNewTracks.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("whatIs", "newTracks")
            findNavController().navigate(R.id.catalogueAllFragment, bundle)
        }

        binding.tvAllNewAlbums.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("whatIs", "newAlbums")
            findNavController().navigate(R.id.catalogueAllFragment, bundle)
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = CatalogueNewsFragment()
    }
}