package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentDiscographyBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.DiscographyViewModel


class DiscographyFragment : Fragment() {

    lateinit var binding: FragmentDiscographyBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var trackAdapter: TrackAdapter
    lateinit var albumAdapter: AlbumAdapter
    lateinit var singleAdapter: AlbumAdapter
    lateinit var discographyViewModel: DiscographyViewModel
    lateinit var band: Band

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity =  activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)

        band = arguments?.getSerializable("band") as Band
        val provider = ViewModelProvider(this)
        discographyViewModel = provider[DiscographyViewModel::class.java]
        discographyViewModel.getTracks(band)
        discographyViewModel.getAlbums(band)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = band.name

        binding = FragmentDiscographyBinding.inflate(layoutInflater)

        binding.rcViewDiscographyTopTracks.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rcViewDiscographyAlbums.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcViewDiscographySingles.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        trackAdapter = TrackAdapter(fragmentActivity, this)
        albumAdapter = AlbumAdapter(this)
        singleAdapter = AlbumAdapter(this)


        discographyViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.trackList = it as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rcViewDiscographyTopTracks.adapter = trackAdapter
        }

        discographyViewModel.listAlbum.observe(viewLifecycleOwner){
            albumAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcViewDiscographyAlbums.adapter = albumAdapter
        }

        discographyViewModel.listSingle.observe(viewLifecycleOwner){
            singleAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcViewDiscographySingles.adapter = singleAdapter
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
            if (count == 0) {
                activity?.onBackPressed()
            } else {
                findNavController().popBackStack()
            }
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = DiscographyFragment()
    }
}