package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.singmeapp.adapters.BandAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentCatalogueBinding
import com.example.singmeapp.databinding.FragmentCatalogueNewsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.CatalogueNewsViewModel

class CatalogueNewsFragment : Fragment() {

    lateinit var fragActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueNewsBinding
    lateinit var catalogueNewsViewModel: CatalogueNewsViewModel
    lateinit var newTrackAdapter: TrackAdapter
    lateinit var newAlbumAdapter: AlbumAdapter
    lateinit var newBandAdapter: BandAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity

        newTrackAdapter = TrackAdapter(fragActivity, this)
        newAlbumAdapter = AlbumAdapter(this)
        newBandAdapter = BandAdapter(this)

        val provider = ViewModelProvider(this)
        catalogueNewsViewModel = provider[CatalogueNewsViewModel::class.java]
        catalogueNewsViewModel.getTracks()
        catalogueNewsViewModel.getAlbums()
        catalogueNewsViewModel.getBands()

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
        binding.rvCatalogueNewBands.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        catalogueNewsViewModel.listNewTrack.observe(viewLifecycleOwner){
            newTrackAdapter.trackList.clear()
            newTrackAdapter.trackList.addAll(it as ArrayList<Track>) /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rvCatalogueNewTracks.adapter = newTrackAdapter
        }

        catalogueNewsViewModel.listNewAlbum.observe(viewLifecycleOwner){
            newAlbumAdapter.albumList.clear()
            newAlbumAdapter.albumList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rvCatalogueNewAlbums.adapter = newAlbumAdapter
        }

        catalogueNewsViewModel.listNewBand.observe(viewLifecycleOwner){
            newBandAdapter.bandList.clear()
            newBandAdapter.bandList.addAll(it as ArrayList<Band>)
            binding.rvCatalogueNewBands.adapter = newBandAdapter
        }

        catalogueNewsViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["track"] == true && it["trackImage"] == true && it["albumImage"] == true && it["bandImage"] == true && it["bandBack"] == true){
                binding.catalogueNewsProgressLayout.visibility = View.GONE
            }
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
        
        binding.tvAllNewBands.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("whatIs", "newBands")
            findNavController().navigate(R.id.catalogueAllFragment, bundle)
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = CatalogueNewsFragment()
    }
}