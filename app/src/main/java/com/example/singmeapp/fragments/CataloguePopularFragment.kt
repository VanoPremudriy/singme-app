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
import com.example.singmeapp.databinding.FragmentCatalogueNewsBinding
import com.example.singmeapp.databinding.FragmentCataloguePopularBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.CatalogueNewsViewModel
import com.example.singmeapp.viewmodels.CataloguePopularViewModel


class CataloguePopularFragment : Fragment() {

    lateinit var binding: FragmentCataloguePopularBinding
    lateinit var fragActivity: AppCompatActivity
    lateinit var cataloguePopularViewModel: CataloguePopularViewModel
    lateinit var newTrackAdapter: TrackAdapter
    lateinit var newAlbumAdapter: AlbumAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity

        newTrackAdapter = TrackAdapter(fragActivity, this)
        newAlbumAdapter = AlbumAdapter(this)

        val provider = ViewModelProvider(this)
        cataloguePopularViewModel = provider[CataloguePopularViewModel::class.java]
        cataloguePopularViewModel.getTracks()
        cataloguePopularViewModel.getAlbums()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        fragActivity.title = getString(R.string.catalogue)
        binding = FragmentCataloguePopularBinding.inflate(layoutInflater)


        binding.rvCataloguePopularTracks.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvCataloguePopularAlbums.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        cataloguePopularViewModel.listPopularTrack.observe(viewLifecycleOwner){
            newTrackAdapter.trackList = it as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rvCataloguePopularTracks.adapter = newTrackAdapter
        }

        cataloguePopularViewModel.listPopularAlbum.observe(viewLifecycleOwner){
            newAlbumAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rvCataloguePopularAlbums.adapter = newAlbumAdapter
        }

        binding.tvAllPopularTracks.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("whatIs", "popularTracks")
            findNavController().navigate(R.id.catalogueAllFragment, bundle)
        }
        binding.tvAllPopularAlbums.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("whatIs", "popularAlbums")
            findNavController().navigate(R.id.catalogueAllFragment, bundle)
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = CataloguePopularFragment()
    }
}